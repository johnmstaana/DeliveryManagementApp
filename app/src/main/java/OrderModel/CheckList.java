package OrderModel;

/**
 * Created by johnm on 18/10/2016.
 */

public class CheckList {
    private String address;
    private boolean isComplete;
    private int isPeople;
    private String orderId;



    public CheckList(String address, String order_Status, int isPeople, String orderId) {
        this.address = address;
        this.isPeople = isPeople;
        this.order_Status = order_Status;
        this.orderId = orderId;

        checkStatus();
    }

    public String getOrder_Status() {
        return order_Status;
    }

    public void setOrder_Status(String order_Status) {
        this.order_Status = order_Status;
    }

    private String order_Status;


    public int getIsPeople() {
        return isPeople;
    }

    public void setIsPeople(int isPeople) {
        this.isPeople = isPeople;
    }


    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public boolean checkStatus() {
        if(order_Status.equals("Complete"))
            isComplete = true;
        else
            isComplete = false;
        return isComplete;
    }
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
