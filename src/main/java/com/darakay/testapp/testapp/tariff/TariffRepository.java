package com.darakay.testapp.testapp.tariff;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TariffRepository extends CrudRepository<Tariff, Long> {
    Optional<Tariff> findByName(String name);
}
