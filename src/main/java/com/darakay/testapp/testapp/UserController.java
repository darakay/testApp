package com.darakay.testapp.testapp;

import com.darakay.testapp.testapp.account.Account;
import com.darakay.testapp.testapp.tariff.TariffType;
import com.darakay.testapp.testapp.user.User;
import com.darakay.testapp.testapp.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity userLogUp(@RequestBody User user) throws URISyntaxException {
        User saved = userRepository.save(user);
        return ResponseEntity.created(new URI("/users/"+saved.getId())).build();
    }



}
