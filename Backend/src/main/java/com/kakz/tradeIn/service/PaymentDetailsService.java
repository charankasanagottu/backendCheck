package com.kakz.tradeIn.service;

import com.kakz.tradeIn.model.PaymentDetails;
import com.kakz.tradeIn.model.User;

public interface PaymentDetailsService {
    public PaymentDetails addPaymentDetails(String accountNumber,
                                            String accountHolder,
                                            String ifsc,
                                            String bankName,
                                            User user);

    public PaymentDetails getUsersPaymentDetails(User user) throws Exception;
}
