package com.licursi.rest.transferservice.model;

import java.math.BigDecimal;

public class AccountBuilder {

    private Account account;

    private AccountBuilder(Account account) {
        this.account = account;
    }


    /**
     * Removes the id
     */
    public AccountBuilder noId() {
        return id(null);
    }

    /**
     * Define another id
     */
    public AccountBuilder id(Long id) {
        this.account.setId(id);
        return this;
    }

    /**
     * Removes the name
     */
    public AccountBuilder noName() {
        return name(null);
    }

    /**
     * Define another name
     */
    public AccountBuilder name(String name) {
        this.account.setName(name);
        return this;
    }

    /**
     * Removes the balance
     */
    public AccountBuilder noBalance() {
        this.account.setBalance(null);
        return this;
    }

    /**
     * Define another balance amount
     */
    public AccountBuilder balance(BigDecimal balance) {
        this.account.setBalance(balance);
        return this;
    }

    /**
     * Define another balance amount
     */
    public AccountBuilder balance(String balance) {
        this.account.setBalance(new BigDecimal(balance));
        return this;
    }

    /**
     * Creates the account with defined values. Final point
     *
     * @return An Account instance
     */
    public Account build() {
        return account;
    }

    /**
     * Creates a generic Account. Initially with the following values:
     * <pre>
     *     id : 1
     *     name : "Person"
     *     balance : 1000.00
     * </pre>
     *
     * @return the default initialized account builder
     */
    public static AccountBuilder createGeneric() {
        final Account account = new Account();
        account.setId(1L);
        account.setName("Person");
        account.setBalance(new BigDecimal("1000.00"));
        return new AccountBuilder(account);
    }

    /**
     * Creates a generic Account. Initially with the following values:
     * <pre>
     *     id : 1
     *     name : [provided name]
     *     balance : 1000.00
     * </pre>
     *
     * @param name initial name definition
     * @return the default initialized account builder with predefined Name
     */
    public static AccountBuilder createGeneric(String name) {
        return createGeneric().name(name);
    }

}
