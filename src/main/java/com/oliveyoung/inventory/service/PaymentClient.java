package com.oliveyoung.inventory.service;

import org.springframework.stereotype.Component;

/**
 * 외부 결제 게이트웨이 클라이언트 (모의).
 * 실제 환경에서는 네트워크 호출이며 수백 ms ~ 수 초가 걸린다.
 */
@Component
public class PaymentClient {

    public void approve(String paymentToken, long amount) {
        try {
            Thread.sleep(200);   // 외부 호출 지연 시뮬레이션
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (paymentToken == null || paymentToken.isBlank()) {
            throw new IllegalArgumentException("결제 토큰 없음");
        }
    }
}
