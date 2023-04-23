package com.wsti.expensemanager.data.model;

import androidx.annotation.NonNull;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wsti.expensemanager.adapters.LocalDateTimeAdapter;
import com.wsti.expensemanager.data.enums.ExpenseType;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

public class ExpenseRecord {
    private static final int CURRENCY_VALUE = 2;

    private final String name;
    private final ExpenseType expenseType;
    private final BigDecimal currencyValue;
    private final LocalDateTime insertedDate;
    private final String guid;

    public ExpenseRecord(
            String name,
            ExpenseType expenseType,
            BigDecimal currencyValue,
            LocalDateTime insertedDate
    ) {
        this.name = name;
        this.expenseType = expenseType;
        this.currencyValue = currencyValue.setScale(CURRENCY_VALUE, RoundingMode.HALF_EVEN);
        this.insertedDate = insertedDate;
        guid = UUID.randomUUID().toString();
    }

    public ExpenseRecord(
            String name,
            ExpenseType expenseType,
            String guid,
            BigDecimal currencyValue,
            LocalDateTime insertedDate
    ) {
        this.name = name;
        this.expenseType = expenseType;
        this.currencyValue = currencyValue.setScale(CURRENCY_VALUE, RoundingMode.HALF_EVEN);
        this.insertedDate = insertedDate;
        this.guid = guid;
    }

    public static ExpenseRecord fromJson(String json) {
        Gson gson = getGson();
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

    public LocalDateTime getInsertedDate() {
        return insertedDate;
    }

    public String toJson() {
        Gson gson = getGson();
        return gson.toJson(this);
    }

    private static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }
}
