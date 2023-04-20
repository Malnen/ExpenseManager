package com.wsti.expensemanager.data.enums;

public enum SortOption {
    NAME("Name"),
    AMOUNT("Amount"),
    TYPE("Type"),
    INSERTED_DATE("Inserted Date");

    private final String name;

    SortOption(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
