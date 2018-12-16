package com.example.cryptocoffee.blockchainapi.controllers;

import com.example.cryptocoffee.blockchainapi.domain.User;
import com.example.cryptocoffee.blockchainapi.domain.UserRequest;
import com.example.cryptocoffee.blockchainapi.service.TransactionService;
import com.example.cryptocoffee.blockchainapi.service.UserService;
import com.example.cryptocoffee.blockchainapi.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/cryptoCoffee")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private TransactionService transactionService;

    @Value("${account.initialFunds}")
    private String initialFunds;

    @CrossOrigin("*")
    @RequestMapping(method = RequestMethod.GET, path = "/user/{id}")
    public ResponseEntity findUser(@PathVariable("id") String id) {
        Optional<User> user = userService.findUser(id);
        if (user.isPresent()) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            String errorMessage = "{\"error\": \"customer not found\"}";
            return new ResponseEntity<>(errorMessage, HttpStatus.OK);
        }
    }

    @CrossOrigin("*")
    @RequestMapping(method = RequestMethod.POST, path = "/user")
    public ResponseEntity createUser(@RequestBody UserRequest request) throws Exception {
        System.out.println("Request received: " + request);

        Optional<User> existingUser = userService.findUserByRfid(request.getRfid());
        if (existingUser.isPresent()) {
            String errorMessage = "{\"error\", \"user with this rfid already exists\"}";
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        } else {
            String walletAddress = walletService.createNewLightWalletFile(request.getRfid());
            //String walletAddress = "dummyWallet";
            System.out.println("WalletFile Address: " + "0x" + walletAddress);

            User user = new User(request, walletAddress);
            userService.save(user);
            System.out.println("New user registered: " + user + ". All current wallets: " + walletService.getWallets());

            TransactionReceipt transactionReceipt = transactionService.doTransactionFromBank(
                    walletAddress, new BigDecimal(initialFunds));
            return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
        }
    }
}
