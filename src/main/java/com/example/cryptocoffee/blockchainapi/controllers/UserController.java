package com.example.cryptocoffee.blockchainapi.controllers;

import com.example.cryptocoffee.blockchainapi.configuration.Web3jProperties;
import com.example.cryptocoffee.blockchainapi.domain.User;
import com.example.cryptocoffee.blockchainapi.domain.UserRequest;
import com.example.cryptocoffee.blockchainapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Optional;

@RestController
@RequestMapping("/api/cryptoCoffee")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    Web3j web3j;

    @Autowired
    private Web3jProperties properties;

    @CrossOrigin("*")
    @RequestMapping(method = RequestMethod.GET, path = "/user/{id}")
    public ResponseEntity findUser(@PathVariable("id") String id) {
        Optional<User> user = userService.findUserByRfid(id);
        if (user.isPresent()) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            user = userService.findByCorporateKey(id);
            if (user.isPresent()) {
                return new ResponseEntity<>(user, HttpStatus.OK);
            } else {
                String errorMessage = "{\"error\": \"customer not found\"}";
                return new ResponseEntity<>(errorMessage, HttpStatus.OK);
            }
        }
    }

    @CrossOrigin("*")
    @RequestMapping(method = RequestMethod.POST, path = "/user")
    public ResponseEntity createUser(@RequestBody UserRequest request) throws CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
        System.out.println("Request received: " + request);

        Optional<User> existingUser = userService.findUserByRfid(request.getRfid());
        if (existingUser.isPresent()) {
            String errorMessage = "{\"error\", \"user with this rfid already exists\"}";
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        } else {
            //String walletFileName = WalletUtils.generateFullNewWalletFile(request.getRfid(),new File(properties.getKeystore()));
            String walletFileName = WalletUtils.generateLightNewWalletFile(request.getRfid(),new File(properties.getKeystore()));

            System.out.println(walletFileName);
            String[] fetchAddress = walletFileName.split("--");
            String walletAddress = fetchAddress[fetchAddress.length-1].split("\\.")[0];
//            String walletAddress = "dummyWallet";
            System.out.println("walletFile Address>>>>>" + "0x" + walletAddress);
            System.out.println(web3j.ethAccounts().send().getAccounts());
            User user = new User(request, walletAddress);
            System.out.println("new user registered: " + user);
            userService.save(user);
            return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
        }
    }
}
