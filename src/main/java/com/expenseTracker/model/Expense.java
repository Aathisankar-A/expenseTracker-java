package com.expenseTracker.model;

import java.time.LocalDateTime;

public class Expense {
    
    private int id;
    private String description;
    private LocalDateTime time;
    private String category;
    private int income;
    private int expense;

    public Expense() {
        time = LocalDateTime.now();
    }

    public Expense(int id, String description, LocalDateTime time, String category, int income, int expense) {
        this.id = id;
        this.description = description;
        this.time = time;
        this.category = category;
        this.income = income;
        this.expense = expense;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getCategory() {
        return category;
    }

    public int getIncome() {
        return income;
    }

    public int getExpense() {
        return expense;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public void setExpense(int expense) {
      this.expense = expense;
    }
}