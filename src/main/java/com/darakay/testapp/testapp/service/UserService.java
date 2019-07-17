package com.darakay.testapp.testapp.service;

import com.darakay.testapp.testapp.security.UserData;
import com.darakay.testapp.testapp.entity.User;
import com.darakay.testapp.testapp.exception.UserNotFoundException;
import com.darakay.testapp.testapp.repos.UserRepository;
import org.springframework.security.core.Authentication;
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserData principal = (UserData) auth.getPrincipal();
        return userRepository.findById(principal.getId()).get();
    }
}
