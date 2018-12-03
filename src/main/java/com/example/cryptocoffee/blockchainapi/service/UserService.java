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

    private Optional<User> findUserByCorporateKey(String id) {
        return userRepository.findByCorporateKey(id);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public Optional<String> findWalletAddressByRfid(String rfid) {
        return findUserByRfid(rfid).map(User::getWalletAddress);
    }

    public Optional<User> findUser(String id) {
        Optional<User> user = findUserByRfid(id);
        if (user.isPresent()) {
            return user;
        } else {
            return findUserByCorporateKey(id);
        }
    }
}
