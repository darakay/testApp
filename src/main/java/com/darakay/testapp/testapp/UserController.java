package com.darakay.testapp.testapp;

import com.darakay.testapp.testapp.dto.UserTransaction;
import com.darakay.testapp.testapp.exception.BadRequestException;
import com.darakay.testapp.testapp.exception.UserNotFoundException;
import com.darakay.testapp.testapp.service.TransactionService;
import com.darakay.testapp.testapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final TransactionService transactionService;
    private final UserService userService;

    public UserController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @GetMapping("/login")
    public ResponseEntity<?> login(HttpServletRequest request, HttpServletResponse response) throws BadRequestException {
        String credentials = request.getHeader("Authorization");
        String token = userService.login(credentials);
        response.setHeader("XXX-JwtToken", token);
        return ResponseEntity.ok().build();
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
