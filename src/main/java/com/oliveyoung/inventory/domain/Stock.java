package com.oliveyoung.inventory.domain;

import jakarta.persistence.*;

/**
 * 재고. SKU + 재고출처(온라인 물류센터 / 매장) 단위로 관리된다.
 */
@Entity
@Table(name = "stock")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sku;

    /** ONLINE | STORE */
    @Column(nullable = false)
    private String source;

    /** source=STORE 일 때만 값이 있다 */
    private String storeCode;

    /** 실물 재고 */
    @Column(nullable = false)
    private long onHand;

    /** 예약된 수량 */
    @Column(nullable = false)
    private long reserved;

    protected Stock() { }

    public Stock(String sku, String source, String storeCode, long onHand) {
        this.sku = sku;
        this.source = source;
        this.storeCode = storeCode;
        this.onHand = onHand;
        this.reserved = 0;
    }

    public Long getId() { return id; }
    public String getSku() { return sku; }
    public String getSource() { return source; }
    public String getStoreCode() { return storeCode; }
    public long getOnHand() { return onHand; }
    public long getReserved() { return reserved; }

    public long getAvailable() { return onHand - reserved; }

    public void setOnHand(long onHand) { this.onHand = onHand; }
    public void setReserved(long reserved) { this.reserved = reserved; }
}
