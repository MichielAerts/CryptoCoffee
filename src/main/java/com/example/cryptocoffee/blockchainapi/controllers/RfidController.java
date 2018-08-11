package com.example.cryptocoffee.blockchainapi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cryptoCoffee")
public class RfidController {

    @Autowired
    private RfidScanner rfidScanner;

    @CrossOrigin("*")
    @RequestMapping(method = RequestMethod.GET, path = "/register/rfid")
    public ResponseEntity getRfidForRegistration() {
        String rfid = null;
        rfid = rfidScanner.getRfidForRegistration();

        if (rfid != null) {
            return new ResponseEntity<>(rfid, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
        }
    }
}
