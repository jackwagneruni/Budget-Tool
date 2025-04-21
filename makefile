all: Manager.class

Manager.class: Manager.java User.class Account.class Budget.class Report.class Category.class
	javac -g Manager.java

User.class: User.java Account.class Budget.class
	javac -g User.java

Account.class: Account.java FinancialItem.class
	javac -g Account.java

Budget.class: Budget.java Category.class
	javac -g Budget.java

Report.class: Report.java
	javac -g Report.java

Category.class: Category.java
	javac -g Category.java

Income.class: Income.java FinancialItem.class Category.class
	javac -g Income.java

Expense.class: Expense.java FinancialItem.class Category.class
	javac -g Expense.java

Transfer.class: Transfer.java FinancialItem.class Account.class
	javac -g Transfer.java

FinancialItem.class: FinancialItem.java Category.class
	javac -g FinancialItem.java

HasMenu.class: HasMenu.java
	javac -g HasMenu.java

run: Manager.class
	java Manager

clean:
	rm -f *.class *.ser