package com.oliveyoung.inventory;

import com.oliveyoung.inventory.domain.Reservation;
import com.oliveyoung.inventory.domain.Stock;
import com.oliveyoung.inventory.repository.StockRepository;
import com.oliveyoung.inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ⚠️ 기존 테스트. 해피패스만 있다.
 *    이것만 보고 "테스트가 통과하니 안전하다"고 결론 내리면 안 된다.
 */
@SpringBootTest
class InventoryServiceTest {

    @Autowired InventoryService inventoryService;
    @Autowired StockRepository stockRepository;

    static final String SKU = "SKU-TONER-001";

    @BeforeEach
    void setUp() {
        stockRepository.deleteAll();
        stockRepository.save(new Stock(SKU, "ONLINE", null, 100));
        stockRepository.save(new Stock(SKU, "STORE", "ST-0001", 5));
    }

    @Test
    void 재고를_예약할_수_있다() {
        Reservation r = inventoryService.reserve("key-1", SKU, "ONLINE", 2);

        assertThat(r.getStatus()).isEqualTo("RESERVED");
        assertThat(r.getQuantity()).isEqualTo(2);
    }

    @Test
    void 예약하면_가용재고가_줄어든다() {
        inventoryService.reserve("key-2", SKU, "ONLINE", 3);

        Stock stock = stockRepository.findBySkuAndSource(SKU, "ONLINE").orElseThrow();
        assertThat(stock.getAvailable()).isEqualTo(97);
    }

    @Test
    void 전체_가용재고를_조회할_수_있다() {
        assertThat(inventoryService.getTotalAvailable(SKU)).isEqualTo(105);
    }
}
