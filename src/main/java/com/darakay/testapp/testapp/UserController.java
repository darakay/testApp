package com.darakay.testapp.testapp;

import com.darakay.testapp.testapp.dto.TransactionDto;
import com.darakay.testapp.testapp.entity.Account;
import com.darakay.testapp.testapp.entity.User;
import com.darakay.testapp.testapp.entity.TariffType;
import com.darakay.testapp.testapp.exception.AccountNotFoundException;
import com.darakay.testapp.testapp.exception.TariffNotFoundException;
import com.darakay.testapp.testapp.exception.UserNotFoundException;
import com.darakay.testapp.testapp.service.TransactionResult;
import com.darakay.testapp.testapp.service.TransactionService;
import com.darakay.testapp.testapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final TransactionService transactionService;

    public UserController(UserService userService, TransactionService transactionService) {
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<?> userLogUp(@RequestBody User user) throws URISyntaxException {
        return ResponseEntity.created(new URI("/users/"+userService.logUp(user).getId())).build();
    }

    @PostMapping("/{uid}/accounts")
    public ResponseEntity<?> createAccountFor(@PathVariable long uid, @RequestBody TariffType tariffType)
            throws TariffNotFoundException, UserNotFoundException {

        Account created = userService.createAccountForUser(uid, tariffType);
        return ResponseEntity.created(URI.create("/accounts/"+created.getId())).build();
    }

    @PostMapping("{uid}/transaction")
    public ResponseEntity<TransactionResult> transaction(
            @PathVariable long uid, @RequestBody
            TransactionDto transactionDto) throws AccountNotFoundException, UserNotFoundException {
        TransactionResult result = transactionService.create(uid, transactionDto);
        return ResponseEntity.ok(result);
    }
}
