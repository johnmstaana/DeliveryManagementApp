package MapFunction;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by johnm on 30/08/2016.
 */
public class Map_Geocoder {
    public static final int maxResult = 1;

    public static LatLng reverseGeocoding(Context context, String locationName) {
        if (!Geocoder.isPresent()) {
            Log.w("zebia", "Geocoder implementation not present !");
        }
        Geocoder geoCoder = new Geocoder(context, Locale.getDefault());

        try {
            List<Address> addresses = geoCoder.getFromLocationName(locationName, maxResult);
            int tentatives = 0;
            while (addresses.size() == 0 && (tentatives < 10)) {
                addresses = geoCoder.getFromLocationName("<address goes here>", 1);
                tentatives++;
            }


            if (addresses.size() > 0) {
                Log.d("zebia", "reverse Map_Geocoder : locationName " + locationName + "Latitude " + addresses.get(0).getLatitude());
                return new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
            } else {
                //use http api
            }

        } catch (IOException e) {
            Log.d(Map_Geocoder.class.getName(), "not possible finding LatLng for Address : " + locationName);
        }
        return null;
    }
}
