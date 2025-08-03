package com.nhattung.wogo.enums;

public enum VerificationType {

    TEST("Test"),
    DOCUMENT("Document");

    private final String type;

    VerificationType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
