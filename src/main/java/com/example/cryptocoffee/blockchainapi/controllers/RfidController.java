package com.example.cryptocoffee.blockchainapi.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
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

    @Value("${rfid.script.map}")
    private String RFID_SCRIPT_MAP;

    @Value("${rfid.script.name}")
    private String RFID_SCRIPT_NAME;

    @CrossOrigin("*")
    @RequestMapping(method = RequestMethod.GET, path = "/rfid")
    public ResponseEntity getRfid() {
        String rfid = null;
        try {
            UUID uuidCall = UUID.randomUUID();

            // script should:
            // 1. accept one argument containing the uuid
            // 2. retrieve the rfid
            // 3. create a file in the rfid_map directory named as the uuid,
            //    containing a line with format <uuid>:<retreived rfid>
            // 4. return exit status 0 if all is ok.
            runScript(uuidCall);
            rfid = findRfid(uuidCall);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if (rfid != null) {
            return new ResponseEntity<>(rfid, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
