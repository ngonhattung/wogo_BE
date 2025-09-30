package com.nhattung.wogo.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),

    //authentication and authorization errors
    INVALID_KEY(9001, "Invalid key", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(9002, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(9003, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_CREDENTIALS(9004, "Username or password not correct", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN(9005, "Invalid refresh token", HttpStatus.BAD_REQUEST),
    ERROR_REFRESH_TOKEN(9006, "Token invalid or expired", HttpStatus.BAD_REQUEST),


    //notfound errors
    ROLE_NOT_FOUND(1001, "Role not found", HttpStatus.NOT_FOUND),
    SERVICE_CATEGORY_NOT_FOUND(1002, "Service category not found", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(1003, "User not found", HttpStatus.NOT_FOUND),
    QUESTION_CATEGORY_NOT_FOUND(1004, "Question category not found", HttpStatus.NOT_FOUND),
    QUESTION_NOT_FOUND(1005, "Question not found", HttpStatus.NOT_FOUND),
    SERVICE_NOT_FOUND(1006, "Service not found", HttpStatus.NOT_FOUND),
    QUESTION_OPTION_NOT_FOUND(1007, "Question option not found", HttpStatus.NOT_FOUND),
    TEST_NOT_FOUND(1008, "Test not found", HttpStatus.NOT_FOUND),
    VERIFICATION_NOT_FOUND(1009, "Verification not found", HttpStatus.NOT_FOUND),
    WORKER_VERIFICATION_TEST_NOT_FOUND(1010, "Worker verification test not found", HttpStatus.NOT_FOUND),
    WORKER_DOCUMENT_NOT_FOUND(1011, "Worker document not found", HttpStatus.NOT_FOUND),
    WORKER_DOCUMENT_FILE_NOT_FOUND(1012, "Worker document file not found", HttpStatus.NOT_FOUND),
    BOOKING_NOT_FOUND(1013, "Booking not found", HttpStatus.NOT_FOUND),
    ADDRESS_NOT_FOUND(1014, "Address not found", HttpStatus.NOT_FOUND),
    WORKER_NOT_FOUND(1015, "Worker not found", HttpStatus.NOT_FOUND),
    PAYMENT_NOT_FOUND(1016, "Payment not found", HttpStatus.NOT_FOUND),
    JOB_FILE_NOT_FOUND(1017, "Job file not found", HttpStatus.NOT_FOUND),
    JOB_NOT_FOUND(1018, "Job not found", HttpStatus.NOT_FOUND),
    CHAT_ROOM_NOT_FOUND(1019, "Chat room not found", HttpStatus.NOT_FOUND),
    CHAT_FILE_NOT_FOUND(1020, "Chat file not found", HttpStatus.NOT_FOUND),
    WITHDRAWAL_NOT_FOUND(1021, "Withdrawal request not found", HttpStatus.NOT_FOUND),
    WALLET_TRANSACTION_NOT_FOUND(1022, "Wallet transaction not found", HttpStatus.NOT_FOUND),
    DEPOSIT_NOT_FOUND(1023, "Deposit not found", HttpStatus.NOT_FOUND),
    WORKER_QUOTE_NOT_FOUND(1024, "Worker quote not found", HttpStatus.NOT_FOUND),
    NOTIFICATION_NOT_FOUND(1025, "Notification not found", HttpStatus.NOT_FOUND),

    //validation errors
    EMPTY_PHONE(2001, "Phone number cannot be empty", HttpStatus.BAD_REQUEST),
    EMPTY_PASSWORD(2002, "Password cannot be empty", HttpStatus.BAD_REQUEST),
    PASSWORD_LENGTH(2003, "Password must be at least 6 characters", HttpStatus.BAD_REQUEST),
    EMPTY_QUESTION_CATEGORY_NAME(2004, "Question category name cannot be empty", HttpStatus.BAD_REQUEST),
    SERVICE_ID_REQUIRED(2005, "Service ID is required", HttpStatus.BAD_REQUEST),
    REQUIRED_SCORE_RANGE_MESSAGE(2006, "Required score must be between 0 and 100", HttpStatus.BAD_REQUEST),
    QUESTION_PER_TEST_MIN_MESSAGE(2007, "Question per test must be at least 1", HttpStatus.BAD_REQUEST),
    DESCRIPTION_MAX_MESSAGE(2008, "Description must be less than 255 characters", HttpStatus.BAD_REQUEST),
    OPTION_TEXT_NOT_BLANK_MESSAGE(2009, "Option text cannot be blank", HttpStatus.BAD_REQUEST),
    ORDER_INDEX_MIN_MESSAGE(2010, "Order index must be at least 1", HttpStatus.BAD_REQUEST),
    QUESTION_ID_NOT_NULL_MESSAGE(2011, "Question ID cannot be null", HttpStatus.BAD_REQUEST),
    QUESTION_TEXT_NOT_BLANK_MESSAGE(2012, "Question text cannot be blank", HttpStatus.BAD_REQUEST),
    QUESTION_TYPE_NOT_NULL_MESSAGE(2013, "Question type cannot be null", HttpStatus.BAD_REQUEST),
    DIFFICULTY_LEVEL_NOT_NULL_MESSAGE(2014, "Difficulty level cannot be null", HttpStatus.BAD_REQUEST),
    QUESTION_CATEGORY_ID_NOT_NULL_MESSAGE(2015, "Question category ID cannot be null", HttpStatus.BAD_REQUEST),
    QUESTION_OPTIONS_NOT_EMPTY_MESSAGE(2016, "Question options cannot be empty", HttpStatus.BAD_REQUEST),
    QUESTION_OPTIONS_SIZE_MESSAGE(2017, "Question options must have at least one option", HttpStatus.BAD_REQUEST),
    FULLNAME_NOT_BLANK_MESSAGE(2018, "Fullname cannot be empty", HttpStatus.BAD_REQUEST),
    EMPTY_SERVICE_NAME(2019, "Service name cannot be empty", HttpStatus.BAD_REQUEST),
    TEST_ID_NOT_NULL_MESSAGE(2020, "Test ID cannot be null", HttpStatus.BAD_REQUEST),
    ANSWERS_NOT_EMPTY_MESSAGE(2021, "Answers cannot be empty", HttpStatus.BAD_REQUEST),
    SELECTED_OPTIONS_NOT_EMPTY_MESSAGE(2022, "Selected options cannot be empty", HttpStatus.BAD_REQUEST),
    PHONE_INVALID(2023, "Phone number must be in the format 0XXXXXXXXX or +84XXXXXXXXX", HttpStatus.BAD_REQUEST),



    //existing errors
    USER_ALREADY_EXISTS(3001, "User already exists", HttpStatus.BAD_REQUEST),
    SERVICE_CATEGORY_NAME_EXISTS(3002, "Service category name already exists", HttpStatus.BAD_REQUEST),
    SERVICE_NAME_EXISTS(3003, "Service name already exists", HttpStatus.BAD_REQUEST),
    ROLE_ALREADY_EXISTS(3004, "Role already exists", HttpStatus.BAD_REQUEST),
    WORKER_SERVICE_EXISTS(3005, "Worker service already exists", HttpStatus.BAD_REQUEST),
    WORKER_DOCUMENT_EXISTS(3006, "Worker document already exists for this user and service", HttpStatus.BAD_REQUEST),
    EXISTING_PENDING_JOB_REQUEST(3007, "You have an existing pending job request for this service", HttpStatus.BAD_REQUEST),

    // file errors
    FILE_SIZE_TOO_LARGE(4001, "File size exceeds the limit of 5MB", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE(4002, "Invalid file type", HttpStatus.BAD_REQUEST),
    UPLOAD_IMAGE_ERROR(4003, "Error uploading image", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_UPLOAD_ERROR_EMPTY(4004, "File upload error: no files provided", HttpStatus.BAD_REQUEST),

    // general errors
    INVALID_PAGE_SIZE(5001, "Page size must be greater than 0", HttpStatus.BAD_REQUEST),
    INVALID_BOOKING_AMOUNT(5002, "Booking amount must be greater than 0", HttpStatus.BAD_REQUEST),
    QR_LINK_GENERATION_FAILED(5003, "QR link generation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    BOOKING_CANNOT_CONFIRM_PRICE(5004, "Cannot confirm price for this booking", HttpStatus.BAD_REQUEST),
    JOB_CANNOT_BE_PLACED(5005, "Cannot place this job request", HttpStatus.BAD_REQUEST),
    YOU_ALREADY_SEND_QUOTE(5006, "You have already sent a quote for this job request", HttpStatus.BAD_REQUEST),
    CANNOT_ACCEPT_OWN_JOB(5007, "You cannot accept your own job request", HttpStatus.BAD_REQUEST),
    INVALID_PAYMENT_STATUS(5008, "Invalid payment status", HttpStatus.BAD_REQUEST),
    WALLET_BALANCE_NOT_ENOUGH(5009, "Wallet balance is not enough", HttpStatus.BAD_REQUEST),
    ;


    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
    private final int code;
    private final HttpStatusCode statusCode;
    private final String message;
}
