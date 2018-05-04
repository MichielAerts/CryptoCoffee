package com.example.cryptocoffee.blockchainapi.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@ToString
public class User {

    @Id
    private String rfid;

    private String corporateKey;

    private String firstName;

    private String lastName;

    private String ingMail;

    private String walletAddress;

    private String googleMail;

}


