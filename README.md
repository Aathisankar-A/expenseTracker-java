# ExpenseTracker Application

A Java Swing-based desktop application for tracking personal expenses and income using a MySQL database.

## Features

- **Add Expenses and Income**: Record financial transactions with descriptions, amounts, and categories.
- **Category Management**: Add and remove custom categories for organizing transactions.
- **View Transactions**: Display all transactions in a table format showing ID, description, category, date, income, and expense amounts.
- **Filter by Category**: Filter transactions by specific categories or view all.
- **Remove Transactions**: Delete unwanted expense/income entries.
- **Database Integration**: Stores data persistently in a MySQL database.

## Requirements

- Java 11 or higher
- MySQL Server 8.0 or higher
- Maven 3.6 or higher

## Installation and Setup

1. **Clone or Download the Project**:
   ```
   git clone <repository-url>
   cd java-expense-tracker
   ```

2. **Database Setup**:
   - Install and start MySQL Server.
   - Create a new database named `expense_tracker`.
   - Update the database connection details in `src/main/java/com/expenseTracker/util/DatabaseConnection.java` if necessary (default: localhost, root user, no password).

3. **Build the Project**:
   ```
   mvn clean compile
   ```

## Running the Application

1. **Using Maven Exec Plugin**:
   ```
   mvn exec:java
   ```

2. **Create Executable JAR**:
   ```
   mvn package
   ```
   Then run:
   ```
   java -jar target/ExpenseTracker-application-1.0.0.jar
   ```

## Usage

- Launch the application to open the GUI.
- Enter a description, amount, and select a category.
- Click "Add as Expense" or "Add as Income" to record the transaction.
- Use the table to view all transactions.
- Select a transaction to edit/remove or filter by category.
- Manage categories using the "Add Category" option or "Remove Category" button.

## Project Structure

- `src/main/java/com/expenseTracker/Main.java`: Application entry point.
- `src/main/java/com/expenseTracker/gui/ExpenseAppGUI.java`: Main GUI class.
- `src/main/java/com/expenseTracker/model/Expense.java`: Expense model class.
- `src/main/java/com/expenseTracker/model/Category.java`: Category model class.
- `src/main/java/com/expenseTracker/dao/ExpenseAppDAO.java`: Data Access Object for database operations.
- `src/main/java/com/expenseTracker/util/DatabaseConnection.java`: Database connection utility.
- `pom.xml`: Maven configuration file.

## Dependencies

- MySQL Connector/J: For MySQL database connectivity.

## License

This project is open-source. Please check the license file for details.
