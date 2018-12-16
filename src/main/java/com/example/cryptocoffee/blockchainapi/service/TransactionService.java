package com.example.cryptocoffee.blockchainapi.service;

import com.example.cryptocoffee.blockchainapi.configuration.Web3jProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private Web3jProperties properties;

    @Autowired
    private Web3j web3j;

    @Value("${bank.wallet.address}")
    private String walletAddressBank;

    @Value("${bank.wallet.passwordFile}")
    private String passwordFileBank;

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;

    public TransactionReceipt doTransactionToBank(String rfId, String walletAddress, BigDecimal amount) throws Exception {
        Credentials credentials = WalletUtils.loadCredentials(rfId, walletService.getWalletFile(walletAddress));
        TransactionReceipt receipt = Transfer.sendFunds(
                web3j, credentials, walletAddressBank, amount, Convert.Unit.ETHER).send();
        System.out.println("Transaction to Bank; Receipt: " + receipt);
        return receipt;
    }

    public TransactionReceipt doTransactionFromBank(String walletAdress, BigDecimal amount) throws Exception {
        String pw = Files.readAllLines(Paths.get(passwordFileBank)).get(0);
        Credentials credentials = WalletUtils.loadCredentials(pw, walletService.getWalletFile(walletAddressBank));
        TransactionReceipt receipt = Transfer.sendFunds(
                web3j, credentials, walletAdress, amount, Convert.Unit.ETHER).send();
        System.out.println("Transaction from Bank; Receipt: " + receipt);
        return receipt;
    }

    public boolean payForCoffee(String rfid) {
        boolean status = false;
        Optional<String> walletAdress = userService.findWalletAddressByRfid(rfid);
        if (walletAdress.isPresent()) {
            try {
                doTransactionToBank(rfid, walletAdress.get(), BigDecimal.valueOf(1.0));
                status = true;
            } catch (Exception e) {
                throw new RuntimeException("Exception during transaction for rfid " + rfid, e);
            }
        }
        return status;
    }
}
