import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Account implements Serializable {
    private String name;
    private String type;
    private float balance;
    private List<FinancialItem> transactions;
    
    public Account(String name, String type) {
        this.name = name;
        this.type = type;
        this.balance = 0.0f;
        this.transactions = new ArrayList<>();
    }
    
    public Account(String name, String type, float initialBalance) {
        this(name, type);
        this.balance = initialBalance;
    }
    
    public void addTransaction(FinancialItem transaction) {
        transactions.add(transaction);
        if (transaction instanceof Income) {
            balance += transaction.getAmount();
        } else if (transaction instanceof Expense) {
            balance -= transaction.getAmount();
        } else if (transaction instanceof Transfer) {
            Transfer transfer = (Transfer) transaction;
            if (this.equals(transfer.getSourceAccount())) {
                balance -= transaction.getAmount();
            } else if (this.equals(transfer.getDestinationAccount())) {
                balance += transaction.getAmount();
            }
        }
    }
    
    public Transfer transferTo(Account destinationAccount, float amount) {
        if (balance >= amount) {
            Transfer transfer = new Transfer(amount, new java.util.Date(), this, destinationAccount);
            this.addTransaction(transfer);
            destinationAccount.addTransaction(transfer);
            return transfer;
        }
        return null;
    }
    
    public float calculateBalance() {
        float calculatedBalance = 0.0f;
        for (FinancialItem transaction : transactions) {
            if (transaction instanceof Income) {
                calculatedBalance += transaction.getAmount();
            } else if (transaction instanceof Expense) {
                calculatedBalance -= transaction.getAmount();
            } else if (transaction instanceof Transfer) {
                Transfer transfer = (Transfer) transaction;
                if (this.equals(transfer.getSourceAccount())) {
                    calculatedBalance -= transaction.getAmount();
                } else if (this.equals(transfer.getDestinationAccount())) {
                    calculatedBalance += transaction.getAmount();
                }
            }
        }
        return calculatedBalance;
    }
    
    public String getName() {
        return name;
    }
    
    public String getType() {
        return type;
    }
    
    public float getBalance() {
        return balance;
    }
    
    public List<FinancialItem> getTransactions() {
        return transactions;
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s): $%.2f", name, type, balance);
    }
}