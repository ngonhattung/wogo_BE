package com.nhattung.wogo.enums;

public enum BookingStatus {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    WOKE_COME_HOME("Woke Come Home"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    CANCELLED("Canceled");

    private final String status;

    BookingStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
