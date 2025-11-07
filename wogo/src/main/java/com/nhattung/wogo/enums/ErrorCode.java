package com.nhattung.wogo.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Có lỗi xảy ra, vui lòng thử lại sau", HttpStatus.INTERNAL_SERVER_ERROR),

    //authentication and authorization errors
    INVALID_KEY(9001, "Dữ liệu không hợp lệ. Vui lòng kiểm tra và thử lại.", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(9002, "Chưa xác thực", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(9003, "Bạn không có quyền truy cập", HttpStatus.FORBIDDEN),
    INVALID_CREDENTIALS(9004, "Tên đăng nhập hoặc mật khẩu không đúng", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN(9005, "Refresh token không hợp lệ", HttpStatus.BAD_REQUEST),
    ERROR_REFRESH_TOKEN(9006, "Token không hợp lệ hoặc đã hết hạn", HttpStatus.BAD_REQUEST),

    //notfound errors
    ROLE_NOT_FOUND(1001, "Không tìm thấy vai trò", HttpStatus.NOT_FOUND),
    SERVICE_CATEGORY_NOT_FOUND(1002, "Không tìm thấy danh mục dịch vụ", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(1003, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    QUESTION_CATEGORY_NOT_FOUND(1004, "Không tìm thấy danh mục câu hỏi", HttpStatus.NOT_FOUND),
    QUESTION_NOT_FOUND(1005, "Không tìm thấy câu hỏi", HttpStatus.NOT_FOUND),
    SERVICE_NOT_FOUND(1006, "Không tìm thấy dịch vụ", HttpStatus.NOT_FOUND),
    QUESTION_OPTION_NOT_FOUND(1007, "Không tìm thấy lựa chọn của câu hỏi", HttpStatus.NOT_FOUND),
    TEST_NOT_FOUND(1008, "Không tìm thấy bài kiểm tra", HttpStatus.NOT_FOUND),
    VERIFICATION_NOT_FOUND(1009, "Không tìm thấy xác minh", HttpStatus.NOT_FOUND),
    WORKER_VERIFICATION_TEST_NOT_FOUND(1010, "Không tìm thấy bài kiểm tra xác minh thợ", HttpStatus.NOT_FOUND),
    WORKER_DOCUMENT_NOT_FOUND(1011, "Không tìm thấy tài liệu của thợ", HttpStatus.NOT_FOUND),
    WORKER_DOCUMENT_FILE_NOT_FOUND(1012, "Không tìm thấy tệp tài liệu của thợ", HttpStatus.NOT_FOUND),
    BOOKING_NOT_FOUND(1013, "Không tìm thấy đơn đặt dịch vụ", HttpStatus.NOT_FOUND),
    ADDRESS_NOT_FOUND(1014, "Không tìm thấy địa chỉ", HttpStatus.NOT_FOUND),
    WORKER_NOT_FOUND(1015, "Không tìm thấy thợ", HttpStatus.NOT_FOUND),
    PAYMENT_NOT_FOUND(1016, "Không tìm thấy giao dịch thanh toán", HttpStatus.NOT_FOUND),
    JOB_FILE_NOT_FOUND(1017, "Không tìm thấy tệp công việc", HttpStatus.NOT_FOUND),
    JOB_NOT_FOUND(1018, "Không tìm thấy yêu cầu công việc", HttpStatus.NOT_FOUND),
    CHAT_ROOM_NOT_FOUND(1019, "Không tìm thấy phòng chat", HttpStatus.NOT_FOUND),
    CHAT_FILE_NOT_FOUND(1020, "Không tìm thấy tệp chat", HttpStatus.NOT_FOUND),
    WITHDRAWAL_NOT_FOUND(1021, "Không tìm thấy yêu cầu rút tiền", HttpStatus.NOT_FOUND),
    WALLET_TRANSACTION_NOT_FOUND(1022, "Không tìm thấy giao dịch ví", HttpStatus.NOT_FOUND),
    DEPOSIT_NOT_FOUND(1023, "Không tìm thấy giao dịch nạp tiền", HttpStatus.NOT_FOUND),
    WORKER_QUOTE_NOT_FOUND(1024, "Không tìm thấy báo giá của thợ", HttpStatus.NOT_FOUND),
    NOTIFICATION_NOT_FOUND(1025, "Không tìm thấy thông báo", HttpStatus.NOT_FOUND),
    REVIEW_NOT_FOUND(1026, "Không tìm thấy đánh giá", HttpStatus.NOT_FOUND),

    //validation errors
    EMPTY_PHONE(2001, "Số điện thoại không được để trống", HttpStatus.BAD_REQUEST),
    EMPTY_PASSWORD(2002, "Mật khẩu không được để trống", HttpStatus.BAD_REQUEST),
    PASSWORD_LENGTH(2003, "Mật khẩu phải có ít nhất 6 ký tự", HttpStatus.BAD_REQUEST),
    EMPTY_QUESTION_CATEGORY_NAME(2004, "Tên danh mục câu hỏi không được để trống", HttpStatus.BAD_REQUEST),
    SERVICE_ID_REQUIRED(2005, "Mã dịch vụ là bắt buộc", HttpStatus.BAD_REQUEST),
    REQUIRED_SCORE_RANGE_MESSAGE(2006, "Điểm yêu cầu phải nằm trong khoảng 0 đến 100", HttpStatus.BAD_REQUEST),
    QUESTION_PER_TEST_MIN_MESSAGE(2007, "Số câu hỏi mỗi bài kiểm tra phải ít nhất là 1", HttpStatus.BAD_REQUEST),
    DESCRIPTION_MAX_MESSAGE(2008, "Mô tả không được vượt quá 255 ký tự", HttpStatus.BAD_REQUEST),
    OPTION_TEXT_NOT_BLANK_MESSAGE(2009, "Nội dung lựa chọn không được để trống", HttpStatus.BAD_REQUEST),
    ORDER_INDEX_MIN_MESSAGE(2010, "Thứ tự phải lớn hơn hoặc bằng 1", HttpStatus.BAD_REQUEST),
    QUESTION_ID_NOT_NULL_MESSAGE(2011, "ID câu hỏi không được trống", HttpStatus.BAD_REQUEST),
    QUESTION_TEXT_NOT_BLANK_MESSAGE(2012, "Nội dung câu hỏi không được trống", HttpStatus.BAD_REQUEST),
    QUESTION_TYPE_NOT_NULL_MESSAGE(2013, "Loại câu hỏi không được trống", HttpStatus.BAD_REQUEST),
    DIFFICULTY_LEVEL_NOT_NULL_MESSAGE(2014, "Mức độ khó không được trống", HttpStatus.BAD_REQUEST),
    QUESTION_CATEGORY_ID_NOT_NULL_MESSAGE(2015, "ID danh mục câu hỏi không được trống", HttpStatus.BAD_REQUEST),
    QUESTION_OPTIONS_NOT_EMPTY_MESSAGE(2016, "Danh sách lựa chọn câu hỏi không được trống", HttpStatus.BAD_REQUEST),
    QUESTION_OPTIONS_SIZE_MESSAGE(2017, "Câu hỏi phải có ít nhất một lựa chọn", HttpStatus.BAD_REQUEST),
    FULLNAME_NOT_BLANK_MESSAGE(2018, "Họ tên không được để trống", HttpStatus.BAD_REQUEST),
    EMPTY_SERVICE_NAME(2019, "Tên dịch vụ không được để trống", HttpStatus.BAD_REQUEST),
    TEST_ID_NOT_NULL_MESSAGE(2020, "ID bài kiểm tra không được trống", HttpStatus.BAD_REQUEST),
    ANSWERS_NOT_EMPTY_MESSAGE(2021, "Danh sách câu trả lời không được để trống", HttpStatus.BAD_REQUEST),
    SELECTED_OPTIONS_NOT_EMPTY_MESSAGE(2022, "Bạn phải chọn ít nhất một lựa chọn", HttpStatus.BAD_REQUEST),
    PHONE_INVALID(2023, "Số điện thoại phải theo dạng 0XXXXXXXXX hoặc +84XXXXXXXXX", HttpStatus.BAD_REQUEST),

    //existing errors
    USER_ALREADY_EXISTS(3001, "Người dùng đã tồn tại", HttpStatus.BAD_REQUEST),
    SERVICE_CATEGORY_NAME_EXISTS(3002, "Tên danh mục dịch vụ đã tồn tại", HttpStatus.BAD_REQUEST),
    SERVICE_NAME_EXISTS(3003, "Tên dịch vụ đã tồn tại", HttpStatus.BAD_REQUEST),
    ROLE_ALREADY_EXISTS(3004, "Vai trò đã tồn tại", HttpStatus.BAD_REQUEST),
    WORKER_SERVICE_EXISTS(3005, "Thợ đã đăng ký dịch vụ này rồi", HttpStatus.BAD_REQUEST),
    WORKER_DOCUMENT_EXISTS(3006, "Tài liệu thợ cho dịch vụ này đã tồn tại", HttpStatus.BAD_REQUEST),
    EXISTING_PENDING_JOB_REQUEST(3007, "Bạn đã có yêu cầu công việc đang chờ xử lý", HttpStatus.BAD_REQUEST),

    // file errors
    FILE_SIZE_TOO_LARGE(4001, "Kích thước tệp vượt quá 5MB", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE(4002, "Loại tệp không hợp lệ", HttpStatus.BAD_REQUEST),
    UPLOAD_IMAGE_ERROR(4003, "Lỗi khi tải ảnh lên", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_UPLOAD_ERROR_EMPTY(4004, "Không có tệp nào được tải lên", HttpStatus.BAD_REQUEST),

    // general errors
    INVALID_PAGE_SIZE(5001, "Kích thước trang phải lớn hơn 0", HttpStatus.BAD_REQUEST),
    INVALID_BOOKING_AMOUNT(5002, "Số tiền đặt dịch vụ phải lớn hơn 0", HttpStatus.BAD_REQUEST),
    QR_LINK_GENERATION_FAILED(5003, "Tạo link QR thất bại", HttpStatus.INTERNAL_SERVER_ERROR),
    BOOKING_CANNOT_CONFIRM_PRICE(5004, "Không thể xác nhận giá cho đơn này", HttpStatus.BAD_REQUEST),
    JOB_CANNOT_BE_PLACED(5005, "Không thể gửi yêu cầu công việc này", HttpStatus.BAD_REQUEST),
    YOU_ALREADY_SEND_QUOTE(5006, "Bạn đã gửi báo giá cho yêu cầu này rồi", HttpStatus.BAD_REQUEST),
    CANNOT_ACCEPT_OWN_JOB(5007, "Bạn không thể nhận yêu cầu công việc của chính mình", HttpStatus.BAD_REQUEST),
    INVALID_PAYMENT_STATUS(5008, "Trạng thái thanh toán không hợp lệ", HttpStatus.BAD_REQUEST),
    WALLET_BALANCE_NOT_ENOUGH(5009, "Số dư ví không đủ", HttpStatus.BAD_REQUEST),
    TERMS_NOT_ACCEPTED(5010, "Bạn cần đồng ý điều khoản trước khi tiếp tục", HttpStatus.BAD_REQUEST),
    INVALID_PAYMENT_METHOD(5011, "Phương thức thanh toán không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_PAYMENT_TYPE(5012, "Loại thanh toán không hợp lệ", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_BALANCE(5013, "Số dư không đủ để thực hiện giao dịch", HttpStatus.BAD_REQUEST),
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

