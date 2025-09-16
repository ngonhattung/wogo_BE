package com.nhattung.wogo.enums;

public enum BookingStatus {
    COMING("COMING"),
    ARRIVED("ARRIVED"),
    NEGOTIATING("NEGOTIATING"),
    WORKING("WORKING"),
    PAYING("PAYING"),
    COMPLETED("COMPLETED"),
    CANCELLED("CANCELLED");

    private final String status;

    BookingStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
