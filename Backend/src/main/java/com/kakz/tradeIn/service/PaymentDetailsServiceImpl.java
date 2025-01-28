package com.kakz.tradeIn.service;

import com.kakz.tradeIn.model.PaymentDetails;
import com.kakz.tradeIn.model.User;
import com.kakz.tradeIn.repository.PaymentDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service implementation for managing payment details.
 */
@Service
public class PaymentDetailsServiceImpl implements PaymentDetailsService{
    /**
     * Repository for performing CRUD operations on PaymentDetails entities.
     *
     * This field is automatically wired by Spring to enable database interactions
     * related to payment details, such as adding new payment details and retrieving
     * existing ones for a specific user.
     */
    @Autowired
    private PaymentDetailsRepository paymentDetailsRepository;

    /**
     * Adds payment details for a user and saves them in the repository.
     *
     * @param accountNumber the account number of the user
     * @param accountHolder the name of the account holder
     * @param ifsc the IFSC code of the user's bank
     * @param bankName the name of the bank
     * @param user the user for whom payment details are being added
     * @return the payment details that were saved
     */
    @Override
    public PaymentDetails addPaymentDetails(String accountNumber,
                                            String accountHolder,
                                            String ifsc,
                                            String bankName,
                                            User user) {
        PaymentDetails paymentDetails = new PaymentDetails();

        paymentDetails.setAccountNumber(accountNumber);
        paymentDetails.setUser(user);
        paymentDetails.setIfsc(ifsc);
        paymentDetails.setBank(bankName);
        paymentDetails.setAccountHolderName(accountHolder);

        return paymentDetailsRepository.save(paymentDetails);
    }

    /**
     * Retrives the payment details for a specific user.
     *
     * @param user the User whose payment details need to be retrieved
     * @return the PaymentDetails associated with the specified user
     * @throws Exception if no payment details are found for the user
     */
    @Override
    public PaymentDetails getUsersPaymentDetails(User user) throws Exception {
        PaymentDetails paymentDetails = paymentDetailsRepository.findByUserId(user.getId());
        if(paymentDetails == null){
            throw new Exception("User does not hold any Account details ");
        }
        return paymentDetails;
    }
}
