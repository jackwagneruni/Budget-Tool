import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Manager implements HasMenu {
    private Map<String, User> users;
    private User currentUser;
    private List<Category> categories;
    private SimpleDateFormat dateFormat;
    
    public Manager() {
        users = new HashMap<>();
        categories = new ArrayList<>();
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        
        initializeCategories();
        loadData();
    }
    
    private void initializeCategories() {
        Category housing = new Category("Housing");
        Category food = new Category("Food");
        Category transportation = new Category("Transportation");
        Category entertainment = new Category("Entertainment");
        Category utilities = new Category("Utilities");
        Category savings = new Category("Savings");
        Category income = new Category("Income");
        
        housing.addSubCategory(new Category("Rent"));
        housing.addSubCategory(new Category("Mortgage"));
        housing.addSubCategory(new Category("Home Maintenance"));
        
        food.addSubCategory(new Category("Groceries"));
        food.addSubCategory(new Category("Dining Out"));
        food.addSubCategory(new Category("Coffee"));
        
        transportation.addSubCategory(new Category("Gas"));
        transportation.addSubCategory(new Category("Public Transit"));
        transportation.addSubCategory(new Category("Car Maintenance"));
        
        entertainment.addSubCategory(new Category("Movies"));
        entertainment.addSubCategory(new Category("Concerts"));
        entertainment.addSubCategory(new Category("Streaming"));
        
        utilities.addSubCategory(new Category("Electricity"));
        utilities.addSubCategory(new Category("Water"));
        utilities.addSubCategory(new Category("Internet"));
        
        categories.add(housing);
        categories.add(food);
        categories.add(transportation);
        categories.add(entertainment);
        categories.add(utilities);
        categories.add(savings);
        categories.add(income);
    }
    
    public User createUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        if (users.containsKey(username)) {
            System.out.println("Username already exists!");
            return null;
        }
        
        User user = new User(username);
        users.put(username, user);
        currentUser = user;
        System.out.println("User created successfully!");
        return user;
    }
    
    public void login() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        if (users.containsKey(username)) {
            currentUser = users.get(username);
            System.out.println("Login successful!");
        } else {
            System.out.println("User not found!");
        }
    }
    
    public Income recordIncome() {
        if (currentUser == null) {
            System.out.println("Please login first!");
            return null;
        }
        
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter income amount: ");
        float amount = Float.parseFloat(scanner.nextLine());
        
        System.out.print("Enter income source: ");
        String source = scanner.nextLine();
        
        System.out.print("Enter recurrence (once/weekly/monthly/yearly): ");
        String recurrence = scanner.nextLine();
        
        System.out.print("Enter date (MM/dd/yyyy) or press Enter for today: ");
        String dateStr = scanner.nextLine();
        Date date = new Date();
        if (!dateStr.isEmpty()) {
            try {
                date = dateFormat.parse(dateStr);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Using today.");
            }
        }
        
        listAccounts();
        System.out.print("Select account (0-" + (currentUser.getAccounts().size() - 1) + "): ");
        int accountIndex = Integer.parseInt(scanner.nextLine());
        
        if (accountIndex >= 0 && accountIndex < currentUser.getAccounts().size()) {
            Account account = currentUser.getAccounts().get(accountIndex);
            Income income = new Income(amount, date, source, recurrence);
            account.addTransaction(income);
            System.out.println("Income recorded successfully!");
            return income;
        } else {
            System.out.println("Invalid account selection!");
            return null;
        }
    }
    
    public Expense recordExpense() {
        if (currentUser == null) {
            System.out.println("Please login first!");
            return null;
        }
        
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter expense amount: ");
        float amount = Float.parseFloat(scanner.nextLine());
        
        System.out.print("Enter vendor: ");
        String vendor = scanner.nextLine();
        
        System.out.print("Enter notes (optional): ");
        String notes = scanner.nextLine();
        
        System.out.print("Enter date (MM/dd/yyyy) or press Enter for today: ");
        String dateStr = scanner.nextLine();
        Date date = new Date();
        if (!dateStr.isEmpty()) {
            try {
                date = dateFormat.parse(dateStr);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Using today.");
            }
        }
        
        listCategories();
        System.out.print("Select category (0-" + (categories.size() - 1) + "): ");
        int categoryIndex = Integer.parseInt(scanner.nextLine());
        Category category = null;
        
        if (categoryIndex >= 0 && categoryIndex < categories.size()) {
            category = categories.get(categoryIndex);
            
            List<Category> subCategories = category.getSubCategories();
            if (!subCategories.isEmpty()) {
                System.out.println("Subcategories of " + category.getName() + ":");
                for (int i = 0; i < subCategories.size(); i++) {
                    System.out.println(i + ": " + subCategories.get(i).getName());
                }
                System.out.print("Select subcategory (0-" + (subCategories.size() - 1) + ") or -1 for main category: ");
                int subIndex = Integer.parseInt(scanner.nextLine());
                if (subIndex >= 0 && subIndex < subCategories.size()) {
                    category = subCategories.get(subIndex);
                }
            }
        }
        
        listAccounts();
        System.out.print("Select account (0-" + (currentUser.getAccounts().size() - 1) + "): ");
        int accountIndex = Integer.parseInt(scanner.nextLine());
        
        if (accountIndex >= 0 && accountIndex < currentUser.getAccounts().size() && category != null) {
            Account account = currentUser.getAccounts().get(accountIndex);
            Expense expense = new Expense(amount, date, vendor, category, notes);
            account.addTransaction(expense);
            
            for (Budget budget : currentUser.getBudgets()) {
                if (date.compareTo(budget.getStartDate()) >= 0 && date.compareTo(budget.getEndDate()) <= 0) {
                    budget.trackSpending(category, amount);
                }
            }
            
            System.out.println("Expense recorded successfully!");
            return expense;
        } else {
            System.out.println("Invalid account or category selection!");
            return null;
        }
    }
    
    public Transfer transferFunds() {
        if (currentUser == null) {
            System.out.println("Please login first!");
            return null;
        }
        
        if (currentUser.getAccounts().size() < 2) {
            System.out.println("You need at least 2 accounts for transfers!");
            return null;
        }
        
        Scanner scanner = new Scanner(System.in);
        listAccounts();
        
        System.out.print("Select source account (0-" + (currentUser.getAccounts().size() - 1) + "): ");
        int sourceIndex = Integer.parseInt(scanner.nextLine());
        
        System.out.print("Select destination account (0-" + (currentUser.getAccounts().size() - 1) + "): ");
        int destIndex = Integer.parseInt(scanner.nextLine());
        
        if (sourceIndex == destIndex) {
            System.out.println("Source and destination must be different!");
            return null;
        }
        
        if (sourceIndex >= 0 && sourceIndex < currentUser.getAccounts().size() &&
            destIndex >= 0 && destIndex < currentUser.getAccounts().size()) {
            
            Account source = currentUser.getAccounts().get(sourceIndex);
            Account destination = currentUser.getAccounts().get(destIndex);
            
            System.out.print("Enter transfer amount: ");
            float amount = Float.parseFloat(scanner.nextLine());
            
            Transfer transfer = source.transferTo(destination, amount);
            if (transfer != null) {
                System.out.println("Transfer successful!");
                return transfer;
            } else {
                System.out.println("Insufficient funds!");
                return null;
            }
        } else {
            System.out.println("Invalid account selection!");
            return null;
        }
    }
    
    public Report generateReport() {
        if (currentUser == null) {
            System.out.println("Please login first!");
            return null;
        }
        
        Scanner scanner = new Scanner(System.in);
        System.out.println("Report Types:");
        System.out.println("1: Spending Report");
        System.out.println("2: Income Report");
        System.out.println("3: Budget Report");
        System.out.print("Select report type: ");
        int reportType = Integer.parseInt(scanner.nextLine());
        
        System.out.print("Enter start date (MM/dd/yyyy): ");
        Date startDate = new Date();
        try {
            startDate = dateFormat.parse(scanner.nextLine());
        } catch (ParseException e) {
            System.out.println("Invalid date format.");
            return null;
        }
        
        System.out.print("Enter end date (MM/dd/yyyy): ");
        Date endDate = new Date();
        try {
            endDate = dateFormat.parse(scanner.nextLine());
        } catch (ParseException e) {
            System.out.println("Invalid date format.");
            return null;
        }
        
        Report report = new Report(getReportTypeName(reportType), startDate, endDate);
        
        if (reportType == 1) {
            report.generateSpendingReport(currentUser.getAccounts(), categories);
        } else if (reportType == 2) {
            report.generateIncomeReport(currentUser.getAccounts());
        } else if (reportType == 3) {
            report.generateBudgetReport(currentUser.getBudgets());
        } else {
            System.out.println("Invalid report type!");
            return null;
        }
        
        report.printReport();
        
        return report;
    }
    
    private String getReportTypeName(int type) {
        if (type == 1) return "Spending";
        if (type == 2) return "Income";
        if (type == 3) return "Budget";
        return "General";
    }
    
    public Account createAccount() {
        if (currentUser == null) {
            System.out.println("Please login first!");
            return null;
        }
        
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter account name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter account type (checking/savings/credit): ");
        String type = scanner.nextLine();
        
        System.out.print("Enter initial balance: ");
        float balance = Float.parseFloat(scanner.nextLine());
        
        Account account = new Account(name, type, balance);
        currentUser.addAccount(account);
        System.out.println("Account created successfully!");
        return account;
    }
    
    public Budget createBudget() {
        if (currentUser == null) {
            System.out.println("Please login first!");
            return null;
        }
        
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter budget name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter start date (MM/dd/yyyy): ");
        Date startDate = new Date();
        try {
            startDate = dateFormat.parse(scanner.nextLine());
        } catch (ParseException e) {
            System.out.println("Invalid date format.");
            return null;
        }
        
        System.out.print("Enter end date (MM/dd/yyyy): ");
        Date endDate = new Date();
        try {
            endDate = dateFormat.parse(scanner.nextLine());
        } catch (ParseException e) {
            System.out.println("Invalid date format.");
            return null;
        }
        
        Budget budget = new Budget(name, startDate, endDate);
        
        System.out.println("Add categories to budget (enter 0 to finish):");
        while (true) {
            listCategories();
            System.out.print("Select category or 0 to finish: ");
            int categoryIndex = Integer.parseInt(scanner.nextLine());
            
            if (categoryIndex == 0) break;
            
            if (categoryIndex >= 0 && categoryIndex < categories.size()) {
                Category category = categories.get(categoryIndex);
                System.out.print("Enter budget amount for " + category.getName() + ": ");
                float amount = Float.parseFloat(scanner.nextLine());
                budget.setCategoryLimit(category, amount);
            } else {
                System.out.println("Invalid category selection!");
            }
        }
        
        currentUser.addBudget(budget);
        System.out.println("Budget created successfully!");
        return budget;
    }
    
    public void saveData() {
        try {
            FileOutputStream fileOut = new FileOutputStream("budget_data.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(users);
            out.writeObject(categories);
            out.close();
            fileOut.close();
            System.out.println("Data saved successfully!");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    public void loadData() {
        try {
            FileInputStream fileIn = new FileInputStream("budget_data.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            users = (Map<String, User>) in.readObject();
            categories = (List<Category>) in.readObject();
            in.close();
            fileIn.close();
            System.out.println("Data loaded successfully!");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No saved data found, starting fresh.");
        }
    }
    
    @Override
    public String menu() {
        if (currentUser == null) {
            return "Main Menu\n" +
                   "0) Exit\n" +
                   "1) Create User\n" +
                   "2) Login\n" +
                   "Action: ";
        } else {
            return "Main Menu (User: " + currentUser.getUsername() + ")\n" +
                   "0) Exit\n" +
                   "1) Create Account\n" +
                   "2) Create Budget\n" +
                   "3) Record Income\n" +
                   "4) Record Expense\n" +
                   "5) Transfer Funds\n" +
                   "6) Generate Report\n" +
                   "7) View Accounts\n" +
                   "8) View Budgets\n" +
                   "9) Logout\n" +
                   "Action: ";
        }
    }
    
    @Override
    public void start() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        
        while (running) {
            System.out.print(menu());
            int choice = Integer.parseInt(scanner.nextLine());
            
            if (currentUser == null) {
                if (choice == 0) {
                    running = false;
                    saveData();
                } else if (choice == 1) {
                    createUser();
                } else if (choice == 2) {
                    login();
                } else {
                    System.out.println("Invalid choice!");
                }
            } else {
                if (choice == 0) {
                    running = false;
                    saveData();
                } else if (choice == 1) {
                    createAccount();
                } else if (choice == 2) {
                    createBudget();
                } else if (choice == 3) {
                    recordIncome();
                } else if (choice == 4) {
                    recordExpense();
                } else if (choice == 5) {
                    transferFunds();
                } else if (choice == 6) {
                    generateReport();
                } else if (choice == 7) {
                    viewAccounts();
                } else if (choice == 8) {
                    viewBudgets();
                } else if (choice == 9) {
                    currentUser = null;
                    System.out.println("Logged out successfully!");
                } else {
                    System.out.println("Invalid choice!");
                }
            }
        }
    }
    
    private void listAccounts() {
        System.out.println("Accounts:");
        List<Account> accounts = currentUser.getAccounts();
        for (int i = 0; i < accounts.size(); i++) {
            System.out.println(i + ": " + accounts.get(i));
        }
    }
    
    private void listCategories() {
        System.out.println("Categories:");
        for (int i = 0; i < categories.size(); i++) {
            System.out.println(i + ": " + categories.get(i).getName());
        }
    }
    
    private void viewAccounts() {
        System.out.println("\nYour Accounts:");
        for (Account account : currentUser.getAccounts()) {
            System.out.println(account);
        }
        System.out.println();
    }
    
    private void viewBudgets() {
        System.out.println("\nYour Budgets:");
        for (Budget budget : currentUser.getBudgets()) {
            System.out.println(budget);
            Map<Category, Float> status = budget.getBudgetStatus();
            for (Map.Entry<Category, Float> entry : status.entrySet()) {
                System.out.println("  " + entry.getKey().getName() + ": $" + entry.getValue() + " remaining");
            }
            System.out.println();
        }
    }
    
    public static void main(String[] args) {
        Manager manager = new Manager();
        manager.start();
    }
}