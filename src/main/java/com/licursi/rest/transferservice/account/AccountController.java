package com.licursi.rest.transferservice.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping
    public Iterable<Account> findAll(){
        return accountRepository.findAll();
    }

    @PostMapping
    public Account save(@RequestBody Account account){
        return accountRepository.save(account);
    }
}
