package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class Client extends Application {
    private TableView<FileRecord> localFilesTable;
    private TableView<FileRecord> remoteFilesTable;
    private TableColumn<FileRecord, String> localFilesCol;
    private TableColumn<FileRecord, String> remoteFilesCol;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private BufferedReader fileIn;
    private File myDirectory;
    private ConnectionHandler connectionHandler;
    private PrintWriter fout;

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        // Setup the GUI
        Group root = new Group();
        BorderPane layout = new BorderPane();
        Button uploadBtn = new Button("Upload");
        uploadBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Send the filename located in a specific cell in the TableColumn to the server
                TablePosition tablePosition = localFilesTable.getSelectionModel().getSelectedCells().get(0);
                int row = tablePosition.getRow();
                FileRecord fileRecord = localFilesTable.getItems().get(row);
                TableColumn column = tablePosition.getTableColumn();
                String fileName = (String) column.getCellObservableValue(fileRecord).getValue();
                System.out.println("fileName selected = " + fileName);
                connect();
                out.println("UPLOAD " + fileName);
                out.flush();
                String line;
                try {
                    fileIn = new BufferedReader(new FileReader(myDirectory.getPath() + "\\" + fileName));
                    while ((line = fileIn.readLine()) != null) {
                        System.out.println("File content: " + line);
                        out.println(line);
                        out.flush();
                    }
                    out.println("\0");
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("File upload complete");
                // Update file listing
                if (!remoteFilesTable.getItems().contains(fileRecord)) {
                    remoteFilesTable.getItems().add(fileRecord);
                }



            }
        });
        Button downloadBtn = new Button("Download");
        downloadBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Send the filename located in a specific cell in the TableColumn to the server
                TablePosition tablePosition = remoteFilesTable.getSelectionModel().getSelectedCells().get(0);
                int row = tablePosition.getRow();
                FileRecord fileRecord = remoteFilesTable.getItems().get(row);
                TableColumn column = tablePosition.getTableColumn();
                String fileName = (String) column.getCellObservableValue(fileRecord).getValue();
                System.out.println("fileName selected = " + fileName);
                connect();
                out.println("DOWNLOAD " + fileName);
                out.flush();
                String line;
                File downloadFile = new File(myDirectory.getPath() + "\\" + fileName);
                try {
                    fout = new PrintWriter(downloadFile);
                    //in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    while ((line = in.readLine()) != null) {
                        if (line.equals("\0")) {
                            break;
                        }
                        fout.println(line);
                    }
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Update file listing
                if (!localFilesTable.getItems().contains(fileRecord)) {
                    localFilesTable.getItems().add(fileRecord);
                }

            }
        });
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



        localFilesCol = new TableColumn<>();
        localFilesCol.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        localFilesCol.setMinWidth(300);
        localFilesTable.getColumns().add(localFilesCol);

        remoteFilesCol = new TableColumn<>();
        remoteFilesCol.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        remoteFilesCol.setMinWidth(300);
        remoteFilesTable.getColumns().add(remoteFilesCol);

        layout.setLeft(localFilesTable);
        layout.setRight(remoteFilesTable);



        root.getChildren().add(layout);

        primaryStage.setTitle("File Sharer v1.0");
        Scene scene = new Scene(root, 600, 500);
        primaryStage.setScene(scene);
        primaryStage.show();

        connect();
        ConnectionHandler connectionHandler = new ConnectionHandler();
        Thread t = new Thread(connectionHandler);
        t.start();
        getLocalFiles();


    }
    private void connect() {
        try {
            clientSocket = new Socket("127.0.0.1", 7000);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream());
            System.out.println("Now connected to server");

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void getLocalFiles() {
        // Lets try populating the localFilesTable with our own local files
        ObservableList<FileRecord> localFiles = FXCollections.observableArrayList();
        myDirectory = new File("C:\\Users\\Saman\\Desktop\\localtest");
        File[] fileList = myDirectory.listFiles();
        for (File entry : fileList) {
            if (entry.isFile()) {

                localFiles.add(new FileRecord(entry));
            }

        }

        localFilesTable.setItems(localFiles);



    }
    public class ConnectionHandler implements Runnable {

        @Override
        public void run() {

            getRemoteFilesList();
        }



        public synchronized void getRemoteFilesList() {
            // Get list of remote files by sending server "DIR" command
            ObservableList<FileRecord> remoteFiles = FXCollections.observableArrayList();
            String fileName;
            try {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
                out.println("DIR");
                out.flush();

                System.out.println("Waiting for server response to sent command");

                while ((fileName = in.readLine()) != null) {
                    if (fileName.equals("\0")) {
                        break;
                    }

                    remoteFiles.add(new FileRecord(fileName));

                }


            } catch (IOException e) {
                e.printStackTrace();
            }

            remoteFilesTable.setItems(remoteFiles);

        }

    }



    public static void main(String[] args) {
        launch(args);
    }
}
