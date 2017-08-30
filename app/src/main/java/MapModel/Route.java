package MapModel;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by johnm on 30/08/2016.
 */
public class Route {

    public LatLng getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(LatLng startLocation) {
        this.startLocation = startLocation;
    }

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public EndAddress getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(EndAddress endAddress) {
        this.endAddress = endAddress;
    }

    public LatLng getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(LatLng endLocation) {
        this.endLocation = endLocation;
    }

    public List<LatLng> getPoints() {
        return points;
    }

    public void setPoints(List<LatLng> points) {
        this.points = points;
    }

    public StartAddress getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(StartAddress startAddress) {
        this.startAddress = startAddress;
    }
    private Duration duration;
    private EndAddress endAddress;
    private LatLng endLocation;
    private StartAddress startAddress;
    private LatLng startLocation;
    private List<LatLng> points;
    private Distance distance;
}
