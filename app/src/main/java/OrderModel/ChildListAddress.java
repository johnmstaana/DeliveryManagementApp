package OrderModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by johnm on 31/08/2016.
 */
public class ChildListAddress {
    private ParentListHeader _parentListHeader;
    private HashMap<String,List<String>> childList;
    private ArrayList<String> address;

    public ChildListAddress(HashMap<String, List<String>>childList) {
/*        this._parentListHeader = _parentListHeader;
        this.address = address;*/
        this.childList = childList;
/*        inputKeyVal();*/
    }


    public HashMap<String, List<String>> getChildList() {
        return childList;
    }

    public void setChildList(HashMap<String, List<String>> childList) {
        this.childList = childList;
    }
}
