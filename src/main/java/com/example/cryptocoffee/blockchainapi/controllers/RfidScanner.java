package com.example.cryptocoffee.blockchainapi.controllers;

import com.example.cryptocoffee.blockchainapi.domain.RfidMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.cryptocoffee.blockchainapi.domain.RfidMode.COFFEE;
import static com.example.cryptocoffee.blockchainapi.domain.RfidMode.REGISTRATION;

@Service
public class RfidScanner {

    public static AtomicBoolean IN_COFFEE_MODE = new AtomicBoolean(true);
    public Map<RfidMode, Thread> threadMap = new HashMap<>();
    boolean fakeMode;

    @Value("${rfid.script.map}")
    private String RFID_SCRIPT_MAP;

    @Value("${rfid.script.name}")
    private String RFID_SCRIPT_NAME;

    @Value("${rfid.storage.map}")
    private String RFID_STORAGE_MAP;

    @PostConstruct
    public void init() {
        if (!Files.exists(Paths.get(RFID_SCRIPT_MAP + RFID_SCRIPT_NAME))) {
            fakeMode = true; // in fake mode
            createTempRfidDir();
        }
        startCoffeeMode();
    }

    public void startCoffeeMode() {
        System.out.println("starting Coffee Mode");
        if (threadMap.containsKey(REGISTRATION)) {
            threadMap.get(REGISTRATION).interrupt();
        }
        Thread coffeeThread = new Thread(new CoffeeRfidThread(), "coffeeThread");
        threadMap.put(COFFEE, coffeeThread);
        coffeeThread.start();
    }

    public void startRegistrationMode() {
        System.out.println("starting Registration Mode");
        if (threadMap.containsKey(COFFEE)) {
            threadMap.get(COFFEE).interrupt();
        }
    }

    public void setCoffeeMode(boolean coffeeMode) {
        IN_COFFEE_MODE.getAndSet(coffeeMode);
        if (coffeeMode) {
            startCoffeeMode();
        } else {
            startRegistrationMode();
        }
    }

    public String getRfid() throws InterruptedException {
        String rfid = null;
        try {
            UUID uuidCall = UUID.randomUUID();

            if (!fakeMode) {
                // script should:
                // 1. accept one argument containing the uuid
                // 2. retrieve the rfid
                // 3. create a file in the rfid_map directory named as the uuid,
                //    containing a line with format <uuid>:<retreived rfid>
                // 4. return exit status 0 if all is ok.
                runScript(uuidCall);
                rfid = findRfid(uuidCall);
            } else {
                fakeScript(uuidCall);
                rfid = findRfid(uuidCall);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rfid;
    }

    public String getRfidForRegistration() {
        System.out.println("getting rfid for registration mode");
        threadMap.get(COFFEE).interrupt();

        String rfid = null;
        try {
            rfid = getRfid();
        } catch (InterruptedException e) {
            System.out.println("interrupted in getRfidForRegistration");
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

    private void fakeScript(UUID uuidCall) throws InterruptedException {
        Thread.sleep(5_000L);
        Path file = Paths.get(RFID_STORAGE_MAP + uuidCall.toString());
        try(PrintWriter pw = new PrintWriter(file.toFile())) {
            pw.println(uuidCall.toString() + ":" + "1345232");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public class CoffeeRfidThread implements Runnable {
        @Override
        public void run() {
            while (IN_COFFEE_MODE.get()) {
                try {
                    System.out.println("getting rfid in coffee mode");
                    String rfid = getRfid();
                    System.out.println("found rfid " + rfid);
                    System.out.println("run coffee making script");
                } catch (InterruptedException e) {
                    System.out.println("interrupted in coffee thread");
                }
            }
        }
    }

    private void createTempRfidDir() {
        try {
            RFID_STORAGE_MAP = "target/fakeRfidMap/";
            deleteDir(RFID_STORAGE_MAP);
            Files.createDirectory(Paths.get(RFID_STORAGE_MAP));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteDir(String rfidStorageMap) throws IOException {
        Path rfidMap = Paths.get(rfidStorageMap);
        if (Files.exists(rfidMap)) {
            for (File file : new File(rfidStorageMap).listFiles()) {
                file.delete();
            }
        }
        Files.deleteIfExists(rfidMap);
    }
}
