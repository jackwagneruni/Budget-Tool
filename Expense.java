import java.io.Serializable;
import java.util.Date;

public class Expense implements FinancialItem, Serializable {
    private float amount;
    private Date date;
    private String vendor;
    private Category category;
    private String notes;
    
    public Expense(float amount, Date date, String vendor, Category category, String notes) {
        this.amount = amount;
        this.date = date;
        this.vendor = vendor;
        this.category = category;
        this.notes = notes;
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
        return vendor + (notes != null && !notes.isEmpty() ? " - " + notes : "");
    }
    
    @Override
    public Category getCategory() {
        return category;
    }
    
    public String getVendor() {
        return vendor;
    }
    
    public String getNotes() {
        return notes;
    }
    
    @Override
    public String toString() {
        return String.format("Expense: $%.2f to %s for %s on %s", 
                             amount, vendor, category.getName(), date);
    }
}