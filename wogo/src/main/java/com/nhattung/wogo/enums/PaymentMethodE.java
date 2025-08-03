package com.nhattung.wogo.enums;

public enum PaymentMethodE {
    CASH("Cash"),
    MOMO("Momo"),
    PAYPAL("PayPal"),
    BANK_TRANSFER("Bank Transfer");

    private final String method;

    PaymentMethodE(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}
