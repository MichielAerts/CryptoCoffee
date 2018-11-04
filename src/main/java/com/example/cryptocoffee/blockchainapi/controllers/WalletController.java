package com.example.cryptocoffee.blockchainapi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/cryptoCoffee")
public class WalletController {

    @Autowired
    Web3j web3j;

    @CrossOrigin("*")
    @RequestMapping(method = RequestMethod.GET, path = "/wallets")
    public List<String> getWallets() throws IOException {
        return web3j.ethAccounts().send().getAccounts();
    }

    @CrossOrigin("*")
    @RequestMapping(method = RequestMethod.GET, path = "/wallet/{id}")
    public ResponseEntity<?> checkWalletExist(@PathVariable String id) throws IOException {
        List<String> accounts = web3j.ethAccounts().send().getAccounts();
        if(accounts.contains(id)){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @CrossOrigin("*")
    @RequestMapping(method = RequestMethod.GET, path = "/wallet/{id}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable String id) throws ExecutionException, InterruptedException {
        EthGetBalance balance = web3j.ethGetBalance(id, DefaultBlockParameterName.LATEST).sendAsync().get();
        BigDecimal ethBalance = Convert.fromWei(balance.getBalance().toString(), Convert.Unit.ETHER);
        return new ResponseEntity<BigDecimal>(ethBalance,HttpStatus.OK);
    }
}
