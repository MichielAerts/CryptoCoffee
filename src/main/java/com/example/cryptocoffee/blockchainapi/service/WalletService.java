package com.example.cryptocoffee.blockchainapi.service;

import com.example.cryptocoffee.blockchainapi.configuration.Web3jProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class WalletService {

    @Autowired
    private Web3j web3j;

    @Autowired
    private Web3jProperties properties;

    public List<String> getWallets() throws IOException {
        return web3j.ethAccounts().send().getAccounts();
    }

    public BigDecimal getBalanceinEther(String id) throws InterruptedException, ExecutionException {
        EthGetBalance balance = web3j.ethGetBalance(id, DefaultBlockParameterName.LATEST).sendAsync().get();
        return Convert.fromWei(balance.getBalance().toString(), Convert.Unit.ETHER);
    }

    public File getWalletFile(String walletAddress) {
        File dir = new File(properties.getKeystore());
        String[] files = dir.list((dir1, name) -> name.contains(walletAddress));
        if (files ==  null || files.length == 0) {
            throw new RuntimeException("Wallet address " + walletAddress + " not found");
        } else {
            return new File(properties.getKeystore() + "/" + files[0]);
        }
    }

    public String createNewLightWalletFile(String rfid) throws Exception {
        //String walletFileName = WalletUtils.generateFullNewWalletFile(request.getRfid(),new File(properties.getKeystore()));
        String walletFileName = WalletUtils.generateLightNewWalletFile(rfid, new File(properties.getKeystore()));
        System.out.println(walletFileName);
        String[] fetchAddress = walletFileName.split("--");
        return fetchAddress[fetchAddress.length-1].split("\\.")[0];
    }
}
