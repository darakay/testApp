package com.darakay.testapp.testapp.repos;

import com.darakay.testapp.testapp.entity.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {
}
