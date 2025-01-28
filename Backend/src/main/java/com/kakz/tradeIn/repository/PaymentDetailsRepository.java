package com.kakz.tradeIn.repository;

import com.kakz.tradeIn.model.PaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentDetailsRepository extends JpaRepository<PaymentDetails,Long> {
    PaymentDetails findByUserId(long userId);
}
