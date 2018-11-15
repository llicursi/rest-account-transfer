package com.licursi.rest.transferservice.service;

import com.licursi.rest.transferservice.exceptions.AccountNotFoundException;
import com.licursi.rest.transferservice.exceptions.BalanceConstraintViolationException;
import com.licursi.rest.transferservice.exceptions.NegativeConstraintViolationException;
import com.licursi.rest.transferservice.model.Account;
import com.licursi.rest.transferservice.model.Transfer;
import com.licursi.rest.transferservice.repository.TransferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Handles all valid transfer operation
 */
@Service
public class TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferService.class);

    private TransferRepository transferRepository;
    private AccountService accountService;

    @Autowired
    public TransferService(final TransferRepository transferRepository, final AccountService accountService) {
        this.transferRepository = transferRepository;
        this.accountService = accountService;
    }

    /**
     * Transfer a positive monetary amount from one source account to another account.<br>
     *
     * This operation is atomic, it will withdraw the money from one valid account and
     * deposit into another valid account, but not causing the the source account to
     * have a negative balance.
     *
     * @param source Account in which the money will be withdraw
     * @param target Account in which the money will be deposit
     * @param amount Money being transferred
     * @return The source account after the transaction is completed.
     * @throws AccountNotFoundException If any of the accounts does not exist
     * @throws NegativeConstraintViolationException If attempt to transfer ZERO or NEGATIVE amount of money
     * @throws BalanceConstraintViolationException If the resulting source account balance is negative
     */
    @Transactional
    public Account processTransfer(long source, long target, BigDecimal amount) throws AccountNotFoundException, NegativeConstraintViolationException, BalanceConstraintViolationException {
        log.debug("processTransfer(" + source + ", " + target + ", '" + amount.toString() + "')");

        final Account sourceAccount = accountService.findById(source);
        final Account targetAccount = accountService.findById(target);

        log.info("Starting transfer from " + sourceAccount +
                " to " + targetAccount +
                " (amount :" + amount + ")");

        accountService.withdraw(sourceAccount, amount);
        accountService.deposit(targetAccount, amount);

        final Transfer transfer = new Transfer();
        transfer.setSource(sourceAccount);
        transfer.setTarget(targetAccount);
        transfer.setAmount(amount);

        final Transfer savedTransfer = transferRepository.save(transfer);

        log.info("Transaction #" + transfer.getId() + " from " + savedTransfer.getSource().getName() +
                " to " + savedTransfer.getTarget().getName() +
                " (amount :" + savedTransfer.getAmount() + ") is completed");


        return savedTransfer.getSource();
    }

    /**
     * List all transfers from the specified account to another account
     * @param source Account transferring amounts to another
     * @return List of transfers, can be empty
     * @throws AccountNotFoundException
     */
    public Iterable<Transfer> findAllOutgoing(Long source) throws AccountNotFoundException {
        log.debug("findAllOutgoing(" + source + ")");

        Account sourceAccount = accountService.findById(source);
        return transferRepository.findAllBySource(sourceAccount);
    }


    /**
     * List all transfers from any account to the specified target account
     * @param account Account recieving
     * @return List of transfers, can be empty
     * @throws AccountNotFoundException
     */
    public Iterable<Transfer> findAllIncoming(Long account) throws AccountNotFoundException {
        log.debug("findAllIncoming(" + account + ")");

        Account targetAccount = accountService.findById(account);
        return transferRepository.findAllByTarget(targetAccount);
    }
}
