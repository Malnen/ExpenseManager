package com.wsti.expensemanager.data.model;

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

    public String getName() {
        return name;
    }

    public String getGuid() {
        return guid;
    }
}
