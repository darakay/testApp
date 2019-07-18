package com.darakay.testapp.testapp.service;

import com.darakay.testapp.testapp.entity.User;
import com.darakay.testapp.testapp.exception.UserNotFoundException;
import com.darakay.testapp.testapp.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

    public Long getExpiresForUser(long principalId) throws UserNotFoundException {
       return userRepository
               .findById(principalId)
               .orElseThrow(UserNotFoundException::new)
               .getExpiresAt();
    }

    public void expireCurrentPrincipal(long uid) throws UserNotFoundException {
        User user = getUserById(uid);
        userRepository.save(user.expire());
    }
}
