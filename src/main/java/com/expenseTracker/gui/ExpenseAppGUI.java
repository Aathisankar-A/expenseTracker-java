package com.expenseTracker.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.expenseTracker.dao.ExpenseAppDAO;
import com.expenseTracker.model.Expense;

import java.time.LocalDateTime;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class ExpenseAppGUI extends JFrame {
  private ExpenseAppDAO expenseDAO;
  private JTable expenseTable;
  private DefaultTableModel tableModel;
  private JTextField amountField;
  private JTextField dateTimeField;
  private JTextArea descriptionArea;
  private JButton addExpenseButton;
  private JButton addIncomeButton;
  private JButton removeButton;
  private JButton refreshButton;
  private JComboBox<String> categoryCombo;

  public ExpenseAppGUI() {
    this.expenseDAO = new ExpenseAppDAO();
    initializeComponents();
    setupLayout();
    // setupEventListeners();
    loadExpenses();
    // setVisible(true);
  }
  private void initializeComponents() {
    setTitle("ExpenseTracker Application");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(800, 600);
    setLocationRelativeTo(null);

    amountField = new JTextField(15);

    String[] columnNames = {"ID", "Description", "Category", "Date", "Income", "Expense"};
    tableModel = new DefaultTableModel(columnNames, 0){
        @Override
        public boolean isCellEditable(int row, int column){
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

    // Date label and field
    gbc.gridx = 4;
    gbc.gridy = 0;
    gbc.weightx = 0;
    inputPanel.add(new JLabel("Date:"), gbc);

    dateTimeField = new JTextField();
    gbc.gridx = 5;
    gbc.gridy = 0;
    gbc.weightx = 0.5;
    inputPanel.add(dateTimeField, gbc);

    // Category label and combo box
    gbc.gridx = 6;
    gbc.gridy = 0;
    gbc.weightx = 0;
    inputPanel.add(new JLabel("Category:"), gbc);

    categoryCombo = new JComboBox<>(new String[]{"Food", "Transport", "Bills"});
    gbc.gridx = 7;
    gbc.gridy = 0;
    gbc.weightx = 0.2;
    inputPanel.add(categoryCombo, gbc);

    gbc.gridx = 3;       // place under Amount field
    gbc.gridy = 1;
    gbc.weightx = 0;
    // JPanel buttonPanel = new JPanel(new FlowLayout());
    // buttonPanel.add(addExpenseButton);
    // buttonPanel.add(addIncomeButton);

    // inputPanel.add(buttonPanel, gbc);

    // gbc.gridx = 3;
    // gbc.gridy = 3;
    // gbc.weightx = 0;
    // buttonPanel.add(removeButton);
    // inputPanel.add(buttonPanel, gbc);

    // add(inputPanel, BorderLayout.NORTH);


    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));

    // Add buttons panel (horizontal)
    JPanel addButtonsPanel = new JPanel(new FlowLayout());
    addButtonsPanel.add(addExpenseButton);
    addButtonsPanel.add(addIncomeButton);
    buttonPanel.add(addButtonsPanel);

    // Remove button panel (horizontal)
    JPanel removeButtonPanel = new JPanel(new FlowLayout());
    removeButtonPanel.add(removeButton);
    buttonPanel.add(removeButtonPanel);

    inputPanel.add(buttonPanel, gbc);

    add(inputPanel, BorderLayout.NORTH);

    add(new JScrollPane(expenseTable), BorderLayout.CENTER);

    
  }

  private void loadExpenses() {
        try{
            List<Expense> expenses = expenseDAO.getAllExpenses();
            updateTable(expenses);
        }   
        catch(SQLException e){
            JOptionPane.showMessageDialog(this,"Error Loading todos : "+e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Expense> expenses){
        tableModel.setRowCount(0);
        for(Expense exp : expenses){
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

}