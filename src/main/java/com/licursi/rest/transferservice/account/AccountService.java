package com.licursi.rest.transferservice.account;

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
}
