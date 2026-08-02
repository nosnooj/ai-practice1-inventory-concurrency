package com.oliveyoung.inventory.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 재고 예약. 결제 전에 재고를 잠근다.
 */
@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String reservationId;

    /** 클라이언트가 보낸 멱등키 */
    private String idempotencyKey;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private String source;

    private String storeCode;

    @Column(nullable = false)
    private long quantity;

    /** RESERVED | CONFIRMED | EXPIRED | RELEASED */
    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    protected Reservation() { }

    public Reservation(String reservationId, String idempotencyKey, String sku,
                       String source, String storeCode, long quantity, LocalDateTime expiresAt) {
        this.reservationId = reservationId;
        this.idempotencyKey = idempotencyKey;
        this.sku = sku;
        this.source = source;
        this.storeCode = storeCode;
        this.quantity = quantity;
        this.status = "RESERVED";
        this.expiresAt = expiresAt;
    }

    public Long getId() { return id; }
    public String getReservationId() { return reservationId; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public String getSku() { return sku; }
    public String getSource() { return source; }
    public String getStoreCode() { return storeCode; }
    public long getQuantity() { return quantity; }
    public String getStatus() { return status; }
    public LocalDateTime getExpiresAt() { return expiresAt; }

    public void setStatus(String status) { this.status = status; }
}
