package com.kakz.tradeIn.repository;

import com.kakz.tradeIn.model.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder,Long> {

}
