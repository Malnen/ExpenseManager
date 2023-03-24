package com.wsti.expensemanager.data.model;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.wsti.expensemanager.data.enums.ExpenseType;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class ExpenseRecord {
    private static final int CURRENCY_VALUE = 2;

    private final String name;
    private final ExpenseType expenseType;
    private final String guid;
    private final BigDecimal currencyValue;

    public ExpenseRecord(String name, ExpenseType expenseType, BigDecimal currencyValue) {
        this.name = name;
        this.expenseType = expenseType;
        this.currencyValue = currencyValue.setScale(CURRENCY_VALUE, RoundingMode.HALF_EVEN);
        guid = UUID.randomUUID().toString();
    }

    public ExpenseRecord(String name, ExpenseType expenseType, String guid, BigDecimal currencyValue) {
        this.name = name;
        this.expenseType = expenseType;
        this.guid = guid;
        this.currencyValue = currencyValue.setScale(CURRENCY_VALUE, RoundingMode.HALF_EVEN);
    }

    public static ExpenseRecord fromJson(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<ExpenseRecord>() {
        }.getType();

        return gson.fromJson(json, type);
    }

    public String getName() {
        return name;
    }

    public ExpenseType getExpenseType() {
        return expenseType;
    }

    public BigDecimal getCurrencyValue() {
        return currencyValue;
    }

    public String getGuid() {
        return guid;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
