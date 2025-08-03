package com.nhattung.wogo.enums;

public enum WalletType {
    REVENUE("Revenue"),
    EXPENSE("Expense");

    private final String type;
    WalletType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
