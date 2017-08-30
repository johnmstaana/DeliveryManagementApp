package OrderModel;

import java.util.ArrayList;

/**
 * Created by johnm on 31/08/2016.
 */
public class ParentListHeader {
    private boolean checked;
    private ArrayList<String> headerContainer;

    public ParentListHeader(ArrayList<String> headerContainer) {
        this.headerContainer = headerContainer;
    }

    public ArrayList<String> getHeaderContainer() {
        return headerContainer;
    }

    public void setHeaderContainer(ArrayList<String> headerContainer) {
        this.headerContainer = headerContainer;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

}

