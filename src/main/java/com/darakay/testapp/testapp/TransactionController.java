package com.darakay.testapp.testapp;

import com.darakay.testapp.testapp.dto.TransactionRequest;
import com.darakay.testapp.testapp.dto.TransactionResult;
import com.darakay.testapp.testapp.exception.AccountNotFoundException;
import com.darakay.testapp.testapp.exception.UserNotFoundException;
import com.darakay.testapp.testapp.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController("/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResult> performTransaction(@RequestBody TransactionRequest request)
            throws AccountNotFoundException, UserNotFoundException {
        return ResponseEntity
                .ok(transactionService.perform(request));
    }
}
