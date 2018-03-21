package com.example.cryptocoffee.blockchainapi;

import com.example.cryptocoffee.blockchainapi.configuration.Web3jProperties;
import com.example.cryptocoffee.blockchainapi.module.User;
import com.example.cryptocoffee.blockchainapi.repository.UserRepository;
import com.example.cryptocoffee.blockchainapi.request.WalletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class Wallet {

    @Autowired
    private Web3jProperties properties;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    Web3j web3j;

    @CrossOrigin("*")
    @RequestMapping(method = RequestMethod.POST, value = {"/wallet", "/customer"})
    public ResponseEntity createWallet(@RequestBody WalletRequest request) throws CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, IOException {

        String walletFileName = WalletUtils.generateFullNewWalletFile(request.getRfid(),new File(properties.getKeystore()));

        System.out.println(walletFileName);

        String[] fetchAddress=walletFileName.split("--");
        String walletAddress = fetchAddress[fetchAddress.length-1].split("\\.")[0];

        System.out.println("walletFile Address>>>>>" + "0x" + walletAddress);
        System.out.println(web3j.ethAccounts().send().getAccounts());
        User user = new User();
        user.setRfid(request.getRfid());
        user.setFirstName(request.getFirstName());
        user.setSurName(request.getSurName());
        user.setCorporateKey(request.getCorporateKey());
        user.setGoogleMail(request.getGoogleMail());
        user.setIngMail(request.getIngMail());
        user.setWalletAddress(walletAddress);
        System.out.println(user);
        userRepository.save(user);

        return new ResponseEntity(user, HttpStatus.ACCEPTED);
    }

    @CrossOrigin("*")
    @RequestMapping(method = RequestMethod.GET, path = "/wallet")
    public List<String> getWallets() throws CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, IOException {


        return web3j.ethAccounts().send().getAccounts();
    }

    @CrossOrigin("*")
    @RequestMapping(method = RequestMethod.GET, path = "/wallet/{id}")
    public ResponseEntity<?> checkWalletExist(@PathVariable String id) throws CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, IOException {

        List<String> accounts = web3j.ethAccounts().send().getAccounts();
        if(accounts.contains(id)){
            return   new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @CrossOrigin("*")
    @RequestMapping(method = RequestMethod.GET, path = "/wallet/{id}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable String id) throws CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, IOException, ExecutionException, InterruptedException {

        EthGetBalance balance = web3j.ethGetBalance(id, DefaultBlockParameterName.LATEST).sendAsync().get();
        BigDecimal ethBalance = Convert.fromWei(balance.getBalance().toString(), Convert.Unit.ETHER);
        return new ResponseEntity<BigDecimal>(ethBalance,HttpStatus.OK);
    }
}
