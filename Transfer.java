import java.io.Serializable;
import java.util.Date;

public class Transfer implements FinancialItem, Serializable {
    private float amount;
    private Date date;
    private Account sourceAccount;
    private Account destinationAccount;
    
    public Transfer(float amount, Date date, Account sourceAccount, Account destinationAccount) {
        this.amount = amount;
        this.date = date;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
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
        return "Transfer from " + sourceAccount.getName() + " to " + destinationAccount.getName();
    }
    
    @Override
    public Category getCategory() {
        return null;
    }
    
    public Account getSourceAccount() {
        return sourceAccount;
    }
    
    public Account getDestinationAccount() {
        return destinationAccount;
    }
    
    @Override
    public String toString() {
        return String.format("Transfer: $%.2f from %s to %s on %s", 
                             amount, sourceAccount.getName(), destinationAccount.getName(), date);
    }
}