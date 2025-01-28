package com.kakz.tradeIn.request;

import com.kakz.tradeIn.domain.OrderType;
import lombok.Data;

@Data
public class CreateOrderRequest {
    private String  coinId;
    private double quantity;
    private OrderType orderType;

}
