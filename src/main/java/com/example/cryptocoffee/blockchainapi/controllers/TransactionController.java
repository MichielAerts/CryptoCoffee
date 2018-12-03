package com.example.cryptocoffee.blockchainapi.controllers;

import com.example.cryptocoffee.blockchainapi.service.TransactionService;
import com.example.cryptocoffee.blockchainapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/cryptoCoffee")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @CrossOrigin("*")
    @RequestMapping(method = RequestMethod.POST, path = "/transaction/{rfId}")
    public ResponseEntity payForCoffee(@PathVariable String rfid) throws Exception {

        //TODO
        Optional<String> walletAdress = userService.findWalletAddressByRfid(rfid);
        if (walletAdress.isPresent()) {
            TransactionReceipt transactionReceipt = transactionService.doTransactionToBank(rfid, walletAdress.get(), BigDecimal.valueOf(1.0));
            System.out.println(transactionReceipt.getStatus());
            return new ResponseEntity(HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
}
