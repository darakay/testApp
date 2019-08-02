package com.darakay.testapp.testapp.validation;

import com.darakay.testapp.testapp.exception.AccountNotFoundException;
import com.darakay.testapp.testapp.service.AccountService;
import org.springframework.stereotype.Component;

@Component
public class AccountValidator {
    private final AccountService accountService;

    public AccountValidator(AccountService accountService) {
        this.accountService = accountService;
    }

    public void validate(String accountId) throws AccountNotFoundException {
        if(!accountService.existById(accountId))
            throw new AccountNotFoundException();
    }
}
