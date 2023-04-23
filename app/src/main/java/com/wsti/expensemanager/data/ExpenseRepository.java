package com.wsti.expensemanager.data;

import com.wsti.expensemanager.data.enums.ExpenseType;
import com.wsti.expensemanager.data.model.ExpenseRecord;
import com.wsti.expensemanager.data.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    public ExpenseRecord saveExpenseRecord(
            String expenseName,
            ExpenseType expenseType,
            BigDecimal currencyValue,
            User user,
            LocalDateTime date
    ) {
        ExpenseRecord record = dataSource.saveExpenseRecord(expenseName, expenseType, currencyValue, user, date);
        expenses.add(record);

        return record;
    }

    public ExpenseRecord saveExpenseRecord(ExpenseRecord oldRecord, ExpenseRecord newRecord, User user) {
        String oldRecordGuid = oldRecord.getGuid();
        ExpenseRecord oldRecordWithGuid = expenses.stream()
                .filter(record -> record.getGuid().equals(oldRecordGuid))
                .findFirst()
                .get();

        int oldIndex = expenses.indexOf(oldRecordWithGuid);
        expenses.add(oldIndex, newRecord);
        expenses.remove(oldRecordWithGuid);

        return dataSource.saveExpenseRecord(newRecord, user);
    }

    public void deleteExpense(ExpenseRecord record, User user) {
        expenses.remove(record);
        dataSource.deleteExpense(user, record);
    }

    private List<ExpenseRecord> getAllExpensesOfUser(User user) {
        return dataSource.getAllExpensesOfUser(user);
    }
}
