package com.darakay.testapp.testapp;

import com.darakay.testapp.testapp.dto.AccountCreateRequestDto;
import com.darakay.testapp.testapp.dto.AccountDto;
import com.darakay.testapp.testapp.dto.TransactionDto;
import com.darakay.testapp.testapp.dto.UserDto;
import com.darakay.testapp.testapp.entity.Account;
import com.darakay.testapp.testapp.exception.AccountNotFoundException;
import com.darakay.testapp.testapp.exception.TariffNotFoundException;
import com.darakay.testapp.testapp.exception.UserNotFoundException;
import com.darakay.testapp.testapp.security.UserData;
import com.darakay.testapp.testapp.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;


    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity createAccount(@RequestBody AccountCreateRequestDto requestDto, Principal principal)
            throws TariffNotFoundException, UserNotFoundException {
        UserData userData = (UserData) ((Authentication)principal).getCredentials();
        Account created = accountService.createAccount(requestDto, userData.getId());
        return ResponseEntity.created(URI.create("/api/accounts/" + created.getId())).build();
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDto> getAccount(@PathVariable long accountId)
            throws AccountNotFoundException {

        Account account = accountService.getAccount(accountId);
        AccountDto accountDto = AccountDto.fromEntity(account);
        return ResponseEntity.ok().body(accountDto);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<AccountDto> delete(@PathVariable long accountId) {
        accountService.delete(accountId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{accountId}/users")
    public ResponseEntity<List<UserDto>> getAccountUsers(@PathVariable long accountId)
            throws AccountNotFoundException {

        return ResponseEntity.ok()
                .body(accountService.getUsers(accountId)
                        .stream()
                        .map(UserDto::fromEntity)
                        .collect(Collectors.toList()));
    }

    @DeleteMapping("/{accountId}/users/{uid}")
    public ResponseEntity<List<UserDto>> deleteAccountUser(@PathVariable long accountId, @PathVariable long uid)
            throws AccountNotFoundException, UserNotFoundException {

        accountService.deleteAccountUser(accountId, uid);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<List<TransactionDto>> getAccountTransactions(@PathVariable long accountId)
            throws AccountNotFoundException {

        return ResponseEntity.ok()
                .body(accountService.getTransactions(accountId)
                        .stream()
                        .map(TransactionDto::fromEntity)
                        .collect(Collectors.toList()));
    }
}
