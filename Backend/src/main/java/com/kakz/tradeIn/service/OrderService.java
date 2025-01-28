package com.kakz.tradeIn.service;

import com.kakz.tradeIn.domain.OrderType;
import com.kakz.tradeIn.model.Coin;
import com.kakz.tradeIn.model.Order;
import com.kakz.tradeIn.model.OrderItem;
import com.kakz.tradeIn.model.User;

import java.util.List;

public interface OrderService {
    Order createOrder(User user, OrderItem orderItem, OrderType orderType);
    Order getOrderById(Long id) throws Exception;
    List<Order> getAllOrdersofUser(Long userId,OrderType orderType,String assetSymbol);
    Order processOrder(Coin coin,double quantity,OrderType orderType,User user) throws Exception;

}
