package MapInterface;

import java.util.List;

import MapModel.Route;

/**
 * Created by johnm on 30/08/2016.
 */
public interface Map_AddressJsonListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
