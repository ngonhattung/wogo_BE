package com.nhattung.wogo.service.payment.sepay;

import com.nhattung.wogo.constants.WogoConstants;
import com.nhattung.wogo.dto.response.CreateDepositResponseDTO;
import com.nhattung.wogo.entity.Booking;
import com.nhattung.wogo.entity.Deposit;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class SepayVerifyService implements ISepayVerifyService {

    private final RestTemplate restTemplate;
    @Value("${sepay.historyUrl}")
    private String historyUrl;

    @Value("${sepay.token}")
    private String token;


    @Override
    public boolean checkTransactionForPayment(String bookingCode) {
        String keyword = WogoConstants.TRANSACTION_PAYMENT_CONTENT_PREFIX + bookingCode;
        return checkTransaction(keyword);
    }

    @Override
    public boolean checkTransactionForDeposit(Long depositId) {
        String keyword = WogoConstants.TRANSACTION_DEPOSIT_CONTENT_PREFIX + SecurityUtils.getCurrentUserId() + "TRANS" + depositId;
        return checkTransaction(keyword);
    }

    private boolean checkTransaction(String keyword) {
        List<Map<String, Object>> transactions = fetchTransactions(WogoConstants.LIMIT_TRANSACTION_FETCH);
        if (transactions == null) return false;

        for (Map<String, Object> txn : transactions) {
            String content = (String) txn.get("transaction_content");

            if (content == null || content.isEmpty() || "null".equalsIgnoreCase(content)) {
                continue;
            }

            // Chuẩn hóa chuỗi (xóa khoảng trắng thừa)
            content = content.trim();

            // Thử tách bằng cả dấu chấm và dấu gạch ngang
            String[] partsDot = content.split("\\.", 5);
            String[] partsDash = content.split("-", 5);

            // Kiểm tra từng phần sau khi tách
            if (containsKeyword(partsDot, keyword) || containsKeyword(partsDash, keyword)) {
                return true;
            }

            // Nếu vẫn không thấy, thử kiểm tra toàn bộ chuỗi (phòng khi không có dấu phân cách)
            if (content.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    // Hàm phụ kiểm tra keyword có trong mảng phần tách
    private boolean containsKeyword(String[] parts, String keyword) {
        for (String part : parts) {
            if (part != null && part.trim().equalsIgnoreCase(keyword)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> fetchTransactions(int limit) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = historyUrl + "?accountNumber=" + WogoConstants.ACCOUNT_NUMBER + "&limit=" + limit;
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            return null;
        }

        return (List<Map<String, Object>>) response.getBody().get("transactions");
    }

    @Override
    public String createQRCodeForPayment(Booking booking) {
        return generateQRCode(
                booking.getTotalAmount().toString(),
                WogoConstants.TRANSACTION_PAYMENT_CONTENT_PREFIX + booking.getBookingCode()
        );
    }

    private String generateQRCode(String amount, String description) {
        try {
            return String.format("https://qr.sepay.vn/img?acc=%s&bank=%s&amount=%s&des=%s",
                    URLEncoder.encode(WogoConstants.ACCOUNT_NUMBER, StandardCharsets.UTF_8),
                    URLEncoder.encode(WogoConstants.BANK_NAME, StandardCharsets.UTF_8),
                    URLEncoder.encode(amount, StandardCharsets.UTF_8),
                    URLEncoder.encode(description, StandardCharsets.UTF_8)
            );
        } catch (Exception e) {
            throw new AppException(ErrorCode.QR_LINK_GENERATION_FAILED);
        }
    }

    @Override
    public CreateDepositResponseDTO createQRCodeForDeposit(Deposit deposit) {
        return CreateDepositResponseDTO.builder()
                .depositId(deposit.getId())
                .qrCodeUrl(generateQRCode(
                        deposit.getAmount().toString(),
                        WogoConstants.TRANSACTION_DEPOSIT_CONTENT_PREFIX + SecurityUtils.getCurrentUserId() + "TRANS" + deposit.getId()
                ))
                .build();
    }
}
