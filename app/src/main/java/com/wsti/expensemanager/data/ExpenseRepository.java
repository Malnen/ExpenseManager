package com.wsti.expensemanager.data;

import com.wsti.expensemanager.data.model.ExpenseRecord;
import com.wsti.expensemanager.data.model.User;

import java.util.ArrayList;
import java.util.List;

public class ExpenseRepository {
    private static volatile ExpenseRepository instance;

    private final ExpenseDataSource dataSource;

    private List<ExpenseRecord> expenses;

    public ExpenseRepository(ExpenseDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static ExpenseRepository getInstance(ExpenseDataSource dataSource) {
        if (instance == null) {
            instance = new ExpenseRepository(dataSource);
        }

        return instance;
    }

    public static ExpenseRepository getInstance() {
        return instance;
    }

    public List<ExpenseRecord> getExpenses(User user) {
        if (expenses == null) {
            expenses = getAllExpensesOfUser(user);
        }

        return expenses;
    }

    public void forgetExpenses() {
        expenses = null;
    }

    public ExpenseRecord saveExpenseRecord(String expenseName, User user) {
        ExpenseRecord record = dataSource.saveExpenseRecord(expenseName, user);
        expenses.add(record);

        return record;
    }

    private List<ExpenseRecord> getAllExpensesOfUser(User user) {
        return dataSource.getAllExpensesOfUser(user);
    }
}
