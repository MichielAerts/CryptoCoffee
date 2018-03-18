package com.example.cryptocoffee.blockchainapi;

import com.example.cryptocoffee.blockchainapi.configuration.Web3jProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

@RestController
public class Wallet {

    @Autowired
    private Web3jProperties properties;

    @Autowired
    Web3j web3j;

    @CrossOrigin("*")
    @RequestMapping(method = RequestMethod.POST, path = "/wallet")
    public void createWallet() throws CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, IOException {

        String walletFileName = WalletUtils.generateFullNewWalletFile("twenty20",new File(properties.getKeystore()));

        System.out.println(walletFileName);

        String[] fetchAddress=walletFileName.split("--");
        String getAddress = fetchAddress[fetchAddress.length-1].split("\\.")[0];

        System.out.println("walletFile Address>>>>>" + "0x" + getAddress);
        System.out.println(web3j.ethAccounts().send().getAccounts());
    }
}
