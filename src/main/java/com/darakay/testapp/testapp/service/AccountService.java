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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserService userService;
    private final TariffRepository tariffRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository, UserService userService, TariffRepository tariffRepository) {
        this.accountRepository = accountRepository;
        this.userService = userService;
        this.tariffRepository = tariffRepository;
    }

    public Account createAccount(AccountCreateRequestDto requestDto) throws TariffNotFoundException {
        Tariff tariff = defineTariff(requestDto.getTariffName());
        User user = userService.getCurrentPrincipal();
        Account saved = accountRepository.save(new Account(0, tariff, user));
        User owner = userService.save(user);
        owner.addAccounts(saved);
        userService.save(owner);
        return saved;
    }

    @PostAuthorize(value = "@accountAccessEvaluator.canGetAccountInfo(returnObject, principal.id)")
    public Account getAccount(long id) throws AccountNotFoundException {
        return accountRepository.findById(id).orElseThrow(AccountNotFoundException::new);
    }

    private Tariff defineTariff(String tariffName) throws TariffNotFoundException {
        return tariffRepository.findByName(tariffName).orElseThrow(TariffNotFoundException::new);
    }

    @PreAuthorize(value = "@accountAccessEvaluator.isAccountOwner(#accountId, principal.id)")
    public void delete(@P(value = "accountId") long accountId) {
        accountRepository.deleteById(accountId);
    }

    @PreAuthorize(value = "@accountAccessEvaluator.isAccountOwner(#accountId, principal.id)")
    public List<User> getUsers(long accountId) throws AccountNotFoundException {
        return new ArrayList<>(this.getById(accountId).getUsers());
    }

    @PreAuthorize(value = "@accountAccessEvaluator.isAccountOwner(#accountId, principal.id)")
    public List<Transaction> getTransactions(long accountId) throws AccountNotFoundException {
        Account account =  getById(accountId);
        List<Transaction> w = account.getWithdrawals();
        w.addAll(account.getDeposits());
        return w;
    }

    @Transactional
    @PreAuthorize(value = "@accountAccessEvaluator.isAccountOwner(#accountId, principal.id)")
    public void deleteAccountUser(long accountId, long userId)
            throws AccountNotFoundException, UserNotFoundException {
        Account account = getById(accountId);
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
