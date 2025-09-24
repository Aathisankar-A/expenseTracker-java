package com.expenseTracker.dao;

import com.expenseTracker.util.DatabaseConnection;
import com.expenseTracker.model.Expense;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

import java.sql.*;

public class ExpenseAppDAO {

  private static final String SELECT_ALL_EXPENSES = "SELECT e.id, e.description, c.name AS category, e.date, e.income, e.expense " +
                                                    "FROM expenses e LEFT JOIN categories c ON e.category_id = c.id";


  private Expense getExpenseRow(ResultSet rs) throws SQLException{
    int id = rs.getInt("id");
    String description = rs.getString("description");
    LocalDateTime time = rs.getTimestamp("date").toLocalDateTime();
    String category = rs.getString("category");
    int income = rs.getInt("income");
    int expense = rs.getInt("expense");
    
    Expense expenseObj = new Expense(id, description, time, category, income, expense);

    return expenseObj;
  }
    
  public List<Expense> getAllExpenses() throws SQLException{
    List<Expense> expenses = new ArrayList<Expense>();

    try (Connection conn = DatabaseConnection.getDBConnection();
    PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_EXPENSES);
    ResultSet res = stmt.executeQuery();)
    {
        while(res.next()){
            expenses.add(getExpenseRow(res));
        }
    }
    return expenses;
  }
}