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
    private InputStream in;
    private OutputStream out;
    private final String name;
    private boolean isRunning;
    private FileInputStream fis;
    private FileOutputStream fos;
    private BufferedInputStream bis;
    private BufferedOutputStream bos;
    private File dir;
    private File file;

    public Handler(Socket socket) throws IOException {
        in = socket.getInputStream();
        out = socket.getOutputStream();
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
            fos = new FileOutputStream(file);
            log.debug("fos open");
            bos = new BufferedOutputStream(fos);
            log.debug("bos open");
            int amountData = -1;
            byte[] arrayBuf = new byte[1024 * 8];
            while ((amountData = in.read(arrayBuf)) != -1) {
                bos.write(arrayBuf, 0, amountData);
                bos.flush();
                log.debug("write data");
                bos.close();
            }

            log.debug("Received: {}", file.getName());
//                String response = String.format("%s %s: %s", getDate(), name, file.getName());
//                log.debug("Message for response: {}", response);

        } catch (IOException e) {
            log.error("", e);
        }
    }
}