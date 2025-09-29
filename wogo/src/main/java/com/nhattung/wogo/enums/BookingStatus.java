package com.nhattung.wogo.enums;

import lombok.Getter;

@Getter
public enum BookingStatus {
    PENDING("PENDING"),
    COMING("COMING"),
    ARRIVED("ARRIVED"),
    NEGOTIATING("NEGOTIATING"),
    WORKING("WORKING"),
    PAYING("PAYING"),
    PAID("PAID"),
    COMPLETED("COMPLETED"),
    CANCELLED("CANCELLED");

    private final String status;

    BookingStatus(String status) {
        this.status = status;
    }

}
