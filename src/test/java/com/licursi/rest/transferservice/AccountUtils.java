package com.licursi.rest.transferservice;

import com.licursi.rest.transferservice.model.Account;

import java.math.BigDecimal;
import java.util.Random;


public class AccountUtils {

    private static final Random random = new Random();

    /*
     * Auxiliar account type A generated without ID
     */
    public static Account generateAccountANullId(String name) {
        Account account = new Account();
        account.setName(name);
        account.setBalance(new BigDecimal("1000.00"));
        return account;
    }

    /*
     * Auxiliar account type A generated without ID
     */
    public static Account generateAccountANullId() {
        return generateAccountANullId("Test Name");
    }

    /*
     * Auxiliar account type A generated with random ID
     */
    public static Account generateAccountAWithId() {
        Account account = generateAccountANullId();
        account.setId(random.nextInt(100));
        return account;
    }

    /*
     * Auxiliar account type A generated with random ID
     */
    public static Account generateAccountAWithId(int id) {
        Account account = generateAccountANullId();
        account.setId(id);
        return account;
    }

    /*
     * Auxiliar account type A generated without ID
     */
    public static Account generateAccountANegative() {
        Account account = generateAccountANullId();
        account.setBalance(new BigDecimal("-1000.00"));
        return account;
    }
}
