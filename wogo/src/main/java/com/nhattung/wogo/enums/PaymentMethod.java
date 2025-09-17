package com.nhattung.wogo.enums;

public enum PaymentMethod {
    CASH("Cash"),
    MOMO("Momo"),
    PAYPAL("PayPal"),
    BANK_TRANSFER("Bank Transfer");

    private final String method;

    PaymentMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}
