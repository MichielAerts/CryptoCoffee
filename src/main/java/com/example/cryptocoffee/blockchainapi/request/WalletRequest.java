package com.example.cryptocoffee.blockchainapi.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletRequest {

    private String rfid;

    private String corporateKey;

    private String firstName;

    private String surName;

    private String ingMail;

    private String googleMail;

}
