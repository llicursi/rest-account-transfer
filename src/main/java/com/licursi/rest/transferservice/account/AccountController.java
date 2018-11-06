package com.licursi.rest.transferservice.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    public Iterable<Account> findAll(){
        return accountService.findAll();
    }

    @PostMapping
    public Account save(@RequestBody Account account){
        return accountService.save(account);
    }
}
