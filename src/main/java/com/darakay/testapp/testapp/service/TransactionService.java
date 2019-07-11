package com.darakay.testapp.testapp.service;

import com.darakay.testapp.testapp.dto.TransactionDto;
import com.darakay.testapp.testapp.entity.Account;
import com.darakay.testapp.testapp.entity.Transaction;
import com.darakay.testapp.testapp.entity.TransactionType;
import com.darakay.testapp.testapp.entity.User;
import com.darakay.testapp.testapp.exception.AccountNotFoundException;
import com.darakay.testapp.testapp.exception.UserNotFoundException;
import com.darakay.testapp.testapp.repos.AccountRepository;
import com.darakay.testapp.testapp.repos.TransactionRepository;
import com.darakay.testapp.testapp.repos.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    private final AccountRepository accountRepository;

    private final UserRepository userRepository;

    public TransactionService(TransactionRepository repository, AccountRepository accountRepository, UserRepository userRepository) {
        this.transactionRepository = repository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    synchronized public TransactionResult create(long authorId, TransactionDto transactionDto)
            throws AccountNotFoundException, UserNotFoundException {
        User user = userRepository.findById(authorId).orElseThrow(UserNotFoundException::new);
        Account source = accountRepository
                .findById(transactionDto.getSourceId())
                .orElseThrow(AccountNotFoundException::new);
        Account target = accountRepository
                .findById(transactionDto.getTargetId())
                .orElseThrow(AccountNotFoundException::new);
        if(!isCorrectSum(user, transactionDto))
            return TransactionResult.invalidSum(transactionDto.getSum());

        Transaction transaction = performTransaction(user, source, target, transactionDto.getSum());
        return TransactionResult.ok(transactionRepository.save(transaction));
    }


    private boolean isCorrectSum(User user, TransactionDto transactionDto) throws AccountNotFoundException, UserNotFoundException {
        Account source = accountRepository.findById(transactionDto.getSourceId()).orElseThrow(AccountNotFoundException::new);
        return transactionDto.getSum() > 0 &&  transactionDto.getSum() <= source.getSum() &&
                isCorrectSumForUser(user, source, transactionDto.getSum());
    }

    private boolean isCorrectSumForUser(User user, Account account, double sum) {
        if(account.getOwner().equals(user))
            return sum <= account.getTariff().getOwnerLimit();
        if(account.getUsers().contains(user))
            return sum <= account.getTariff().getUserLimit();
        return false;

    }

    private Transaction performTransaction(User author, Account source, Account target, double sum){
        accountRepository.save(source.changeSum(-sum));
        accountRepository.save(target.changeSum(sum));
        return new Transaction(source, target, author, sum, TransactionType.TRANSACTION);
    }


}
