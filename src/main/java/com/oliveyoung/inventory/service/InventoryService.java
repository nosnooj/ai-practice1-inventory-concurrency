package com.oliveyoung.inventory.service;

import com.oliveyoung.inventory.domain.Reservation;
import com.oliveyoung.inventory.domain.Stock;
import com.oliveyoung.inventory.repository.ReservationRepository;
import com.oliveyoung.inventory.repository.StockRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 재고 예약 서비스.
 *
 * ⚠️ 이 구현은 지난 스프린트에 급하게 작성되었다.
 *    스테이징에서는 문제없이 동작하지만 세일 트래픽은 겪어보지 않았다.
 */
@Service
public class InventoryService {

    private final StockRepository stockRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentClient paymentClient;

    @Value("${inventory.reservation-ttl-minutes:10}")
    private int ttlMinutes;

    public InventoryService(StockRepository stockRepository,
                            ReservationRepository reservationRepository,
                            PaymentClient paymentClient) {
        this.stockRepository = stockRepository;
        this.reservationRepository = reservationRepository;
        this.paymentClient = paymentClient;
    }

    /**
     * 재고를 예약한다.
     */
    @Transactional
    public Reservation reserve(String idempotencyKey, String sku, String source, long quantity) {

        Optional<Reservation> existing = reservationRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            return existing.get();
        }

        Stock stock = stockRepository.findBySkuAndSource(sku, source)
                .orElseThrow(() -> new IllegalArgumentException("재고 없음: " + sku));

        if (stock.getAvailable() < quantity) {
            throw new IllegalStateException("재고 부족");
        }

        stock.setReserved(stock.getReserved() + quantity);
        stockRepository.save(stock);

        Reservation reservation = new Reservation(
                UUID.randomUUID().toString(),
                idempotencyKey,
                sku,
                source,
                stock.getStoreCode(),
                quantity,
                LocalDateTime.now().plusMinutes(ttlMinutes)
        );
        return reservationRepository.save(reservation);
    }

    /**
     * 결제를 요청하고 예약을 확정한다.
     */
    @Transactional
    public Reservation confirm(String reservationId, String paymentToken) {
        Reservation reservation = reservationRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약 없음"));

        // 결제 승인
        paymentClient.approve(paymentToken, reservation.getQuantity());

        Stock stock = stockRepository.findBySkuAndSource(reservation.getSku(), reservation.getSource())
                .orElseThrow();
        stock.setOnHand(stock.getOnHand() - reservation.getQuantity());
        stock.setReserved(stock.getReserved() - reservation.getQuantity());
        stockRepository.save(stock);

        reservation.setStatus("CONFIRMED");
        return reservationRepository.save(reservation);
    }

    /**
     * 만료된 예약을 회수한다.
     */
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void releaseExpired() {
        List<Reservation> expired =
                reservationRepository.findByStatusAndExpiresAtBefore("RESERVED", LocalDateTime.now());

        for (Reservation r : expired) {
            try {
                Stock stock = stockRepository.findBySkuAndSource(r.getSku(), r.getSource()).orElseThrow();
                stock.setReserved(stock.getReserved() - r.getQuantity());
                stockRepository.save(stock);
                r.setStatus("EXPIRED");
                reservationRepository.save(r);
            } catch (Exception e) {
                // 개별 실패는 무시하고 계속 진행
            }
        }
    }

    /** SKU의 전체 재고를 조회한다. */
    public List<Stock> getStocks(String sku) {
        return stockRepository.findBySku(sku);
    }

    /** SKU의 총 가용 재고. */
    public long getTotalAvailable(String sku) {
        long total = 0;
        for (Stock s : stockRepository.findBySku(sku)) {
            total += s.getAvailable();
        }
        return total;
    }
}
