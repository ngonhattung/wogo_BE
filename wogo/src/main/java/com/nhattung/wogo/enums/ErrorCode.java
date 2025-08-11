package com.nhattung.wogo.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1002, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1003, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_CREDENTIALS(1004, "Username or password not correct", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN(1005, "Invalid refresh token", HttpStatus.BAD_REQUEST),
    ERROR_REFRESH_TOKEN(1006, "Token invalid or expired", HttpStatus.BAD_REQUEST),
    USER_ALREADY_EXISTS(1007, "User already exists", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(1008, "Role not found", HttpStatus.NOT_FOUND),
    SERVICE_CATEGORY_NOT_FOUND(1009, "Service category not found", HttpStatus.NOT_FOUND),
    SERVICE_CATEGORY_NAME_EXISTS(1010, "Service category name already exists", HttpStatus.BAD_REQUEST),
    INVALID_PAGE_SIZE(1011, "Invalid page size", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1012, "User not found", HttpStatus.NOT_FOUND),
    QUESTION_CATEGORY_NOT_FOUND(1013, "Question category not found", HttpStatus.NOT_FOUND),
    FILE_SIZE_TOO_LARGE(1014, "File size exceeds the limit of 5MB", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE(1015, "Invalid file type", HttpStatus.BAD_REQUEST),
    UPLOAD_IMAGE_ERROR(1016, "Error uploading image", HttpStatus.INTERNAL_SERVER_ERROR),
    QUESTION_NOT_FOUND(1017, "Question not found", HttpStatus.NOT_FOUND),
    QUESTION_OPTION_NOT_FOUND(1018, "Question option not found", HttpStatus.NOT_FOUND),
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
