package com.example.cryptocoffee.blockchainapi.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {

    private String rfid;

    private String corporateKey;

    private String firstName;

    private String lastName;

    private String ingMail;

    private String googleMail;

}
