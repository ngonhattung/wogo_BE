package com.nhattung.wogo.enums;

public enum TestStatus {
    IN_PROGRESS("In Progress"),
    FAILED("Failed"),
    PASSED("Passed");

    private final String status;

    TestStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
