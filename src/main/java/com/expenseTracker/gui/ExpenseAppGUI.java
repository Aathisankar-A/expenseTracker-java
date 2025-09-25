package com.expenseTracker.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.expenseTracker.dao.ExpenseAppDAO;
import com.expenseTracker.model.Expense;
import com.expenseTracker.model.Category;

import java.time.LocalDateTime;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExpenseAppGUI extends JFrame {
    private ExpenseAppDAO expenseDAO;
    private JTable expenseTable;
    private DefaultTableModel tableModel;
    private JTextField amountField;
    private JTextArea descriptionArea;
    private JButton addExpenseButton;
    private JButton addIncomeButton;
    private JButton removeButton;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> filterCombo;
    private JButton filterButton;
    private JButton showAllButton;

    public ExpenseAppGUI() {
        this.expenseDAO = new ExpenseAppDAO();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadExpenses();
        loadCategory();
    }

    private void initializeComponents() {
        setTitle("ExpenseTracker Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        String[] columnNames = {"ID", "Description", "Category", "Date", "Income", "Expense"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        expenseTable = new JTable(tableModel);
        expenseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        addExpenseButton = new JButton("Add as Expense");
        addIncomeButton = new JButton("Add as Income");
        removeButton = new JButton("Remove");
        filterButton = new JButton("Filter");
        showAllButton = new JButton("Show All");

        categoryCombo = new JComboBox<>();
        filterCombo = new JComboBox<>();
        amountField = new JTextField(10);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Input panel with GridBagLayout
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Description label and text area
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0;
        inputPanel.add(new JScrollPane(descriptionArea), gbc);

        // Amount label and field
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Amount:"), gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 0;
        inputPanel.add(amountField, gbc);

        // Category label and combo box
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Category:"), gbc);

        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.weightx = 0;
        inputPanel.add(categoryCombo, gbc);

        // Button panel
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 6;
        gbc.weightx = 1;
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addExpenseButton);
        buttonPanel.add(addIncomeButton);
        buttonPanel.add(removeButton);
        
        inputPanel.add(buttonPanel, gbc);

        add(inputPanel, BorderLayout.NORTH);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout());
        filterPanel.add(new JLabel("Filter by Category:"));
        filterPanel.add(filterCombo);
        filterPanel.add(filterButton);
        filterPanel.add(showAllButton);

        add(filterPanel, BorderLayout.SOUTH);

        add(new JScrollPane(expenseTable), BorderLayout.CENTER);
    }

    private void loadExpenses() {
        try {
            List<Expense> expenses = expenseDAO.getAllExpenses();
            updateExpensesTable(expenses);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Loading expenses: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateExpensesTable(List<Expense> expenses) {
        tableModel.setRowCount(0);
        for (Expense exp : expenses) {
            Object[] row = {
                exp.getId(),
                exp.getDescription(),
                exp.getCategory(),
                exp.getTime(),
                exp.getIncome(),
                exp.getExpense()
            };
            tableModel.addRow(row);
        }
    }

    private void loadCategory() {
        try {
            List<Category> categories = expenseDAO.getAllCategories();
            updateCategoryCombo(categories);
            updateFilterCombo(categories);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Loading categories: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCategoryCombo(List<Category> categories) {
        categoryCombo.removeAllItems();

        for (Category c : categories) {
            categoryCombo.addItem(c.getName());
        }
        categoryCombo.addItem("Add Category");
    }

    private void updateFilterCombo(List<Category> categories) {
        filterCombo.removeAllItems();
        filterCombo.addItem("All Categories");

        for (Category c : categories) {
            filterCombo.addItem(c.getName());
        }
    }

    private void setupEventListeners() {
        addExpenseButton.addActionListener((e) -> {
            addAsExpense();
        });
        addIncomeButton.addActionListener((e) -> {
            addAsIncome();
        });

        removeButton.addActionListener((e) -> {
            removeExpense();
        });

        filterButton.addActionListener((e) -> {
            filterByCategory();
        });

        showAllButton.addActionListener((e) -> {
            loadExpenses();
        });

        expenseTable.getSelectionModel().addListSelectionListener(
                (e) -> {
                    if (!e.getValueIsAdjusting()) {
                        loadSelectedExpense();
                    }
                }
        );

        categoryCombo.addActionListener((e) -> {
            categoryAction();
        });
    }

    private void loadSelectedExpense() {
        int row = expenseTable.getSelectedRow();

        if (row != -1) {
            String description = tableModel.getValueAt(row, 1).toString();
            String category = tableModel.getValueAt(row, 2).toString();
            String income = tableModel.getValueAt(row, 4).toString();
            int incomeInt = Integer.parseInt(income);
            String expense = tableModel.getValueAt(row, 5).toString();

            descriptionArea.setText(description);

            if (incomeInt > 0) {
                amountField.setText(income);
            } else {
                amountField.setText(expense);
            }

            categoryCombo.setSelectedItem(category);
        }
    }

    private void addAsExpense() {
        String description = descriptionArea.getText().trim();
        String amountText = amountField.getText().trim();
        String selectedCategory = (String) categoryCombo.getSelectedItem();

        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Description is required", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Amount is required", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedCategory == null || selectedCategory.equals("Add Category")) {
            JOptionPane.showMessageDialog(this, "Please select a valid category", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int expenseAmount = Integer.parseInt(amountText);
            if (expenseAmount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than 0", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Expense expense = new Expense(description, selectedCategory, 0, expenseAmount);

            expenseDAO.createExpense(expense);

            JOptionPane.showMessageDialog(this, "Expense Added Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);

            clearInputFields();
            loadExpenses();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for amount", "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Adding expense: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addAsIncome() {
        String description = descriptionArea.getText().trim();
        String amountText = amountField.getText().trim();
        String selectedCategory = (String) categoryCombo.getSelectedItem();

        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Description is required", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Amount is required", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedCategory == null || selectedCategory.equals("Add Category")) {
            JOptionPane.showMessageDialog(this, "Please select a valid category", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int incomeAmount = Integer.parseInt(amountText);
            if (incomeAmount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than 0", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Expense expense = new Expense(description, selectedCategory, incomeAmount, 0);

            expenseDAO.createExpense(expense);

            JOptionPane.showMessageDialog(this, "Income Added Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);

            clearInputFields();
            loadExpenses();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for amount", "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Adding income: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeExpense() {
        int selectedRow = expenseTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an expense to remove", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove this expense?", "Confirm Removal", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int expenseId = (Integer) tableModel.getValueAt(selectedRow, 0);

                boolean removed = expenseDAO.deleteExpense(expenseId);

                if (removed) {
                    JOptionPane.showMessageDialog(this, "Expense Removed Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadExpenses();
                    clearInputFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to remove expense", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error removing expense: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void filterByCategory() {
        String selectedCategory = (String) filterCombo.getSelectedItem();

        if (selectedCategory == null || selectedCategory.equals("All Categories")) {
            loadExpenses();
            return;
        }

        try {
            List<Expense> filteredExpenses = expenseDAO.getExpensesByCategory(selectedCategory);
            updateExpensesTable(filteredExpenses);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error filtering expenses: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void categoryAction() {
        String selectedItem = (String) categoryCombo.getSelectedItem();

        if ("Add Category".equals(selectedItem)) {
            String newCategory = JOptionPane.showInputDialog(this, "Enter new category name:", "Add New Category", JOptionPane.QUESTION_MESSAGE);

            if (newCategory != null && !newCategory.trim().isEmpty()) {
                try {
                    expenseDAO.addCategory(newCategory.trim());
                    JOptionPane.showMessageDialog(this, "Category Added Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadCategory();
                    categoryCombo.setSelectedItem(newCategory.trim());
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error adding category: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void clearInputFields() {
        descriptionArea.setText("");
        amountField.setText("");
        categoryCombo.setSelectedIndex(0);
    }
}
