import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Report {
    private String type;
    private Date startDate;
    private Date endDate;
    private Map<String, Object> data;
    private SimpleDateFormat dateFormat;
    
    public Report(String type, Date startDate, Date endDate) {
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.data = new HashMap<>();
        this.dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    }
    
    public void generateSpendingReport(List<Account> accounts, List<Category> categories) {
        Map<Category, Float> categorySpending = new HashMap<>();
        float totalSpending = 0.0f;
        
        for (Account account : accounts) {
            for (FinancialItem transaction : account.getTransactions()) {
                if (transaction instanceof Expense && 
                    transaction.getDate().compareTo(startDate) >= 0 && 
                    transaction.getDate().compareTo(endDate) <= 0) {
                    
                    Category category = transaction.getCategory();
                    float amount = transaction.getAmount();
                    categorySpending.put(category, categorySpending.getOrDefault(category, 0.0f) + amount);
                    totalSpending += amount;
                }
            }
        }
        
        data.put("categorySpending", categorySpending);
        data.put("totalSpending", totalSpending);
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
        float totalBudgeted = 0.0f;
        float totalSpent = 0.0f;
        
        for (Budget budget : budgets) {
            if (budget.getStartDate().compareTo(endDate) <= 0 && 
                budget.getEndDate().compareTo(startDate) >= 0) {
                
                Map<Category, Float> budgetStatus = budget.getBudgetStatus();
                data.put(budget.getName(), budgetStatus);
                
                for (Map.Entry<Category, Float> entry : budget.getCategories().entrySet()) {
                    totalBudgeted += entry.getValue();
                    totalSpent += entry.getValue() - budgetStatus.getOrDefault(entry.getKey(), 0.0f);
                }
            }
        }
        
        data.put("totalBudgeted", totalBudgeted);
        data.put("totalSpent", totalSpent);
    }
    
    public void exportToCSV(String filename) throws IOException {
        System.out.println("CSV export is deprecated. Use printReport() instead.");
        printReport();
    }
    
    public void printReport() {
        System.out.println("\n=================================================");
        System.out.println(String.format("%s REPORT", type.toUpperCase()));
        System.out.println("=================================================");
        System.out.println("Period: " + dateFormat.format(startDate) + " to " + dateFormat.format(endDate));
        System.out.println("-------------------------------------------------");
        
        if (type.equals("Spending")) {
            printSpendingReport();
        } else if (type.equals("Income")) {
            printIncomeReport();
        } else if (type.equals("Budget")) {
            printBudgetReport();
        }
        
        System.out.println("=================================================");
    }
    
    private void printSpendingReport() {
        Map<Category, Float> categorySpending = (Map<Category, Float>) data.get("categorySpending");
        float totalSpending = (Float) data.getOrDefault("totalSpending", 0.0f);
        
        System.out.println("SPENDING BY CATEGORY:");
        System.out.println("-------------------------------------------------");
        System.out.println(String.format("%-35s %-15s", "CATEGORY", "AMOUNT"));
        System.out.println("-------------------------------------------------");
        
        if (categorySpending != null) {
            for (Map.Entry<Category, Float> entry : categorySpending.entrySet()) {
                String category = entry.getKey().getName();
                if (category.length() > 32) {
                    category = category.substring(0, 29) + "...";
                }
                
                System.out.println(String.format("%-35s $%-15.2f", 
                                   category, entry.getValue()));
            }
        }
        
        System.out.println("-------------------------------------------------");
        System.out.println(String.format("%-35s $%-15.2f", "TOTAL", totalSpending));
    }
    
    private void printIncomeReport() {
        Map<String, Float> incomeBySource = (Map<String, Float>) data.get("incomeBySource");
        float totalIncome = (Float) data.getOrDefault("totalIncome", 0.0f);
        
        System.out.println("INCOME BY SOURCE:");
        System.out.println("-------------------------------------------------");
        System.out.println(String.format("%-35s %-15s", "SOURCE", "AMOUNT"));
        System.out.println("-------------------------------------------------");
        
        if (incomeBySource != null) {
            for (Map.Entry<String, Float> entry : incomeBySource.entrySet()) {
                String source = entry.getKey();
                if (source.length() > 32) {
                    source = source.substring(0, 29) + "...";
                }
                
                System.out.println(String.format("%-35s $%-15.2f", 
                                   source, entry.getValue()));
            }
        }
        
        System.out.println("-------------------------------------------------");
        System.out.println(String.format("%-35s $%-15.2f", "TOTAL", totalIncome));
    }
    
    private void printBudgetReport() {
        float totalBudgeted = (Float) data.getOrDefault("totalBudgeted", 0.0f);
        float totalSpent = (Float) data.getOrDefault("totalSpent", 0.0f);
        
        System.out.println("BUDGET SUMMARY:");
        System.out.println("-------------------------------------------------");
        
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getValue() instanceof Map && !entry.getKey().equals("categorySpending") && 
                !entry.getKey().equals("incomeBySource")) {
                
                System.out.println("Budget: " + entry.getKey());
                System.out.println("-------------------------------------------------");
                System.out.println(String.format("%-35s %-15s", "CATEGORY", "REMAINING"));
                System.out.println("-------------------------------------------------");
                
                Map<Category, Float> budgetStatus = (Map<Category, Float>) entry.getValue();
                for (Map.Entry<Category, Float> categoryEntry : budgetStatus.entrySet()) {
                    String category = categoryEntry.getKey().getName();
                    if (category.length() > 32) {
                        category = category.substring(0, 29) + "...";
                    }
                    
                    System.out.println(String.format("%-35s $%-15.2f", 
                                      category, categoryEntry.getValue()));
                }
                
                System.out.println("-------------------------------------------------");
            }
        }
        
        System.out.println(String.format("%-35s $%-15.2f", "TOTAL BUDGETED", totalBudgeted));
        System.out.println(String.format("%-35s $%-15.2f", "TOTAL SPENT", totalSpent));
        System.out.println(String.format("%-35s $%-15.2f", "REMAINING", totalBudgeted - totalSpent));
    }
    
    public Map<String, Object> getData() {
        return data;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s Report (%s to %s)\n", 
                              type, dateFormat.format(startDate), dateFormat.format(endDate)));
        sb.append("----------------------------------------\n");
        
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getKey().equals("totalBudgeted") || 
                entry.getKey().equals("totalSpent") || 
                entry.getKey().equals("totalIncome") ||
                entry.getKey().equals("totalSpending")) {
                continue;
            }
            
            sb.append(entry.getKey()).append(":\n");
            if (entry.getValue() instanceof Map) {
                Map<?, ?> mapData = (Map<?, ?>) entry.getValue();
                for (Map.Entry<?, ?> mapEntry : mapData.entrySet()) {
                    sb.append(String.format("  %s: $%.2f\n", mapEntry.getKey(), mapEntry.getValue()));
                }
            } else {
                sb.append("  ").append(entry.getValue()).append("\n");
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
}