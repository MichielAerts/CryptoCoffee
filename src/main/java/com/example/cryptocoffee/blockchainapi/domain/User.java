package com.example.cryptocoffee.blockchainapi.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class User {

    @Id
    private String rfid;

    private String corporateKey;

    private String firstName;

    private String lastName;

    private String ingMail;

    private String walletAddress;

    private String googleMail;

    public User(UserRequest request, String walletAddress) {
        this.rfid = request.getRfid();
        this.firstName = request.getFirstName();
        this.lastName = request.getLastName();
        this.corporateKey = request.getCorporateKey();
        this.ingMail = request.getIngMail();
        this.googleMail = request.getGoogleMail();
        this.walletAddress = walletAddress;
    }
}


