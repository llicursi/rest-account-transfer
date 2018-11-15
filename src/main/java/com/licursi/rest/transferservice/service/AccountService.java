package com.licursi.rest.transferservice.service;

import com.licursi.rest.transferservice.exceptions.AccountNotFoundException;
import com.licursi.rest.transferservice.exceptions.BalanceConstraintViolationException;
import com.licursi.rest.transferservice.exceptions.NegativeConstraintViolationException;
import com.licursi.rest.transferservice.model.Account;
import com.licursi.rest.transferservice.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 *
 */
@Service
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    private AccountRepository accountRepository;

    @Autowired
    public AccountService(final AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    /**
     * Locate an Account based on it's numeric unique id.
     */
    public Account findById(Long id) throws AccountNotFoundException {
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException("Can not find requested account", id));
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
    public synchronized Account save(Account account) throws BalanceConstraintViolationException {
        log.debug("save() " + account);
        if (account != null && account.getBalance() != null &&
                account.getBalance().compareTo(new BigDecimal(0)) < 0) {
            throw new BalanceConstraintViolationException("Account balance for '" + account.getName() + " cannot be lower than 0");
        }
        return accountRepository.save(account);
    }


    /**
     * Removes from account balance a positive amount, resulting in a non-negative balance.
     *
     * @param source some client account
     * @param amount positive monetary value
     */
    public void withdraw(Account source, BigDecimal amount) throws NegativeConstraintViolationException, BalanceConstraintViolationException {
        validatePositiveAmount(amount, "withdraw");
        final BigDecimal newBalance = source.getBalance().subtract(amount);
        source.setBalance(newBalance);
        save(source);
    }

    /**
     * Adds new funds to an account balance with a positive monetary amount, resulting in a non-negative balance.
     *
     * @param target some client account
     * @param amount positive monetary value
     */
    public void deposit(Account target, BigDecimal amount) throws NegativeConstraintViolationException, BalanceConstraintViolationException {
        validatePositiveAmount(amount, "deposit");
        final BigDecimal newBalance = target.getBalance().add(amount);
        target.setBalance(newBalance);
        save(target);
    }

    // Rejects 0 or negative values
    private void validatePositiveAmount(BigDecimal amount, String operation) throws NegativeConstraintViolationException {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeConstraintViolationException("Invalid negative amount on '" + operation + "' operation");
        }
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new NegativeConstraintViolationException("Invalid empty amount on '" + operation + "' operation");
        }
    }

}
