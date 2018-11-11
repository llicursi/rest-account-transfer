package com.licursi.rest.transferservice.service;

import com.licursi.rest.transferservice.exceptions.BalanceConstraintViolationException;
import com.licursi.rest.transferservice.exceptions.NegativeConstraintViolationException;
import com.licursi.rest.transferservice.model.Account;
import com.licursi.rest.transferservice.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

/**
 *
 */
@Service
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    private AccountRepository accountRepository;

    @Autowired
    public AccountService(final AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }


    /**
     * Retrieve the complete list of Accounts
     *
     * @return Iterable list of Accounts
     */
    public Iterable<Account> findAll() {
        return accountRepository.findAll();
    }

    /**
     * Creates a new Account with new unique identifier.
     *
     * @param account Account details
     * @return Save account details with ID
     */
    public Account save(Account account) {
        log.debug("save() " + account);
        if (account != null && account.getBalance() != null &&
                account.getBalance().compareTo(new BigDecimal(0)) < 0) {
            throw new BalanceConstraintViolationException("Negative balance is not accepted");
        }
        return accountRepository.save(account);
    }


    /**
     * Removes from account balance a positive amount, resulting in a non-negative balance.
     *
     * @param account some client account
     * @param amount  positive monetary value
     */
    public void withdraw(Account account, BigDecimal amount) throws NegativeConstraintViolationException, BalanceConstraintViolationException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegativeConstraintViolationException("Invalid negative 'amount' on withdraw operation");
        }
        final BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        save(account);
    }

    /**
     * Locate an Account based on it's numeric unique id.
     */
    public Optional<Account> findById(Integer id) {
        return accountRepository.findById(id);
    }
}
