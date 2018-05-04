package com.example.cryptocoffee.blockchainapi.repository;

import com.example.cryptocoffee.blockchainapi.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, String> {

    Optional<User> findByCorporateKey(String corporateKey);

    Optional<User> findById(String rfid);
}
