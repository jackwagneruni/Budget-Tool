import java.io.Serializable;
import java.util.Date;

public class Income implements FinancialItem, Serializable {
    private float amount;
    private Date date;
    private String source;
    private String recurrence;
    private Category category;
    
    public Income(float amount, Date date, String source, String recurrence) {
        this.amount = amount;
        this.date = date;
        this.source = source;
        this.recurrence = recurrence;
    }
    
    @Override
    public float getAmount() {
        return amount;
    }
    
    @Override
    public Date getDate() {
        return date;
    }
    
    @Override
    public String getDescription() {
        return source + " (" + recurrence + ")";
    }
    
    @Override
    public Category getCategory() {
        return category;
    }
    
    public String getSource() {
        return source;
    }
    
    public String getRecurrence() {
        return recurrence;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    @Override
    public String toString() {
        return String.format("Income: $%.2f from %s on %s", amount, source, date);
    }
}