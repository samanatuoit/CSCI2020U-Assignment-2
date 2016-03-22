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
import java.net.Socket;

public class Client extends Application {
    TableView<FileRecord> localFilesTable;
    TableView<FileRecord> remoteFilesTable;
    Socket clientSocket;
    BufferedReader in;
    PrintWriter out;
    private File myDirectory;

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));


        Group root = new Group();
        BorderPane layout = new BorderPane();
        Button uploadBtn = new Button("Upload");
        Button downloadBtn = new Button("Download");
        Button exitBtn = new Button("Exit");
        exitBtn.setOnAction(evt -> System.exit(0));
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.add(uploadBtn, 0, 0);
        gridPane.add(downloadBtn, 1, 0);
        gridPane.add(exitBtn, 2, 0);
        layout.setTop(gridPane);

        localFilesTable = new TableView<>();
        remoteFilesTable = new TableView<>();

        //localFilesTable.setItems(connectionHandler.getLocalFiles());

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

        try {
            clientSocket = new Socket("127.0.0.1", 7000);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream());
            out.println("DIR");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ConnectionHandler connectionHandler = new ConnectionHandler();
        Thread t = new Thread(connectionHandler);
        t.start();
        getLocalFiles();




        //remoteFilesTable.setItems(getRemoteFilesList());

        //setUpNetworking();
    }
    private void getLocalFiles() {
        // Lets try populating the localFilesTable with our own local files
        ObservableList<FileRecord> localFiles = FXCollections.observableArrayList();
        myDirectory = new File("C:\\Users\\Saman\\Desktop\\localtest");
        File[] fileList = myDirectory.listFiles();
        for (File entry : fileList) {
            if (entry.isFile()) {
                //System.out.println("entry = " + entry);
                localFiles.add(new FileRecord(entry));
            }

        }
        //System.out.println("localFiles = " +  localFiles);
        localFilesTable.setItems(localFiles);

        //return localFiles;

    }
    public class ConnectionHandler implements Runnable {

        @Override
        public void run() {
            //Connect();
            getRemoteFilesList();
        }

        /*private void Connect() {
            try {
                sock = new Socket("127.0.0.1", 7000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        private void getRemoteFilesList() {
            // Get list of remote files by sending server "DIR" command
            ObservableList<FileRecord> remoteFiles = FXCollections.observableArrayList();
            String fileName;
            try {
                //PrintWriter out = new PrintWriter(sock.getOutputStream());
                //out.println("DIR");
                //out.flush();
                // After giving server the command, wait for its response
                //ServerSocket serverSocket = new ServerSocket(7001);
                System.out.println("Waiting for server response to sent command");
                //Socket listenSocket = serverSocket.accept();
                //BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                while ((fileName = in.readLine()) != null) {
                    if (fileName.equals("\0")) {
                        break;
                    }
                    //System.out.println("remote fileName = " + fileName);
                    remoteFiles.add(new FileRecord(fileName));

                }


            } catch (IOException e) {
                e.printStackTrace();
            }

            remoteFilesTable.setItems(remoteFiles);
            //System.out.println("remoteFilesTable = " + remoteFilesTable);
        }

    }



    public static void main(String[] args) {
        launch(args);
    }
}
