package com.darakay.testapp.testapp.evaluators;

import com.darakay.testapp.testapp.entity.Account;
import com.darakay.testapp.testapp.entity.User;
import com.darakay.testapp.testapp.exception.AccountNotFoundException;
import com.darakay.testapp.testapp.repos.AccountRepository;
import com.darakay.testapp.testapp.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component(value = "accountAccessEvaluator")
public class AccountAccessEvaluator {
    private final UserService userService;
    private final AccountRepository accountRepository;

    public AccountAccessEvaluator(UserService userService, AccountRepository accountRepository) {
        this.userService = userService;
        this.accountRepository = accountRepository;
    }

    public boolean canGetAccountInfo(Account account){
       User user = getPrincipal();
        return account.getOwner().equals(user)
                || account.getUsers().stream().anyMatch(u -> u.equals(user));
    }

    public boolean isAccountOwner(long accountId) throws AccountNotFoundException {
        return accountRepository.findById(accountId)
                .orElseThrow(AccountNotFoundException::new)
                .getOwner().equals(getPrincipal());
    }


    private User getPrincipal(){
        org.springframework.security.core.userdetails.User principal =
                (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.loadByLogin(principal.getUsername()).get();
    }
}
