package com.oliveyoung.inventory.api;

import com.oliveyoung.inventory.domain.Reservation;
import com.oliveyoung.inventory.domain.Stock;
import com.oliveyoung.inventory.service.InventoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/reservations")
    public Reservation reserve(@RequestHeader(value = "Idempotency-Key", required = false) String key,
                               @RequestBody Map<String, Object> body) {
        String sku = (String) body.get("sku");
        String source = (String) body.get("source");
        long quantity = ((Number) body.get("quantity")).longValue();
        return inventoryService.reserve(key, sku, source, quantity);
    }

    @PostMapping("/reservations/{reservationId}/confirm")
    public Reservation confirm(@PathVariable String reservationId,
                               @RequestBody Map<String, Object> body) {
        return inventoryService.confirm(reservationId, (String) body.get("paymentToken"));
    }

    @GetMapping("/stocks")
    public List<Stock> stocks(@RequestParam String sku) {
        return inventoryService.getStocks(sku);
    }

    @GetMapping("/stocks/total")
    public Map<String, Object> total(@RequestParam String sku) {
        return Map.of("sku", sku, "available", inventoryService.getTotalAvailable(sku));
    }
}
