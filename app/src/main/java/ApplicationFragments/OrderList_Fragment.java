package ApplicationFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.johnm.DeliveryManagementMain.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import OrderModel.CheckList;
import OrderModel.ChildListAddress;
import OrderModel.ExpandableListAdapter;
import OrderModel.ParentListHeader;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by johnm on 31/08/2016.
 */
public class OrderList_Fragment extends Fragment {
    private onConfirmationListener mCallBack;

    public interface onConfirmationListener {
        public void onConfirmation(int accept, ArrayList<String> mapChildAddress, ArrayList<CheckList>_order_ID);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallBack = (onConfirmationListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnListSelectedListener");
        }
    }

    //List Adaptor for the first panel of the program
    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;

    CheckList checkListtemp;

    private View v;
    private String getParentID = "";

    //Hashmap that will contain the unique id Order_ID with the cust_id as a key
    private HashMap<String, ArrayList<CheckList>> orderID = new HashMap<>();

    // Arraylist that will hold all the ORDER ID based on which is the cust_id picked
    private ArrayList<CheckList> putOrderID = new ArrayList<>();

    //Hashmap set that will contain the parent(custId) and children (restaurant addresses)
    private HashMap<String, List<String>> orders = new HashMap<>();

    // Temporary holder for the parent and child addresses this will be used to pass the addresses into another fragment, if the specific parent is selected then get the children of that parents
    // and store them on this variables
    private ArrayList<String> childAdr = new ArrayList<>();
    private ArrayList<String> parentHdr = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.order_list_layout, null);
        super.onActivityCreated(savedInstanceState);
        new RetrieveOrders().execute();
        return v;
    }

    public void clearArrays() {
        if (!orders.isEmpty()) {
            orders.clear();
        }
        if (childAdr != null) {
            childAdr.clear();
        }
        if (parentHdr != null) {
            parentHdr.clear();
        }
    }

    /**
     * Class that will perform the connectiong between the database and the android.
     * This class will retrieve all the data that is currently in qeue order for delivery to select
     *
     * @param @parentListHeader Store the customer_id that will be later used as the parent header in expandableList View
     * @param @childListAddress store all the restaurant addresses that correlate with the customer order
     */
    class RetrieveOrders extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;

        private ParentListHeader parentListHeader;
        private ChildListAddress childListAddress;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Connecting to server");
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            // Clear the addresses from the previous query
            clearArrays();
            // Prepare the connection between the database and the application
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http:/172.28.109.90/ReadData.php")
                    .build();

            try {
                // Get the response from the database
                Response response = client.newCall(request).execute();
                String temp = response.body().string();
                if (temp == null) {
                    Toast.makeText(getActivity(), "Database connection error", Toast.LENGTH_SHORT).show();
                } else {
                    try {

                        //Decode the JSON response from the database and allocate the informations to child and the parent header
                        JSONObject jsonRes = new JSONObject(temp);
                        JSONArray jsonArray = jsonRes.getJSONArray("orders");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jo = jsonArray.getJSONObject(i);
                            String custID = jo.getString("CustID");
                            String restAddr = jo.getString("Address");
                            String custAddr = jo.getString("CustAddress");
                            String order_ID = jo.getString("OrderID");
                            String status = jo.getString("Status");

                            //remove the duplicate from having multiple customerID ordering from different restaurant
                            if (orders.containsKey(custID)) {
                                orders.get(custID).add(restAddr);
                            } else {
                                ArrayList<String> holdAddresses = new ArrayList<>();
                                holdAddresses.add(custAddr);
                                holdAddresses.add(restAddr);
                                orders.put(custID, holdAddresses);
                            }

                            if (orderID.containsKey(custID)) {
                                orderID.get(custID).add(new CheckList(restAddr,status,i,order_ID));

                            } else {
                                ArrayList<CheckList> holdOrderID = new ArrayList<>();
                                holdOrderID.add(new CheckList(custAddr,"Customer",0,order_ID) );
                                holdOrderID.add(new CheckList(restAddr,status,i,order_ID) );
                                orderID.put(custID, holdOrderID);
                            }
                        }

                        //Get the customerID in the hashset and store them in parentheader
                        for (String key : orders.keySet()) {
                            parentHdr.add(key);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {


            super.onPostExecute(aVoid);

            parentListHeader = new ParentListHeader(parentHdr);
            childListAddress = new ChildListAddress(orders);

            //Expandable listview for the OrderList_Fragment
            expListView = (ExpandableListView) v.findViewById(R.id.lvExp);


            // Set custom adapter
            listAdapter = new ExpandableListAdapter(getActivity(), parentHdr, orders);

            //Set Adapter to custom ExpandableListAdapter
            expListView.setAdapter(listAdapter);

            //Listener for long clicks on the expandable listview that will initialize the second fragment Map_Fragment.
            //This will pass the childaddress to Map_Fragment for processing
            expListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    getParentID = parentListHeader.getHeaderContainer().get(position);

                    childAdr.addAll(childListAddress.getChildList().get(getParentID));


                    putOrderID.addAll(orderID.get(getParentID));



                    new AlertDialog.Builder(getActivity())
                            .setTitle("Map View")
                            .setMessage("View Addresses In The Map?")
                            .setIcon(android.R.drawable.ic_dialog_map)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //Send arguments to the interface for Map_Fragment to recieve
                                    mCallBack.onConfirmation(whichButton, childAdr, putOrderID);

                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                    return false;
                }
            });
            progressDialog.dismiss();

        }
    }

}