package com.example.cryptocoffee.blockchainapi.module;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Entity
@Getter
@Setter
@ToString
public class User {

    @Id
    private String rfid;

    private String corporateKey;

    private String firstName;

    private String surName;

    private String ingMail;

    private String walletAddress;

    private String googleMail;

}


