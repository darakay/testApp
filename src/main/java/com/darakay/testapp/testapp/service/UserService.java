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
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TariffRepository tariffRepository;


    public UserService(UserRepository userRepository, AccountRepository accountRepository, TariffRepository tariffRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.tariffRepository = tariffRepository;
    }

    public Account createAccountForUser(long userId, TariffType tariffType) throws UserNotFoundException, TariffNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Tariff tariff = tariffRepository.findByName(tariffType.getType()).orElseThrow(TariffNotFoundException::new);
        Account saved = accountRepository.save(new Account(0, tariff, user));
        User owner = userRepository.save(user);
        owner.addAccounts(saved);
        userRepository.save(owner);
        return saved;
    }

    public User logUp(User user){
        return userRepository.save(user);
    }
}
