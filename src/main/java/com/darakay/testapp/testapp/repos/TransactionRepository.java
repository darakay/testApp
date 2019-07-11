package com.darakay.testapp.testapp.repos;

import com.darakay.testapp.testapp.entity.Transaction;
import com.darakay.testapp.testapp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    Collection<Transaction> findTransactionsByUser(User user, Sort sort);
    Page<Transaction> findTransactionsByUser(User user, Pageable pageable);
    Collection<Transaction> findTransactionsByUser(User user);

}
