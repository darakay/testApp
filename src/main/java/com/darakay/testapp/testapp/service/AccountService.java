package com.darakay.testapp.testapp.service;


import com.darakay.testapp.testapp.dto.AccountCreateRequestDto;
import com.darakay.testapp.testapp.entity.Account;
import com.darakay.testapp.testapp.entity.Tariff;
import com.darakay.testapp.testapp.exception.TariffNotFoundException;
import com.darakay.testapp.testapp.repos.AccountRepository;
import com.darakay.testapp.testapp.repos.TariffRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    private final TariffRepository tariffRepository;

    public AccountService(AccountRepository accountRepository, TariffRepository tariffRepository) {
        this.accountRepository = accountRepository;
        this.tariffRepository = tariffRepository;
    }

    public Account createAccount(AccountCreateRequestDto requestDto) throws TariffNotFoundException {
        Tariff tariff = defineTariff(requestDto.getTariffName());
        return accountRepository.save(new Account(0, tariff, null));
    }

    private Tariff defineTariff(String tariffName) throws TariffNotFoundException {
        return tariffRepository.findByName(tariffName).orElseThrow(TariffNotFoundException::new);
    }
}
