// typeDetail (Income/Expense)
// category (Rent,food,travel,entertainment,etc., for expense  and salary/business/other for income)
// amount(value in $)
// date(detail entry date and year)

public class Transactions {
    private  String typeDetail;
    private String category;
    private double amount;
    private String date; //MM-YYYY format

    public Transactions(String typeDetail, String category, double amount, String date){
        this.typeDetail = typeDetail;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public String getTypeDetail(){
        return  typeDetail;
    }

    public String getDate() {
        return date;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTypeDetail(String typeDetail) {
        this.typeDetail = typeDetail;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return typeDetail + "," + category + "," + amount + "," + date + ",";
    }
    
}
