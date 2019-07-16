package com.darakay.testapp.testapp;

import com.darakay.testapp.testapp.dto.UserTransaction;
import com.darakay.testapp.testapp.exception.UserNotFoundException;
import com.darakay.testapp.testapp.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final TransactionService transactionService;

    public UserController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{uid}/transaction")
    public ResponseEntity<List<UserTransaction>> getTransactions(@PathVariable long uid,
                                                                 @RequestParam (required = false, defaultValue = "date") String sortedBy,
                                                                 @RequestParam(required = false) Integer limit,
                                                                 @RequestParam(required = false, defaultValue = "0") int offset)
            throws UserNotFoundException {

        return ResponseEntity.ok(transactionService.getUserTransactionsSortedBy(uid, sortedBy, limit, offset));
    }
}
