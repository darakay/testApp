package com.darakay.testapp.testapp.repos;

import com.darakay.testapp.testapp.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface UserRepository extends CrudRepository<User, Long> {
    @EntityGraph(attributePaths = {"accounts"})
    Optional<User> findById(long id);

    User findByFirstNameAndLastName(String firstName, String lastName);
}
