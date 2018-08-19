package com.example.cryptocoffee.blockchainapi.controllers;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cryptoCoffee")
public class RfidController {

    @Autowired
    private RfidScanner rfidScanner;

    @CrossOrigin("*")
    @RequestMapping(method = RequestMethod.GET, path = "/rfid/registration")
    public ResponseEntity getRfidForRegistration() {
        String rfid = null;
        rfid = rfidScanner.getRfidForRegistration();
        System.out.println("rfid from registration " + rfid);
        if (rfid != null) {
            return new ResponseEntity<>(rfid, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
        }
    }

    @CrossOrigin("*")
    @RequestMapping(method = RequestMethod.POST, path = "/rfid/mode")
    public ResponseEntity setMode(@RequestBody RegistrationModeRequest request) {
        rfidScanner.setCoffeeMode(request.isCoffeeMode());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Getter
    static class RegistrationModeRequest {
        boolean coffeeMode;
    }
}
