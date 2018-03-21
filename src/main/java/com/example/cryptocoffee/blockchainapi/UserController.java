package com.example.cryptocoffee.blockchainapi;

import com.example.cryptocoffee.blockchainapi.module.User;
import com.example.cryptocoffee.blockchainapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(method = RequestMethod.GET, path = "/customer/{id}")
    public ResponseEntity findUser(@PathVariable("id") String id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            return new ResponseEntity(user, HttpStatus.ACCEPTED);
        } else {
            user = userRepository.findByCorporateKey(id);
            if (user.isPresent()) {
                return new ResponseEntity(user, HttpStatus.ACCEPTED);
            } else {
                String errorMessage = "{\"error\": \"customer not found\"}";
                return new ResponseEntity(errorMessage, HttpStatus.ACCEPTED);
            }
        }
    }


}
