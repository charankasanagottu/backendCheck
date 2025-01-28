package com.kakz.tradeIn.repository;

import com.kakz.tradeIn.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode,Long> {
    VerificationCode findByUserId(Long userId);
}