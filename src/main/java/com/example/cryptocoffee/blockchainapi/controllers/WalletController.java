package com.example.cryptocoffee.blockchainapi.controllers;

import com.example.cryptocoffee.blockchainapi.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/cryptoCoffee")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @CrossOrigin("*")
    @RequestMapping(method = RequestMethod.GET, path = "/wallets")
    public List<String> getWallets() throws IOException {
        return walletService.getWallets();
    }

    @CrossOrigin("*")
    @RequestMapping(method = RequestMethod.GET, path = "/wallet/{id}")
    public ResponseEntity<?> checkWalletExist(@PathVariable String id) throws IOException {
        List<String> wallets = walletService.getWallets();
        if(wallets.contains(id)){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @CrossOrigin("*")
    @RequestMapping(method = RequestMethod.GET, path = "/wallet/{id}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable String id) throws ExecutionException, InterruptedException {
        BigDecimal ethBalance = walletService.getBalanceinEther(id);
        return new ResponseEntity<BigDecimal>(ethBalance,HttpStatus.OK);
    }


}
