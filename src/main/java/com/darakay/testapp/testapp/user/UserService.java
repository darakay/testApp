package com.darakay.testapp.testapp.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public long logUp(User user) {
        User saved = userRepository.save(user);
        return saved.getId();
    }
}
