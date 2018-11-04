package com.example.cryptocoffee.blockchainapi.service;

import com.example.cryptocoffee.blockchainapi.domain.User;
import com.example.cryptocoffee.blockchainapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> findUserByRfid(String rfid) {
        return userRepository.findById(rfid);
    }

    public Optional<User> findByCorporateKey(String id) {
        return userRepository.findByCorporateKey(id);
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
