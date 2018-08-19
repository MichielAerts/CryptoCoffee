package com.example.cryptocoffee.blockchainapi.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserRequest {

    private String rfid;

    private String corporateKey;

    private String firstName;

    private String lastName;

    private String ingMail;

    private String googleMail;

}
