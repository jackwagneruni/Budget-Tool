import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Report {
    private String type;
    private Date startDate;
    private Date endDate;
    private Map<String, Object> data;
    
    public Report(String type, Date startDate, Date endDate) {
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.data = new HashMap<>();
    }
    
    public void generateSpendingReport(List<Account> accounts, List<Category> categories) {
        Map<Category, Float> categorySpending = new HashMap<>();
        
        for (Account account : accounts) {
            for (FinancialItem transaction : account.getTransactions()) {
                if (transaction instanceof Expense && 
                    transaction.getDate().compareTo(startDate) >= 0 && 
                    transaction.getDate().compareTo(endDate) <= 0) {
                    
                    Category category = transaction.getCategory();
                    float amount = transaction.getAmount();
                    categorySpending.put(category, categorySpending.getOrDefault(category, 0.0f) + amount);
                }
            }
        }
        
        data.put("categorySpending", categorySpending);
    }
    
    public void generateIncomeReport(List<Account> accounts) {
        Map<String, Float> incomeBySource = new HashMap<>();
        float totalIncome = 0.0f;
        
        for (Account account : accounts) {
            for (FinancialItem transaction : account.getTransactions()) {
                if (transaction instanceof Income && 
                    transaction.getDate().compareTo(startDate) >= 0 && 
                    transaction.getDate().compareTo(endDate) <= 0) {
                    
                    Income income = (Income) transaction;
                    String source = income.getSource();
                    float amount = income.getAmount();
                    
                    incomeBySource.put(source, incomeBySource.getOrDefault(source, 0.0f) + amount);
                    totalIncome += amount;
                }
            }
        }
        
        data.put("incomeBySource", incomeBySource);
        data.put("totalIncome", totalIncome);
    }
    
    public void generateBudgetReport(List<Budget> budgets) {
        for (Budget budget : budgets) {
            if (budget.getStartDate().compareTo(endDate) <= 0 && 
                budget.getEndDate().compareTo(startDate) >= 0) {
                
                Map<Category, Float> budgetStatus = budget.getBudgetStatus();
                data.put(budget.getName(), budgetStatus);
            }
        }
    }
    
    public void exportToCSV(String filename) throws IOException {
        FileWriter writer = new FileWriter(filename);
        writer.write("Report Type," + type + "\n");
        writer.write("Start Date," + startDate + "\n");
        writer.write("End Date," + endDate + "\n\n");
        
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            writer.write(entry.getKey() + "\n");
            if (entry.getValue() instanceof Map) {
                Map<?, ?> mapData = (Map<?, ?>) entry.getValue();
                for (Map.Entry<?, ?> mapEntry : mapData.entrySet()) {
                    writer.write(mapEntry.getKey() + "," + mapEntry.getValue() + "\n");
                }
            } else {
                writer.write(entry.getValue().toString() + "\n");
            }
            writer.write("\n");
        }
        
        writer.close();
    }
    
    public Map<String, Object> getData() {
        return data;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s Report (%tD to %tD)\n", type, startDate, endDate));
        sb.append("----------------------------------------\n");
        
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            sb.append(entry.getKey()).append(":\n");
            if (entry.getValue() instanceof Map) {
                Map<?, ?> mapData = (Map<?, ?>) entry.getValue();
                for (Map.Entry<?, ?> mapEntry : mapData.entrySet()) {
                    sb.append(String.format("  %s: %.2f\n", mapEntry.getKey(), mapEntry.getValue()));
                }
            } else {
                sb.append("  ").append(entry.getValue()).append("\n");
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
}