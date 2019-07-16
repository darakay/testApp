package com.darakay.testapp.testapp.service;

import com.darakay.testapp.testapp.entity.Account;
import com.darakay.testapp.testapp.entity.Tariff;
import com.darakay.testapp.testapp.entity.TariffType;
import com.darakay.testapp.testapp.entity.User;
import com.darakay.testapp.testapp.exception.TariffNotFoundException;
import com.darakay.testapp.testapp.exception.UserNotFoundException;
import com.darakay.testapp.testapp.repos.AccountRepository;
import com.darakay.testapp.testapp.repos.TariffRepository;
import com.darakay.testapp.testapp.repos.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User logUp(User user){
        return userRepository.save(user);
    }

    public Optional<User> loadByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public User getUserById(long id) throws UserNotFoundException {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public User save(User user){
        return userRepository.save(user);
    }

    public User getCurrentPrincipal(){
        org.springframework.security.core.userdetails.User principal =
                (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByLogin(principal.getUsername()).get();
    }
}
