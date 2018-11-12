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
import java.util.Optional;

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

    @Transactional
    public Account processTransfer(int source, int target, BigDecimal amount) throws AccountNotFoundException, NegativeConstraintViolationException, BalanceConstraintViolationException {
        log.debug("processTransfer(" + source + ", " + target + ", '" + amount.toString() + "')");

        final Optional<Account> sourceAccount = accountService.findById(source);
        final Optional<Account> targetAccount = accountService.findById(target);

        if (!sourceAccount.isPresent()) {
            throw new AccountNotFoundException("Specified account (source) id '" + source + "' does not exist");
        }
        if (!targetAccount.isPresent()) {
            throw new AccountNotFoundException("Specified account (target) id '" + target + "' does not exist");
        }

        log.info("Starting transfer from " + sourceAccount.get() +
                " to " + targetAccount.get() +
                " (amount :" + amount + ")");

        accountService.withdraw(sourceAccount.get(), amount);
        accountService.deposit(targetAccount.get(), amount);

        final Transfer transfer = new Transfer();
        transfer.setSource(sourceAccount.get());
        transfer.setTarget(targetAccount.get());
        transfer.setAmount(amount);

        final Transfer savedTransfer = transferRepository.save(transfer);

        log.info("Transaction #" + transfer.getId() + " from " + savedTransfer.getSource().getName() +
                " to " + savedTransfer.getTarget().getName() +
                " (amount :" + savedTransfer.getAmount() + ") is completed");


        return savedTransfer.getSource();
    }

    public Iterable<Transfer> findAllOutgoing(Integer source) {
        log.debug("findAllOutgoing(" + source + ")");
        Account sourceAccount = new Account();
        sourceAccount.setId(source);
        return transferRepository.findAllBySource(sourceAccount);
    }
}
