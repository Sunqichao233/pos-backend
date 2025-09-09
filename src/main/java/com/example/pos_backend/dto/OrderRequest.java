package com.example.pos_backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    private Long orgId;
    private Long storeId;
    private Long userId;
    private List<OrderItemRequest> items;

    @Data
    public static class OrderItemRequest {
        private Long productId;
        private Integer quantity;
    }
}
