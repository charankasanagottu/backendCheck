package com.kakz.tradeIn.repository;

import com.kakz.tradeIn.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {

}
