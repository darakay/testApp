package com.darakay.testapp.testapp.evaluators;

import com.darakay.testapp.testapp.entity.Account;
import com.darakay.testapp.testapp.entity.User;
import com.darakay.testapp.testapp.exception.AccountNotFoundException;
import com.darakay.testapp.testapp.exception.UserNotFoundException;
import com.darakay.testapp.testapp.service.AccountService;
import com.darakay.testapp.testapp.service.UserService;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component(value = "accountAccessEvaluator")
public class AccountAccessEvaluator {

    private final AccountService accountService;

    private final UserService userService;

    public AccountAccessEvaluator(AccountService accountService, UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

    public boolean isAccountUser(Account account, long principalId){
        return account.getOwner().getId() == principalId
                || account.getUsers().stream().map(User::getId).anyMatch(id -> id == principalId);
    }

    public boolean isAccountOwner(long accountId, long principalId) throws AccountNotFoundException {
        return accountService.getById(accountId).getOwner().getId() == principalId;
    }

    public boolean accessTokenIsValid(long principalId) throws UserNotFoundException {
        long wxp = userService.getExpiresForUser(principalId);
        return new Date(wxp).after(new Date());
    }
}
