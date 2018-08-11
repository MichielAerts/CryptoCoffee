package com.example.cryptocoffee.blockchainapi.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RfidScanner {

    public static final String COFFEE = "Coffee";
    public static AtomicBoolean IN_REGISTRATION_MODE = new AtomicBoolean(false);
    public Map<String, Thread> threadMap = new HashMap<>();

    @Value("${rfid.script.map}")
    private String RFID_SCRIPT_MAP;

    @Value("${rfid.script.name}")
    private String RFID_SCRIPT_NAME;

    @Value("${rfid.storage.map}")
    private String RFID_STORAGE_MAP;

    @PostConstruct
    public void init() {
        Thread coffeeThread = new Thread(new CoffeeRfidThread());
        threadMap.put(COFFEE, coffeeThread);
        coffeeThread.start();
    }

//    public String getRfid() throws InterruptedException {
//        Thread.sleep(2_000L);
//        return "test";
//    }

    public String getRfid() throws InterruptedException {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rfid;
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

    public String getRfidForRegistration() {
        IN_REGISTRATION_MODE.getAndSet(true);
        threadMap.get(COFFEE).interrupt();

        String rfid = null;
        try {
            rfid = getRfid();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        IN_REGISTRATION_MODE.getAndSet(false);
        return rfid;
    }

    public class CoffeeRfidThread implements Runnable {

        @Override
        public void run() {
            while (!IN_REGISTRATION_MODE.get()) {
                try {
                    String rfid = getRfid();
                    System.out.println(rfid);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
