package ApplicationFragments;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.johnm.DeliveryManagementMain.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import MapFunction.MapLocationProvider;
import MapFunction.Map_AddressJsonParser;
import MapFunction.Map_Geocoder;
import MapInterface.Map_AddressJsonListener;
import MapModel.Route;

import static android.content.ContentValues.TAG;

/**
 * Map fragments that will be the main of the program holding all the processing of the location
 * and showing it to the deliery person{@link Fragment} subclass.
 */
public class Map_Fragment extends Fragment implements OnMapReadyCallback, Map_AddressJsonListener, View.OnClickListener, MapLocationProvider.LocationCallback {
    // map for the application
    private GoogleMap mMap;

    //Button variables in the second panel
    private Button btnFindPath;
    private Button btnAccept;
    private Button btnOrder;

    //Current location or position of the person using the app
    private LatLng currentLoc;

    //Arraylist that will hold all the information for the map and will be reset each time find path is click
    private ArrayList<LatLng> markerPoints = new ArrayList<>();
    private List<Marker> originMarkers;
    private List<Marker> destinationMarkers;
    private List<Marker> waypointMarkers;

    //Polylines that will connect all the markers
    private List<Polyline> polylinePaths = new ArrayList<>();

    //Convert the string addresses into LatLang
    private Map_Geocoder _reverseGeo = new Map_Geocoder();
    private ProgressDialog progressDialog;

    //Arraylist that will hold the given address of the restaurants and customers
    private ArrayList<String> childAddresses = new ArrayList<>();

    // Distance and duration of the entire process of delivery inside the map
    private int _duration;
    private int _distance;

    //Hold the current view
    private static View view;

    //Checks if the job is accepted if true then disable the btnAccept
    private boolean jobAccepted;

    //Route that will be used to pass the route taken from the argument in the interface DirectionfinderSuccess
    private ArrayList<Route> route = new ArrayList<>();

    // Listener for the interface
    private onClickSelectedListener mCallBack;

    private MapLocationProvider mapLocationProvider;


/*
    public static Map_Fragment newInstance() {
        Map_Fragment fragment = new Map_Fragment();
        return fragment;
    }
*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        }
        try {
            view = inflater.inflate(R.layout.map_fragment_layout, container, false);
        } catch (InflateException e) {

        }

        setupMap();
        mapLocationProvider = new MapLocationProvider(getContext(),this);

        //Listener for all the buttons in map fragment
        btnFindPath = (Button) view.findViewById(R.id.btn_path);
        btnFindPath.setOnClickListener(this);
        btnAccept = (Button) view.findViewById(R.id.btn_accept);
        btnAccept.setOnClickListener(this);
        btnOrder = (Button) view.findViewById(R.id.btn_order);
        btnOrder.setOnClickListener(this);
        return view;
    }


    /**
     * Setup map that will initialize the google map, call if map is not running
     */
    public void setupMap() {

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * This will invoke the Map_AddressJsonParser to send all data required for a google map api request.
     * It will return all the values needed for the output on the map such as details for the ROUTE class
     */
    public void sendRequest(ArrayList<String>addresses) {
        convertToGeocode(addresses);
        markerPoints.add(1,currentLoc);
        try {
            new Map_AddressJsonParser(this, markerPoints).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        markerPoints.clear();
    }


    /**
     * Call this function when the map is ready and available to be used
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        LatLng auckland = new LatLng(-36.8484483,174.7726317);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(auckland,13));
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);


    }

    /**
     * Resets all the data from all of the arraylist that is connected to the map view
     * eg, Markers, Polylines, Duration, Distanse
     */
    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(getActivity(), "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();


            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();

            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();

            }
        }
        if (waypointMarkers != null) {
            for (Marker marker : waypointMarkers) {
                marker.remove();

            }
        }
        if (_duration != 0) {
            _duration = 0;

        }
        if (_distance != 0) {
            _distance = 0;

        }
        if (route != null) {
            route.clear();

        }
    }


