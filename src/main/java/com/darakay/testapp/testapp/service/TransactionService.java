package com.darakay.testapp.testapp.service;

import com.darakay.testapp.testapp.dto.TransactionRequest;
import com.darakay.testapp.testapp.dto.TransactionResult;
import com.darakay.testapp.testapp.dto.UserTransactionDto;
import com.darakay.testapp.testapp.entity.Account;
import com.darakay.testapp.testapp.entity.Transaction;
import com.darakay.testapp.testapp.entity.TransactionType;
import com.darakay.testapp.testapp.entity.User;
import com.darakay.testapp.testapp.exception.AccountNotFoundException;
import com.darakay.testapp.testapp.exception.UserNotFoundException;
import com.darakay.testapp.testapp.repos.TransactionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    private final AccountService accountService;

    private final UserService userService;

    public TransactionService(TransactionRepository repository, AccountService accountService, UserService userService) {
        this.transactionRepository = repository;
        this.accountService = accountService;
        this.userService = userService;
    }

    @PreAuthorize(value = "@accountAccessEvaluator.accessTokenIsValid(principal.id)")
    synchronized public TransactionResult perform(TransactionRequest transactionRequest, long uid)
            throws AccountNotFoundException, UserNotFoundException {
        User user = userService.getUserById(uid);
        Account source = accountService.getById(transactionRequest.getSourceAccountId());
        Account target = accountService.getById(transactionRequest.getTargetAccountId());
        if(!isCorrectSum(user, transactionRequest.getSum(), source))
            return TransactionResult.fail("Invalid transaction");

        Transaction transaction = performTransaction(user, source, target, transactionRequest.getSum());
        return TransactionResult.ok(transactionRepository.save(transaction));
    }


    private boolean isCorrectSum(User user, double sum, Account source) throws AccountNotFoundException, UserNotFoundException {
        return sum> 0 &&  sum <= source.getSum() &&
                isCorrectSumForUser(user, source, sum);
    }

    private boolean isCorrectSumForUser(User user, Account account, double sum) {
        if(account.getOwner().equals(user))
            return sum <= account.getTariff().getOwnerLimit();
        if(account.getUsers().contains(user))
            return sum <= account.getTariff().getUserLimit();
        return false;

    }

    private Transaction performTransaction(User author, Account source, Account target, double sum){
        accountService.save(source.changeSum(-sum));
        accountService.save(target.changeSum(sum));
        return new Transaction(source, target, author, sum, TransactionType.TRANSACTION);
    }

    @PreAuthorize(value = "@accountAccessEvaluator.accessTokenIsValid(principal.id)")
    public List<UserTransactionDto> getUserTransactionsSortedBy(long uid, String order, Integer limit, Integer offset) throws UserNotFoundException {
        User user = userService.getUserById(uid);
        if(limit == null)
            return getUserTransactionsSortedBy(uid, order);
        Pageable pageable = PageRequest.of(offset, limit, Sort.Direction.ASC, order);
        return transactionRepository
                .findTransactionsByUser(user, pageable)
                .stream()
                .map(UserTransactionDto::fromTransaction)
                .collect(Collectors.toList());
    }

    private List<UserTransactionDto> getUserTransactionsSortedBy(long uid, String order) throws UserNotFoundException {
        User user = userService.getUserById(uid);
        Sort sort = new Sort(Sort.Direction.ASC, order);
        return transactionRepository
                .findTransactionsByUser(user, sort)
                .stream()
                .map(UserTransactionDto::fromTransaction)
                .collect(Collectors.toList());
    }

}
