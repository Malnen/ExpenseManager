package com.wsti.expensemanager.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wsti.expensemanager.data.enums.ExpenseType;
import com.wsti.expensemanager.data.model.ExpenseRecord;
import com.wsti.expensemanager.data.model.User;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseDataSource {
    private static final String MY_PREFS = "my_prefs";
    private static final String EXPENSES = "expenses";

    private final Context context;

    public ExpenseDataSource(Context context) {
        this.context = context;
    }

    public ExpenseRecord saveExpenseRecord(String expenseName, ExpenseType expenseType, BigDecimal currencyValue, User user) {
        ExpenseRecord record = new ExpenseRecord(expenseName, expenseType, currencyValue);
        return saveExpenseRecord(record, user);
    }

    public ExpenseRecord saveExpenseRecord(ExpenseRecord record, User user) {
        saveExpense(record, user);
        return record;
    }

    public List<ExpenseRecord> getAllExpensesOfUser(User user) {
        Map<User, List<ExpenseRecord>> expenses = getAllExpenses();
        return expenses.getOrDefault(user, new ArrayList<>());
    }

    public void deleteExpense(User user, ExpenseRecord record) {
        Map<User, List<ExpenseRecord>> expenses = getAllExpenses();
        List<ExpenseRecord> userExpenses = expenses.get(user);
        userExpenses.removeIf(userRecord -> hasSameGuid(record, userRecord));
        expenses.put(user, userExpenses);
        saveExpenses(expenses);
    }

    private void saveExpense(ExpenseRecord record, User user) {
        Map<User, List<ExpenseRecord>> expenses = getAllExpenses();
        List<ExpenseRecord> userExpenses = expenses.getOrDefault(user, new ArrayList<>());
        ExpenseRecord existingExpense = getExistingExpense(record, userExpenses);
        if (existingExpense != null) {
            userExpenses.replaceAll((ExpenseRecord expenseRecord) -> hasSameGuid(record, expenseRecord) ? record : expenseRecord);
        } else {
            userExpenses.add(record);
        }

        expenses.put(user, userExpenses);
        saveExpenses(expenses);
    }

    private void saveExpenses(Map<User, List<ExpenseRecord>> expenses) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Type type = new TypeToken<Map<User, List<ExpenseRecord>>>() {
        }.getType();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(type, new UserExpenseRecordMapAdapter());
        Gson gson = gsonBuilder.create();
        String json = gson.toJson(expenses, type);
        editor.putString(EXPENSES, json);
        editor.apply();
    }

    private Map<User, List<ExpenseRecord>> getAllExpenses() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(EXPENSES, null);
        if (json != null) {
            Type type = new TypeToken<Map<User, List<ExpenseRecord>>>() {
            }.getType();
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(type, new UserExpenseRecordMapAdapter());
            Gson gson = gsonBuilder.create();

            return gson.fromJson(json, type);
        }

        return new HashMap<>();
    }

    private ExpenseRecord getExistingExpense(ExpenseRecord record, List<ExpenseRecord> userExpenses) {
        return userExpenses.stream()
                .filter((ExpenseRecord userRecord) -> hasSameGuid(record, userRecord))
                .findFirst()
                .orElse(null);
    }

    private boolean hasSameGuid(ExpenseRecord firstRecord, ExpenseRecord secondRecord) {
        String firstRecordGuid = firstRecord.getGuid();
        String secondRecordGuid = secondRecord.getGuid();

        return firstRecordGuid.equals(secondRecordGuid);
    }
}
