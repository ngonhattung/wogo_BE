package com.nhattung.wogo.enums;

public enum TransactionType {

    REVENUE("Revenue"),
    DEPOSIT("Deposit"),
    WITHDRAW("Withdraw"),
    PAYMENT("Payment"),
    REFUND("Refund");

    private final String type;

    TransactionType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
