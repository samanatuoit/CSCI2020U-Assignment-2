package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Client extends Application {
    TableView<FileRecord> localFilesTable;
    TableView<FileRecord> remoteFilesTable;
    Socket sock;

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Group root = new Group();
        BorderPane layout = new BorderPane();
        Button uploadBtn = new Button("Upload");
        Button downloadBtn = new Button("Download");
        Button connectBtn = new Button("Connect");
        connectBtn.setOnAction(evt -> Connect());
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.add(uploadBtn, 0, 0);
        gridPane.add(downloadBtn, 1, 0);
        gridPane.add(connectBtn, 2, 0);
        layout.setTop(gridPane);

        localFilesTable = new TableView<>();
        remoteFilesTable = new TableView<>();

        localFilesTable.setItems(getLocalFiles());

        TableColumn<FileRecord, String> localFilesCol = new TableColumn<>();
        localFilesCol.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        localFilesCol.setMinWidth(300);
        localFilesTable.getColumns().add(localFilesCol);

        TableColumn<FileRecord, String> remoteFilesCol = new TableColumn<>();
        remoteFilesCol.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        remoteFilesCol.setMinWidth(300);
        remoteFilesTable.getColumns().add(remoteFilesCol);

        layout.setLeft(localFilesTable);
        layout.setRight(remoteFilesTable);



        root.getChildren().add(layout);

        primaryStage.setTitle("File Sharer v1.0");
        Scene scene = new Scene(root, 600, 800);
        primaryStage.setScene(scene);
        primaryStage.show();



        //remoteFilesTable.setItems(getRemoteFiles());

        //setUpNetworking();
    }
    private void Connect() {
        try {
            this.sock = new Socket("127.0.0.1", 7000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        getRemoteFiles();
    }
    private ObservableList<FileRecord> getLocalFiles() {
        // Lets try populating the localFilesTable with our own local files
        ObservableList<FileRecord> myfiles = FXCollections.observableArrayList();
        File myDirectory = new File("C:\\Users\\Saman\\Desktop\\localtest");
        File[] fileList = myDirectory.listFiles();
        for (File entry : fileList) {
            myfiles.add(new FileRecord(entry.getName()));
            System.out.println(entry);
        }

        return myfiles;

    }
    private ObservableList<FileRecord> getRemoteFiles() {
        // Get list of remote files by sending server "DIR" command
        ObservableList<FileRecord> remoteFiles = null;
        String fileName;
        try {
            PrintWriter out = new PrintWriter(sock.getOutputStream());
            out.println("DIR");
            out.flush();
            // After giving server the command, wait for its response
            //ServerSocket serverSocket = new ServerSocket(7001);
            System.out.println("Waiting for server response to sent command");
            //Socket listenSocket = serverSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            while ((fileName = in.readLine()) != null) {
                System.out.println(fileName);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return remoteFiles;
    }
    private void setUpNetworking() {
        try {
            this.sock = new Socket("127.0.0.1", 7000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
