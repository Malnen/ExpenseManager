package com.wsti.expensemanager.data.model;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.UUID;

public class ExpenseRecord {
    private final String name;
    private final String guid;

    public ExpenseRecord(String name) {
        this.name = name;
        guid = UUID.randomUUID().toString();
    }

    public ExpenseRecord(String name, String guid) {
        this.name = name;
        this.guid = guid;
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

    public String getGuid() {
        return guid;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
