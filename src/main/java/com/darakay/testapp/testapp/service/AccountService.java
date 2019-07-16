package com.darakay.testapp.testapp.service;


import com.darakay.testapp.testapp.dto.AccountCreateRequestDto;
import com.darakay.testapp.testapp.entity.Account;
import com.darakay.testapp.testapp.entity.Tariff;
import com.darakay.testapp.testapp.entity.Transaction;
import com.darakay.testapp.testapp.entity.User;
import com.darakay.testapp.testapp.exception.AccountNotFoundException;
import com.darakay.testapp.testapp.exception.TariffNotFoundException;
import com.darakay.testapp.testapp.exception.UserNotFoundException;
import com.darakay.testapp.testapp.repos.AccountRepository;
import com.darakay.testapp.testapp.repos.TariffRepository;
import com.darakay.testapp.testapp.repos.UserRepository;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserService userService;
    private final TariffRepository tariffRepository;

    public AccountService(AccountRepository accountRepository, UserService userService, TariffRepository tariffRepository) {
        this.accountRepository = accountRepository;
        this.userService = userService;
        this.tariffRepository = tariffRepository;
    }

    public Account createAccount(AccountCreateRequestDto requestDto) throws TariffNotFoundException {
        Tariff tariff = defineTariff(requestDto.getTariffName());
        User user = getCurrentPrincipal();
        Account saved = accountRepository.save(new Account(0, tariff, user));
        User owner = userService.save(user);
        owner.addAccounts(saved);
        userService.save(owner);
        return saved;
    }

    @PostAuthorize(value = "@accountAccessEvaluator.canGetAccountInfo(returnObject)")
    public Account getAccount(long id) throws AccountNotFoundException {
        return accountRepository.findById(id).orElseThrow(AccountNotFoundException::new);
    }

    private Tariff defineTariff(String tariffName) throws TariffNotFoundException {
        return tariffRepository.findByName(tariffName).orElseThrow(TariffNotFoundException::new);
    }

    private User getCurrentPrincipal(){
        org.springframework.security.core.userdetails.User principal =
                (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.loadByLogin(principal.getUsername()).get();
    }

    @PreAuthorize(value = "@accountAccessEvaluator.isAccountOwner(#accountId)")
    public void delete(@P(value = "accountId") long accountId) {
        accountRepository.deleteById(accountId);
    }

    @PreAuthorize(value = "@accountAccessEvaluator.isAccountOwner(#accountId)")
    public List<User> getUsers(long accountId) throws AccountNotFoundException {
        return new ArrayList<>(
                accountRepository.findById(accountId)
                        .orElseThrow(AccountNotFoundException::new)
                        .getUsers());
    }

    @PreAuthorize(value = "@accountAccessEvaluator.isAccountOwner(#accountId)")
    public List<Transaction> getTransactions(long accountId) throws AccountNotFoundException {
        Account account =  accountRepository
                .findById(accountId)
                .orElseThrow(AccountNotFoundException::new);
        List<Transaction> w = account.getWithdrawals();
        w.addAll(account.getDeposits());
        return w;
    }

    @Transactional
    @PreAuthorize(value = "@accountAccessEvaluator.isAccountOwner(#accountId)")
    public void deleteAccountUser(long accountId, long userId)
            throws AccountNotFoundException, UserNotFoundException {
        Account account = accountRepository.findById(accountId).orElseThrow(AccountNotFoundException::new);
        User user = userService.getUserById(userId);
        userService.save(user.deleteAccount(account));
        accountRepository.save(account.removeUser(user));
    }

    public Account getById(long id) throws AccountNotFoundException {
        return accountRepository.findById(id).orElseThrow(AccountNotFoundException::new);
    }

    public Account save(Account account){
        return accountRepository.save(account);
    }
}
