package ApplicationFragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.johnm.DeliveryManagementMain.R;

import java.io.IOException;
import java.util.ArrayList;

import OrderModel.CheckList;
import OrderModel.ListViewAdapter;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by johnm on 1/09/2016.
 */
public class OrderCheckList_Fragment extends ListFragment implements View.OnClickListener{
    ArrayList<String> childAddress = new ArrayList<>();
    ArrayList<CheckList> checkList_ = new ArrayList<>();
    private String orderID = "";
    private String status = "";

    private ListViewAdapter lva;
    private ListView lv;

    private onClickSelectedListener mCallBack;


    public interface onClickSelectedListener {
        public void onMapBackSelected(View view, ArrayList<CheckList>checklits);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallBack = (onClickSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnListSelectedListener");
        }
    }
    public void onClick(View view) {
        mCallBack.onMapBackSelected(view,checkList_); ;
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.checklist_adapter_layout, container, false);
        lva = new ListViewAdapter(getActivity(),R.layout.checklist_layout,checkList_);
        setListAdapter(lva);
        for(CheckList t: checkList_){
            System.out.println("is completed"+t.isComplete());
        }
        Button backBtn = (Button) view.findViewById(R.id.btn_map);
        backBtn.setOnClickListener(this);
        return view;
    }

    class UpdateCompletedAddress extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("Order_ID",orderID)
                    .add("Status",status)
                    .build();

            Request request = new Request.Builder()
                    .url("http://172.28.109.90/UpdateData.php")
                    .post(formBody)
                    .build();

            // Get the response from the database
            try {
                Response response = client.newCall(request).execute();
                String temp = response.body().string();

                if (temp == null) {
                    Toast.makeText(getActivity(), "Database connection error", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(getActivity(), "connectionSuccess", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }

    }


    public void setCheckList(ArrayList<CheckList> _checkList){
        checkList_ = _checkList;
}
    public void setChildAddress(ArrayList<String> childAddresses) {
        childAddress = childAddresses;
    }

}
