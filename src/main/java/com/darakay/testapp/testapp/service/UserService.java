package com.darakay.testapp.testapp.service;

import com.darakay.testapp.testapp.entity.User;
import com.darakay.testapp.testapp.exception.UserNotFoundException;
import com.darakay.testapp.testapp.repos.UserRepository;
import com.darakay.testapp.testapp.security.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PreAuthorize(value = "@accountAccessEvaluator.accessTokenIsValid(principal.id)")
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

    public User getUserByIdOrNull(long id){
        return userRepository.findById(id).orElse(null);
    }

    public Long getExpiresForUser(long principalId) throws UserNotFoundException {
       return userRepository
               .findById(principalId)
               .orElseThrow(UserNotFoundException::new)
               .getExpiresAt();
    }

    public void expireCurrentPrincipal(){
        User user = getCurrentPrincipal();
        userRepository.save(user.expire());
    }
}
