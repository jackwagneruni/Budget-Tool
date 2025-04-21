import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Budget implements Serializable {
    private String name;
    private Date startDate;
    private Date endDate;
    private Map<Category, Float> categories;
    private Map<Category, Float> actualSpending;
    
    public Budget(String name, Date startDate, Date endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.categories = new HashMap<>();
        this.actualSpending = new HashMap<>();
    }
    
    public void setCategoryLimit(Category category, float amount) {
        categories.put(category, amount);
        actualSpending.putIfAbsent(category, 0.0f);
    }
    
    public void trackSpending(Category category, float amount) {
        float currentSpending = actualSpending.getOrDefault(category, 0.0f);
        actualSpending.put(category, currentSpending + amount);
    }
    
    public float getRemainingBudget(Category category) {
        float limit = categories.getOrDefault(category, 0.0f);
        float spent = actualSpending.getOrDefault(category, 0.0f);
        return limit - spent;
    }
    
    public Map<Category, Float> getBudgetStatus() {
        Map<Category, Float> status = new HashMap<>();
        for (Category category : categories.keySet()) {
            status.put(category, getRemainingBudget(category));
        }
        return status;
    }
    
    public String getName() {
        return name;
    }
    
    public Date getStartDate() {
        return startDate;
    }
    
    public Date getEndDate() {
        return endDate;
    }
    
    public Map<Category, Float> getCategories() {
        return categories;
    }
    
    public Map<Category, Float> getActualSpending() {
        return actualSpending;
    }
    
    @Override
    public String toString() {
        return String.format("Budget: %s (%tD to %tD)", name, startDate, endDate);
    }
}