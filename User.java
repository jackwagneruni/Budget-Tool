import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements Serializable {
    private String username;
    private List<Account> accounts;
    private List<Budget> budgets;
    private Map<String, String> preferences;
    
    public User(String username) {
        this.username = username;
        this.accounts = new ArrayList<>();
        this.budgets = new ArrayList<>();
        this.preferences = new HashMap<>();
    }
    
    public void addAccount(Account account) {
        accounts.add(account);
    }
    
    public void addBudget(Budget budget) {
        budgets.add(budget);
    }
    
    public void setPreference(String key, String value) {
        preferences.put(key, value);
    }
    
    public String getUsername() {
        return username;
    }
    
    public List<Account> getAccounts() {
        return accounts;
    }
    
    public List<Budget> getBudgets() {
        return budgets;
    }
    
    public Map<String, String> getPreferences() {
        return preferences;
    }
    
    public Account findAccount(String name) {
        for (Account account : accounts) {
            if (account.getName().equals(name)) {
                return account;
            }
        }
        return null;
    }
    
    public Budget findBudget(String name) {
        for (Budget budget : budgets) {
            if (budget.getName().equals(name)) {
                return budget;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return String.format("User: %s with %d accounts and %d budgets", 
                             username, accounts.size(), budgets.size());
    }
}