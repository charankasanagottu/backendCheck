package com.kakz.tradeIn.service;

import com.kakz.tradeIn.domain.WithdrawalStatus;
import com.kakz.tradeIn.model.User;
import com.kakz.tradeIn.model.Withdrawal;
import com.kakz.tradeIn.repository.WithdrawalRepository;
import lombok.With;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for handling withdrawal operations.
 * This class provides methods for requesting a new withdrawal,
 * processing existing withdrawal requests, and fetching
 * the withdrawal history for a specific user or all users.
 */
@Service
public class WithdrawalServiceImpl implements WithdrawalService{

    /**
     * Repository for performing CRUD operations on Withdrawal entities.
     * This repository provides methods to save, find, and delete Withdrawal
     * records from the database.
     */
    @Autowired
    private WithdrawalRepository withdrawalRepository;

    /**
     * Requests a new withdrawal for the specified user with the given amount.
     *
     * @param amount the amount to be withdrawn
     * @param user the user requesting the withdrawal
     * @return the newly created and saved Withdrawal object
     */
    @Override
    public Withdrawal requestWithdrawal(Long amount, User user) {
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setDate(LocalDateTime.now());
        withdrawal.setAmount(amount);
        withdrawal.setUser(user);
        withdrawal.setStatus(WithdrawalStatus.PENDING);

        return withdrawalRepository.save(withdrawal);
    }

    /**
     * Processes a withdrawal request by updating its status and date.
     * If the withdrawal is accepted, its status is set to SUCCESS;
     * otherwise, it is set to DECLINED.
     *
     * @param withdrawalId the ID of the withdrawal to be processed
     * @param accept a boolean indicating whether the withdrawal is accepted
     * @return the updated Withdrawal entity after processing
     * @throws Exception if the withdrawal does not exist
     */
    @Override
    public Withdrawal procedWithdrawal(Long withdrawalId, boolean accept) throws Exception {
        Optional<Withdrawal> withdrawal  = withdrawalRepository.findById(withdrawalId);
        if(withdrawal.isEmpty()){
            throw new Exception("withdrawal does not exist");
        }
        Withdrawal withdrawal1 = withdrawal.get();
        withdrawal1.setDate(LocalDateTime.now());
        if(accept){
            withdrawal1.setStatus(WithdrawalStatus.SUCCESS);
        }
        else{
            withdrawal1.setStatus(WithdrawalStatus.DECLINED);
        }
        return withdrawalRepository.save(withdrawal1);
    }

    /**
     * Retrieves the withdrawal history for a specific user.
     *
     * @param user the user whose withdrawal history is to be fetched
     * @return a list of Withdrawals associated with the specified user
     */
    @Override
    public List<Withdrawal> getUsersWithdrawalHistory(User user) {

        return withdrawalRepository.findByUserId(user.getId());
    }

    /**
     * Retrieves all withdrawal requests from the repository.
     *
     * @return a list containing all withdrawal requests.
     */
    @Override
    public List<Withdrawal> getAllWithdrawalRequest() {

        return withdrawalRepository.findAll();
    }
}
