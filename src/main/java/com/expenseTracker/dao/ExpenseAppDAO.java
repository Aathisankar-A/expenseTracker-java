package com.expenseTracker.dao;

import com.expenseTracker.util.DatabaseConnection;
import com.expenseTracker.model.Expense;
import com.expenseTracker.model.Category;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

import java.sql.*;

public class ExpenseAppDAO {

    private static final String SELECT_ALL_EXPENSES = "SELECT e.id, e.description, c.name AS category, e.date, e.income, e.expense " +
                                                      "FROM expenses e LEFT JOIN categories c ON e.category_id = c.id";
                                                      
    private static final String SELECT_ALL_CATEGORIES = "SELECT * FROM categories";

    private static final String INSERT_EXPENSE = "INSERT INTO expenses (description, date, income, expense, category_id) VALUES (?, ?, ?, ?, ?)";
    
    private static final String INSERT_CATEGORY = "INSERT INTO categories (name) VALUES (?)";
    
    private static final String DELETE_EXPENSE = "DELETE FROM expenses WHERE id = ?";

    private static final String DELETE_EXPENSES_BY_CATEGORY = "DELETE e FROM expenses e JOIN categories c ON e.category_id = c.id WHERE c.name = ?";
    private static final String DELETE_CATEGORY = "DELETE FROM categories WHERE name = ?";
    
    private static final String GET_CATEGORY_ID_BY_NAME = "SELECT id FROM categories WHERE name = ?";

    public int getCategoryIdByName(String categoryName) throws SQLException {
        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_CATEGORY_ID_BY_NAME)) {
            stmt.setString(1, categoryName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    return -1; // Category not found
                }
            }
        }
    }

    public int addCategory(String categoryName) throws SQLException {
        try(Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_CATEGORY, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, categoryName);
            
            int rowsAffected = stmt.executeUpdate();
            
            if(rowsAffected == 0) {
                throw new SQLException("Creating category failed, no rows affected.");
            }

            try(ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating category failed, no ID obtained.");
                }
            }
        }
    }

    public void createExpense(Expense expense) throws SQLException {
        try(Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_EXPENSE, Statement.RETURN_GENERATED_KEYS)) {
            
            int categoryId = getCategoryIdByName(expense.getCategory());
            if (categoryId == -1) {
                throw new SQLException("Category not found: " + expense.getCategory());
            }
            
            stmt.setString(1, expense.getDescription());
            stmt.setTimestamp(2, Timestamp.valueOf(expense.getTime()));
            stmt.setInt(3, expense.getIncome());
            stmt.setInt(4, expense.getExpense());
            stmt.setInt(5, categoryId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if(rowsAffected == 0) {
                throw new SQLException("Creating expense failed, no rows affected.");
            }

            // try(ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            //     if (generatedKeys.next()) {
            //         return generatedKeys.getInt(1);
            //     } else {
            //         throw new SQLException("Creating expense failed, no ID obtained.");
            //     }
            // }
        }
    }

    public boolean deleteExpense(int expenseId) throws SQLException {
        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_EXPENSE)) {
            stmt.setInt(1, expenseId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean deleteCategory(String category) throws  SQLException {
        if(!deleteExpenseByCategory(category)){
            return false;
        }
        
        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_CATEGORY)) {
            stmt.setString(1, category);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean deleteExpenseByCategory(String category) throws SQLException {
        try (Connection conn = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = conn.prepareStatement(DELETE_EXPENSES_BY_CATEGORY)) {

            stmt.setString(1, category);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }

    }

    private Expense getExpenseRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String description = rs.getString("description");
        LocalDateTime time = rs.getTimestamp("date").toLocalDateTime();
        String category = rs.getString("category");
        int income = rs.getInt("income");
        int expense = rs.getInt("expense");
        
        Expense expenseObj = new Expense(id, description, category, time, income, expense);
        return expenseObj;
    }
    
    public List<Expense> getAllExpenses() throws SQLException {
        List<Expense> expenses = new ArrayList<Expense>();

        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_EXPENSES);
             ResultSet res = stmt.executeQuery()) {
            while (res.next()) {
                expenses.add(getExpenseRow(res));
            }
        }
        return expenses;
    }

    public List<Expense> getExpensesByCategory(String categoryName) throws SQLException {
        String sql = SELECT_ALL_EXPENSES + " WHERE c.name = ?";
        List<Expense> expenses = new ArrayList<Expense>();

        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoryName);
            try (ResultSet res = stmt.executeQuery()) {
                while (res.next()) {
                    expenses.add(getExpenseRow(res));
                }
            }
        }
        return expenses;
    }

    private Category getCategoryRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String category = rs.getString("name");
        
        Category categoryObj = new Category(id, category);
        return categoryObj;
    }
    
    public List<Category> getAllCategories() throws SQLException {
        List<Category> categories = new ArrayList<Category>();

        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_CATEGORIES);
             ResultSet res = stmt.executeQuery()) {
            while (res.next()) {
                categories.add(getCategoryRow(res));
            }
        }
        return categories;
    }
}
