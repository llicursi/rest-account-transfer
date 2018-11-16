package com.licursi.rest.transferservice.controller;

import com.licursi.rest.transferservice.exceptions.AccountNotFoundException;
import com.licursi.rest.transferservice.exceptions.BalanceConstraintViolationException;
import com.licursi.rest.transferservice.exceptions.PositiveValueViolationException;
import com.licursi.rest.transferservice.model.Account;
import com.licursi.rest.transferservice.model.Transfer;
import com.licursi.rest.transferservice.service.AccountService;
import com.licursi.rest.transferservice.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/account")
public class AccountController {

    private AccountService accountService;
    private TransferService transferService;

    @Autowired
    public AccountController(AccountService accountService, TransferService transferService) {
        this.accountService = accountService;
        this.transferService = transferService;
    }

    @GetMapping
    public Iterable<Account> findAll() {
        return accountService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Account save(@RequestBody Account account) throws BalanceConstraintViolationException {
        return accountService.save(account);
    }

    @PostMapping("/{source}/transfer/{target}")
    public Account transfer(@PathVariable Long source,
                            @PathVariable Long target,
                            @RequestBody BigDecimal amount) throws AccountNotFoundException, PositiveValueViolationException, BalanceConstraintViolationException {
        synchronized (transferService) {
            Account sourceUpdated = transferService.processTransfer(source, target, amount);
            return sourceUpdated;
        }
    }

    @GetMapping(value = "/{source}/outgoing", consumes = "application/json")
    public Iterable<Transfer> findAllOutgoing(@PathVariable Long source) throws AccountNotFoundException {
        Iterable<Transfer> trasferBySource = transferService.findAllOutgoing(source);
        return trasferBySource;
    }

    @GetMapping("/{source}/incoming")
    public Iterable<Transfer> findAllIncoming(@PathVariable Long source) throws AccountNotFoundException {
        Iterable<Transfer> trasferByTarget = transferService.findAllIncoming(source);
        return trasferByTarget;
    }

}
