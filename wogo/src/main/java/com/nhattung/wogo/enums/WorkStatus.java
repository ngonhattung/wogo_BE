package com.nhattung.wogo.enums;

public enum WorkStatus {
    AVAILABLE("AVAILABLE"),
    IN_PROGRESS("IN_PROGRESS"),
    BUSY("BUSY");

    private final String value;

    WorkStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
