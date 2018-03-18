package com.example.cryptocoffee.blockchainapi;

import com.example.cryptocoffee.blockchainapi.configuration.Web3jProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.NewAccountIdentifier;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import javax.xml.ws.Response;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

@RestController
public class Transaction {

    @Autowired
    private Web3jProperties properties;

    @Autowired
    private Web3j web3j;


    @CrossOrigin("*")
    @RequestMapping(method = RequestMethod.POST, path = "/account")
    public String createAccount() throws Exception {
        Admin admin = Admin.build(new HttpService("http://127.0.0.1:8042"));
        System.out.println("Inside");

        File dir = new File(properties.getKeystore());
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept (File dir, String name) {
                return name.contains("2b6d3fb94706a57b68de46f86b2fc58fc32cb698");
            }
        };

        String[] files = dir.list(filter);
        Credentials credentials = WalletUtils.loadCredentials("twenty20",new File(properties.getKeystore()+"/"+files[0]));
        TransactionReceipt transactionReceipt = Transfer.sendFunds(
                web3j, credentials, "0xa7acfeb068dd3c9e271c2b3a4a2794c9f185dd1b" ,
                BigDecimal.valueOf(1.0), Convert.Unit.ETHER).send();


    return "Hello";
    }

    BigInteger getNonce(String address) throws Exception {
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                address, DefaultBlockParameterName.LATEST).sendAsync().get();

        return ethGetTransactionCount.getTransactionCount();
    }
}
