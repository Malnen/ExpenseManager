package com.wsti.expensemanager.data.enums;

public enum ExpensePriority {
    none("None"),
    high("High"),
    normal("Normal"),
    low("Low");

    private final String name;

    ExpensePriority(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
