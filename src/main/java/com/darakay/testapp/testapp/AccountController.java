package com.darakay.testapp.testapp;

import com.darakay.testapp.testapp.dto.UserDto;
import com.darakay.testapp.testapp.entity.Account;
import com.darakay.testapp.testapp.entity.Tariff;
import com.darakay.testapp.testapp.exception.AccountNotFoundException;
import com.darakay.testapp.testapp.repos.AccountRepository;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/{accountId}/users")
    public ResponseEntity<List<UserDto>> getAccountUser(@PathVariable long accountId) throws AccountNotFoundException {
        Account account = accountRepository
                .findById(accountId)
                .orElseThrow(AccountNotFoundException::new);
        List<UserDto> accountUsers = account.getUsers().stream().map(UserDto::fromEntity).collect(Collectors.toList());
        return ResponseEntity
                .ok(accountUsers);
    }

    @GetMapping("/{accountId}/owner")
    public ResponseEntity<UserDto> getAccountOwner(@PathVariable long accountId) throws AccountNotFoundException {
        Account account = accountRepository
                .findById(accountId)
                .orElseThrow(AccountNotFoundException::new);
        return ResponseEntity.ok(UserDto.fromEntity(account.getOwner()));
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<?> closeAccount(@PathVariable long accountId) throws AccountNotFoundException {
        Account account = accountRepository
                .findById(accountId)
                .orElseThrow(AccountNotFoundException::new);
        accountRepository.delete(account);
        return ResponseEntity.ok().build();
    }
}
