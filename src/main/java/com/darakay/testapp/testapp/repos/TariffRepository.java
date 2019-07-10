package com.darakay.testapp.testapp.repos;

import com.darakay.testapp.testapp.entity.Tariff;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TariffRepository extends CrudRepository<Tariff, Long> {
    Optional<Tariff> findByName(String name);
}
