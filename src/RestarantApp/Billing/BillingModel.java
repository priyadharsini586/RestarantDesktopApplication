package RestarantApp.Billing;

public class BillingModel {

    int s_no;
    String item_name,quantity,rate,amount;

    public BillingModel(int s_no,String item_name,String quantity,String rate,String amount)
    {
        this.s_no = s_no;
        this.item_name = item_name;
        this.quantity = quantity;
        this.rate = rate;
         this.amount = amount;
    }
    public int getS_no() {
        return s_no;
    }

    public void setS_no(int s_no) {
        this.s_no = s_no;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
