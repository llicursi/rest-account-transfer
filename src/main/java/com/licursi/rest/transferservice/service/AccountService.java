package com.licursi.rest.transferservice.service;

import com.licursi.rest.transferservice.exceptions.AccountNotFoundException;
import com.licursi.rest.transferservice.exceptions.BalanceConstraintViolationException;
import com.licursi.rest.transferservice.exceptions.PositiveValueViolationException;
import com.licursi.rest.transferservice.model.Account;
import com.licursi.rest.transferservice.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        validateAccount(account.getBalance());
        return accountRepository.save(account);
    }

    private BigDecimal validateAccount(BigDecimal balance) throws BalanceConstraintViolationException {
        if (balance != null && balance.compareTo(new BigDecimal(0)) < 0) {
            throw new BalanceConstraintViolationException("Account can not have negative balance");
        }
        return balance;
    }


    /**
     * Removes from account balance a positive amount, resulting in a non-negative balance.
     *
     * @param accountId source client account id
     * @param amount positive monetary value
     * @return The source account
     */
    @Transactional
    public Account withdraw(Long accountId, BigDecimal amount) throws PositiveValueViolationException, BalanceConstraintViolationException, AccountNotFoundException {
        final Account source = findById(accountId);
        validatePositiveAmount(amount, "withdraw");
        final BigDecimal newBalance = source.getBalance().subtract(amount);
        source.setBalance(validateAccount(newBalance));
        return source;
    }

    /**
     * Adds new funds to an account balance with a positive monetary amount, resulting in a non-negative balance.
     *
     * @param accountId target client account id
     * @param amount positive monetary value
     * @return The source account
     */
    @Transactional
    public Account deposit(Long accountId, BigDecimal amount) throws PositiveValueViolationException, BalanceConstraintViolationException, AccountNotFoundException {
        final Account target = findById(accountId);
        validatePositiveAmount(amount, "deposit");
        final BigDecimal newBalance = target.getBalance().add(amount);
        target.setBalance(newBalance);
        return target;
    }

    // Rejects 0 or negative values
    private void validatePositiveAmount(BigDecimal amount, String operation) throws PositiveValueViolationException {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new PositiveValueViolationException("Invalid negative amount on '" + operation + "' operation");
        }
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new PositiveValueViolationException("Invalid empty amount on '" + operation + "' operation");
        }
    }

}
