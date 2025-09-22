package com.nhattung.wogo.service.sepay;

import com.nhattung.wogo.constants.WogoConstants;
import com.nhattung.wogo.service.payment.IPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class SepayVerifyService implements ISepayVerifyService {

    private final RestTemplate restTemplate;
    private final IPaymentService paymentService;
    @Value("${sepay.historyUrl}")
    private String historyUrl;

    @Value("${sepay.token}")
    private String token;


    @Override
    public boolean checkTransaction(String bookingCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = historyUrl + "?accountNumber=" + WogoConstants.ACCOUNT_NUMBER + "&limit=5";
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            return false;
        }

        Map body = response.getBody();
        List<Map<String, Object>> transactions = (List<Map<String, Object>>) body.get("transactions"); // tùy theo format JSON của SePay

        if (transactions == null) return false;

        String keyword = "TTDV" + bookingCode;

        for (Map<String, Object> txn : transactions) {

            String content = (String) txn.get("transaction_content");

            if (Objects.equals(content, "null") || content.isEmpty())
                continue;

            String[] parts = content.split("\\.", 5); // split tối đa 5 phần, không cần split hết

            if (parts.length > 3 && keyword.equals(parts[3])) {
                return true;
            }

        }
        return false;
    }
}
