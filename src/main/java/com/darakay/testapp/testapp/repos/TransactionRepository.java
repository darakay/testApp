package com.darakay.testapp.testapp.repos;

import com.darakay.testapp.testapp.entity.Transaction;
import org.springframework.data.repository.CrudRepository;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
}
