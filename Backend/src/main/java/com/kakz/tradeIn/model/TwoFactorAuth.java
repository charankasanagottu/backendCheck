package com.kakz.tradeIn.model;

import com.kakz.tradeIn.domain.VerificationType;
import lombok.Data;


@Data
public class TwoFactorAuth {
    private boolean isEnabled = false;

    private VerificationType sendTo;


}
