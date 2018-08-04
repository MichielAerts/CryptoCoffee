package com.example.cryptocoffee.blockchainapi.controllers;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/cryptoCoffee")
public class RfidController {

    @Value("${rfid.storage.map}")
    private String RFID_STORAGE_MAP;

    private static final String REGISTRATION_MODE = "REGISTRATION_MODE";

    @Value("${rfid.script.map}")
    private String RFID_SCRIPT_MAP;

    @Value("${rfid.script.name}")
    private String RFID_SCRIPT_NAME;

    @CrossOrigin("*")
    @RequestMapping(method = RequestMethod.POST, path = "/rfid/mode")
    public ResponseEntity setRegistrationMode(@RequestBody RegistrationModeRequest request) {

        // if the customer is trying to register, we set a status file, so that we use the scan
        //   for registration purposes and not to order coffee

        Path regMode = Paths.get(RFID_SCRIPT_MAP + REGISTRATION_MODE);
        boolean success = false;
        if (request.isRegistrationMode()) {
            if (!Files.exists(regMode)) {
                try {
                    Files.createFile(regMode);
                    success = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                Files.deleteIfExists(regMode);
                success = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (success) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Getter
    @Setter
    public static class RegistrationModeRequest {
        boolean registrationMode;
    }

    @CrossOrigin("*")
    @RequestMapping(method = RequestMethod.GET, path = "/rfid")
    public ResponseEntity getRfid() {
        String rfid = null;
        try {
            UUID uuidCall = UUID.randomUUID();

            if (Files.exists(Paths.get(RFID_SCRIPT_MAP + RFID_SCRIPT_NAME))) {
                // script should:
                // 1. accept one argument containing the uuid
                // 2. retrieve the rfid
                // 3. create a file in the rfid_map directory named as the uuid,
                //    containing a line with format <uuid>:<retreived rfid>
                // 4. return exit status 0 if all is ok.
                runScript(uuidCall);
                rfid = findRfid(uuidCall);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if (rfid != null) {
            return new ResponseEntity<>(rfid, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
        }
    }

    private String findRfid(UUID uuidCall) throws IOException {
        String rfid = null;
        String uuid = uuidCall.toString();
        List<String> fileContents = Files.readAllLines(Paths.get(RFID_STORAGE_MAP + uuid));
        Pattern p = Pattern.compile(uuid + ":(?<rfid>\\d+)");
        for (String line : fileContents) {
            Matcher m = p.matcher(line);
            if (m.matches()) {
                rfid = m.group("rfid");
            }
        }
        return rfid;
    }

    private void runScript(UUID uuidCall) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(RFID_SCRIPT_MAP + RFID_SCRIPT_NAME, uuidCall.toString());
        Process process = pb.start();

        int exitValue = process.waitFor();
        if (exitValue != 0) {
            // TODO check for errors
            throw new RuntimeException("execution of script failed!");
        }
    }
}
