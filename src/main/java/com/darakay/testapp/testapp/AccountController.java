package com.darakay.testapp.testapp;

import com.darakay.testapp.testapp.account.AccountRepository;
import com.darakay.testapp.testapp.tariff.Tariff;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private AccountRepository accountRepository;

    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping("/{accountId}/tariff")
    public ResponseEntity<Tariff> getTariffByAccountId(@PathVariable long accountId){
        return ResponseEntity.ok()
                .body(accountRepository
                        .findById(accountId)
                        .orElseThrow(ResourceNotFoundException::new)
                        .getTariff());
    }
}
