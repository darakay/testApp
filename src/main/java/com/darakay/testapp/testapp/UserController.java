package com.darakay.testapp.testapp;

import com.darakay.testapp.testapp.dto.UserTransactionDto;
import com.darakay.testapp.testapp.exception.BadCredentialsException;
import com.darakay.testapp.testapp.exception.InvalidAuthorizationHeader;
import com.darakay.testapp.testapp.exception.UserNotFoundException;
import com.darakay.testapp.testapp.service.TransactionService;
import com.darakay.testapp.testapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/users/")
public class UserController {
    private final TransactionService transactionService;

    public UserController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
    }

    @GetMapping("{uid}/transaction")
    public ResponseEntity<List<UserTransactionDto>> getTransactions(@PathVariable long uid,
                                                                    @RequestParam (required = false, defaultValue = "date") String sortedBy,
                                                                    @RequestParam(required = false) Integer limit,
                                                                    @RequestParam(required = false, defaultValue = "0") int offset)
            throws UserNotFoundException {

        return ResponseEntity.ok(transactionService.getUserTransactionsSortedBy(uid, sortedBy, limit, offset));
    }
}