    /**
     * Interface that will do most of the assigning of markers, polylines. Data is taken from Map_AddressJsonParser that implements an http call that decode a json reply from google
     * Routes will be stored according to their destinations
     *
     * @param routes
     */
    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {

        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();
        waypointMarkers = new ArrayList<>();


        for (int i = 0; i < routes.size(); i++) {
            route.add(i, routes.get(i));

            if (i != 0) {
                waypointMarkers.add(mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                        .title(route.get(i).getStartAddress().toString())
                        .position(route.get(i).getStartLocation())));
            } else {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.get(0).getStartLocation(), 14));
                originMarkers.add(mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .title(route.get(0).getStartAddress().toString())
                        .position(route.get(0).getStartLocation())));

            }
            if (i != routes.size() - 1) {
                waypointMarkers.add(mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                        .title(route.get(i).getEndAddress().toString())
                        .position(route.get(i).getEndLocation())));
            } else {
                destinationMarkers.add(mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .title(route.get(route.size() - 1).getEndAddress().toString())
                        .position(route.get(route.size() - 1).getEndLocation())));
            }
        }
        for (Route routex : routes) {
            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(3);
            _distance += routex.getDistance().value;
            _duration += routex.getDuration().value;

            for (int j = 0; j < routex.getPoints().size(); j++)
                polylineOptions.add(routex.getPoints().get(j));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
        ((TextView) view.findViewById(R.id.tvDuration)).setText(convertSecToMin(_duration) + " mins");
        ((TextView) view.findViewById(R.id.tvDistance)).setText(convertToKm(_distance) + " km");

    }


    @Override
    /**
     * All the interactions between the buttons on Map_Fragment will be passed in the interface and declared in the main activity
     *
     */
    public void onClick(View view) {
        mCallBack.onClickSelected(view);
    }

    /**
     * Method that will convert meter to kilometer that will be use to display in map fragment
     *
     * @param meter reply we get from json that is in meter
     * @return
     */
    public String convertToKm(int meter) {
        double kilometer = 0.001;
        kilometer = kilometer * (double) meter;
        return String.format("%.2f", kilometer);
    }

    /**
     * Method that will convert seconds to minutes that will be use to be display in map fragment
     *
     * @param seconds reply we get from json that is in seconds
     * @return
     */
    public int convertSecToMin(int seconds) {
        int minutes = 0;
        minutes = (seconds % 3600) / 60;
        return minutes;
    }

    @Override
    public void onResume() {
        super.onResume();
        setupMap();
        mapLocationProvider.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapLocationProvider.disconnect();
    }

    @Override
    public void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        currentLoc = new LatLng(currentLatitude, currentLongitude);

        //mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Current Location"));
        MarkerOptions options = new MarkerOptions()
                .position(currentLoc);
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLoc));

    }

    /**
     * Method that will be used to communicate with fragments via activity
     * implements an interface that will pass the button that was pressed on the current view
     */
    public interface onClickSelectedListener {
        public void onClickSelected(View view);
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

    /**
     * Setup the childaddresses for map to be proces
     *
     * @param _childaddresses contains the address of the restaurants that customer made orders
     */
    public void setupChildAddresses(ArrayList<String> _childaddresses) {
        for (int i = 0; i < _childaddresses.size(); i++) {
            if (!childAddresses.contains(_childaddresses.get(i))) {
                childAddresses.add(_childaddresses.get(i));
            }
        }
    }

    /**
     * Convert the childaddress strings into Latitude and Longtitude and check if the latitude already exist in the arraylist
     *
     * @param _childAddress
     */
    public void convertToGeocode(ArrayList<String> _childAddress) {

        for (int i = 0; i < _childAddress.size(); i++) {
            if (!markerPoints.contains(_childAddress.get(i))) {
                markerPoints.add(_reverseGeo.reverseGeocoding(getActivity(), _childAddress.get(i)));

            }


        }
    }

    /**
     * Check if the job is accepted by the delivery personnel,
     *
     * @param isAccepted
     */
    public void isAcceptedBtnClicked(boolean isAccepted) {
        jobAccepted = isAccepted;
        if (jobAccepted == true) {
            btnAccept.setEnabled(false);
        }

    }

    /**
     * Return if the job is accepted by DeliveryPerson or not
     *
     * @return
     */
    public boolean isJobAccepted() {
        return jobAccepted;
    }


    /**
     * Destroy the view of the fragment to resolve duplication and empty view
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Fragment fragment = (getActivity().getSupportFragmentManager().findFragmentById(R.id.map));
        if (fragment != null) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    public Map_Fragment() {
        // Required empty public constructor
    }

}

