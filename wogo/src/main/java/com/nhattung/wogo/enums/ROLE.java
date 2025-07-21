package com.nhattung.wogo.enums;

public enum ROLE {
    CUSTOMER("ROLE_CUSTOMER"),
    ADMIN("ROLE_ADMIN"),
    WORKER("ROLE_WORKER");
    private final String value;

    ROLE(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
