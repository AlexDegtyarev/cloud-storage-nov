package com.geekbrains;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Slf4j
public class MainController implements Initializable {
    private Path clientDir;
    public ListView<String> clientView;
    public ListView<String> serverView;
    public TextField input;
    private InputStream in;
    private OutputStream out;
    private FileOutputStream fos;
    private FileInputStream fis;
    private BufferedInputStream bis;
    private BufferedOutputStream bos;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            clientDir = Paths.get("cloud-storage-nov-client", "client");
            if (!Files.exists(clientDir)) {
                Files.createDirectory(clientDir);
            }
            clientView.getItems().clear();
            clientView.getItems().addAll(getFiles(clientDir));
            clientView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    String item = clientView.getSelectionModel().getSelectedItem();
                    input.setText(item);
                }
            });
            Socket socket = new Socket("localhost", 8189);
            in = socket.getInputStream();
            out = socket.getOutputStream();
            Thread readThread = new Thread(this::read);
            readThread.setDaemon(true);
            readThread.start();
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private List<String> getFiles(Path path) throws IOException {
        return Files.list(path).map(p -> p.getFileName().toString()).collect(Collectors.toList());
    }

    private void read() {
        try {
            while (true) {
//                String msg = is.readUTF();
//                log.debug("Received: {}", msg);
//                Platform.runLater(() -> serverView.getItems().add(msg));
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public void FileToServer(ActionEvent actionEvent)  {
        String nameFile = input.getText();
        try {
            fis = new FileInputStream("cloud-storage-nov-client/client/" + nameFile);
            bis = new BufferedInputStream(fis);
            int length = -1;
            byte [] arrayBuf = new byte[1024];
            while ((length = bis.read(arrayBuf)) != -1) {
                out.write(arrayBuf, 0, length);
            }
            out.flush();
            bis.close();
        } catch (IOException e) {
            log.error("", e);
        }
    }
}