package com.darakay.testapp.testapp.evaluators;

import com.darakay.testapp.testapp.entity.Account;
import com.darakay.testapp.testapp.entity.User;
import com.darakay.testapp.testapp.exception.AccountNotFoundException;
import com.darakay.testapp.testapp.service.AccountService;
import org.springframework.stereotype.Component;

@Component(value = "accountAccessEvaluator")
public class AccountAccessEvaluator {

    private final AccountService accountService;

    public AccountAccessEvaluator(AccountService accountService) {
        this.accountService = accountService;
    }

    public boolean canGetAccountInfo(Account account, long principalId){
        return account.getOwner().getId() == principalId
                || account.getUsers().stream().map(User::getId).anyMatch(id -> id == principalId);
    }

    public boolean isAccountOwner(long accountId, long principalId) throws AccountNotFoundException {
        return accountService.getById(accountId).getOwner().getId() == principalId;
    }
}
