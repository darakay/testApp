package com.darakay.testapp.testapp;

import com.darakay.testapp.testapp.dto.AccountCreateRequestDto;
import com.darakay.testapp.testapp.entity.Account;
import com.darakay.testapp.testapp.exception.TariffNotFoundException;
import com.darakay.testapp.testapp.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;


    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity createAccount(@RequestBody AccountCreateRequestDto requestDto) throws TariffNotFoundException {
        Account created = accountService.createAccount(requestDto);
        return ResponseEntity.created(URI.create("/accounts/" + created.getId())).build();
    }
}
