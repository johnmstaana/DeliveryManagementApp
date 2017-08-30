package com.example.johnm.DeliveryManagementMain;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.ArrayList;

import ApplicationFragments.Map_Fragment;
import ApplicationFragments.OrderCheckList_Fragment;
import ApplicationFragments.OrderList_Fragment;
import OrderModel.CheckList;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class MainActivity extends AppCompatActivity implements Map_Fragment.onClickSelectedListener, OnMapReadyCallback, OrderList_Fragment.onConfirmationListener, OrderCheckList_Fragment.onClickSelectedListener {

    private GoogleMap googleMap;
    private static Map_Fragment maps;
    private ArrayList<String> getMapChildAddress = new ArrayList<>();
    private ArrayList<CheckList> getCheckListOrder = new ArrayList<>();
    private ArrayList<CheckList> orderChecklist = new ArrayList<>();
    private ArrayList<String> uncompletedAddress = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.frag_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            OrderList_Fragment orderListFragment = new OrderList_Fragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frag_container, orderListFragment, "TAG");
            transaction.addToBackStack("TAG");
            transaction.commit();
        }

    }


    @Override
    public void onClickSelected(View view) {
        switch (view.getId()) {

            case R.id.btn_path:
                boolean orderCompleted = false;
                if (maps.isJobAccepted()) {
                    for (CheckList check : orderChecklist)
                        if (!check.isComplete()) {
                            uncompletedAddress.add(check.getAddress());

                        }  else {

                        }
                    if(orderCompleted){
                    }else {
                        maps.setupChildAddresses(uncompletedAddress);
                        maps.sendRequest(uncompletedAddress);
                    }

                    uncompletedAddress.clear();
                } else {
                    maps.sendRequest(getMapChildAddress);
                }
                break;
            case R.id.btn_order:
                OrderCheckList_Fragment orderCheckListFragment = (OrderCheckList_Fragment) getSupportFragmentManager().findFragmentById(R.id.checkList);

                if (maps.isJobAccepted() == true) {
                    if (orderCheckListFragment != null) {

                    } else {
                        OrderCheckList_Fragment newOrderCheckListFragment = new OrderCheckList_Fragment();
                        newOrderCheckListFragment.setChildAddress(getMapChildAddress);
                        newOrderCheckListFragment.setCheckList(getCheckListOrder);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frag_container, newOrderCheckListFragment);
                        transaction.addToBackStack("ToComplete");
                        transaction.commit();
                    }
                } else {

                    OrderList_Fragment orderListFrag = new OrderList_Fragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frag_container, orderListFrag);
                    transaction.addToBackStack(null);
                    transaction.commit();

                }
                break;
            case R.id.btn_accept:
                maps.isAcceptedBtnClicked(true);

                break;


            default:
                System.out.println("nothing");
        }
    }


    @Override
    public void onConfirmation(int accept, ArrayList<String> _mapChildAddress, ArrayList<CheckList> orderID) {
        if (!_mapChildAddress.isEmpty())
            getMapChildAddress = _mapChildAddress;
        getCheckListOrder = orderID;


        maps = (Map_Fragment) getSupportFragmentManager().findFragmentById(R.id.map);
        maps = new Map_Fragment();
        maps.setupChildAddresses(getMapChildAddress);
        if (accept == BUTTON_POSITIVE) {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frag_container, maps, "TAG_MAP");
            transaction.addToBackStack(null);
            transaction.commit();

        }
    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        googleMap = mMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);


    }


    @Override
    public void onMapBackSelected(View view, ArrayList<CheckList> unCompletedCheckList) {

        getSupportFragmentManager().popBackStack();
        orderChecklist = unCompletedCheckList;
    }
}
