package com.nhattung.wogo.enums;

public enum VerificationStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected");

    private final String status;

    VerificationStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }


}
