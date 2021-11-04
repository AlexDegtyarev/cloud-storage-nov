package com.geekbrains;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Slf4j
public class Handler implements Runnable {

    private static int counter = 0;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private BufferedInputStream bis;
    private BufferedOutputStream bos;
    private final String name;
    private boolean isRunning;
    private FileInputStream fis;
    private File dir;
    private File file;
    public final static int FILE_SIZE = Integer.MAX_VALUE;

    public Handler(Socket socket) throws IOException {
        bis = new BufferedInputStream(socket.getInputStream());
        bos = new BufferedOutputStream(socket.getOutputStream());
        counter++;
        name = "User#" + counter;
        log.debug("Set nick: {} for new client", name);
        isRunning = true;
    }

    private String getDate() {
        return formatter.format(LocalDateTime.now());
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    @Override
    public void run() {
        try {
            dir = new File("cloud-storage-nov-server/server");
            boolean createDir = dir.mkdir();
            if (createDir) {
                log.debug("Directory is created: {}", dir.getName());
            }
            file = new File(dir, "layout.fxml");
            boolean createFile = file.createNewFile();
            if (createFile) {
                log.debug("File is created: {}", file.getName());
            }
            while (isRunning) {
                byte[] arrayData = new byte[(int) file.length()];
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                bis.read(arrayData, 0, arrayData.length);
                bos.write(arrayData, 0, arrayData.length);
                bos.flush();
//                log.debug("Received: {}", file.getName());
//                String response = String.format("%s %s: %s", getDate(), name, file.getName());
//                log.debug("Message for response: {}", response);
                try {
                    bos.close();
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }
}