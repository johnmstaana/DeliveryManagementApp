package MapModel;

/**
 * Created by johnm on 30/08/2016.
 */
public class EndAddress {
    public EndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    @Override
    public String toString() {
        return endAddress;
    }

    public String endAddress;
}
