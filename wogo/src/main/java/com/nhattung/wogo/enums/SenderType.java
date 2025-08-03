package com.nhattung.wogo.enums;

public enum SenderType {
    USER("User"),
    WORKER("Worker");

    private final String type;

    SenderType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
