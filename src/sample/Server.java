package sample;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private Socket clientSocket;
    private ServerSocket serverSocket;
    private File serverDirectory;
    private ArrayList<FileRecord> fileRecordArrayList;
    private File[] filesList;
    BufferedReader in;


    public static void main(String[] args) {
        /*try {
            ServerSocket serverSocket = new ServerSocket(7000);
            while (true) {
                clientSocket = serverSocket.accept();
                //ConnectionHandler clientHandler = new ConnectionHandler(clientSocket);
                //Thread t = new Thread(clientHandler);
                //t.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //Server server = new Server();
        new Server().go();

    }
    public void go() {
        // Gather all filenames in the server's shared directory
        serverDirectory = new File("C:\\Users\\Saman\\Desktop\\remotetest");


        // Start listening for connections
        try {
            serverSocket = new ServerSocket(7000);
            while (true) {
                System.out.println("Now listening for connections");
                clientSocket = serverSocket.accept();
                System.out.println("Client connected");
                Thread t = new Thread(new ClientConnectionHandler());
                t.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public class ClientConnectionHandler implements Runnable {

        @Override
        public void run() {
            getClientCommand();
        }
        private void disconnect() {
            try {
                clientSocket.close();
                System.out.println("Disconnected client");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private synchronized void getClientCommand() {
            String clientCommand;
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                clientCommand = in.readLine();
                String clientCommandTokens[] = clientCommand.split(" ");
                for (int i = 0; i < clientCommandTokens.length; i++) {
                    String entry = clientCommandTokens[i];
                    System.out.println("clientCommandTokens[" + i + "] = " + clientCommandTokens[i]);
                }

                if (clientCommandTokens[0].equals("DIR")) {
                    System.out.println("Received command: " + clientCommandTokens[0]);
                    sendFilesList();
                }
                else if (clientCommandTokens[0].equals("UPLOAD")) {
                    System.out.println("Received command: " + clientCommandTokens[0]);
                    receiveFile(clientCommandTokens[1]);
                }
                else if (clientCommandTokens[0].equals("DOWNLOAD")) {
                    System.out.println("Received command: " + clientCommandTokens[0]);
                    sendFile(clientCommandTokens[1]);
                }
                else {
                    System.out.println("Unknown command");
                    disconnect();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        private synchronized void sendFilesList() {
            // We put the files into an ArrayList so later if the client wants to download that file,
            // we will check every entry of fileRecordArrayList to find the exact file
            fileRecordArrayList = new ArrayList<>();
            filesList = serverDirectory.listFiles();
            try {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
                //out.println("Heres a files list");
                for (File entry : filesList) {
                    System.out.println("entry.geName() = " + entry.getName());
                    out.println(entry.getName());
                    out.flush();
                    fileRecordArrayList.add(new FileRecord(entry));
                }
                out.println("\0");
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            disconnect();

        }

        private synchronized void receiveFile(String fileName) {
            System.out.println("Attempting to receive filename: " + fileName);
            File receiveFile = new File(serverDirectory.getPath() + "\\" + fileName);
            System.out.println("File created: " + serverDirectory.getPath() + "\\" + fileName);
            String line;
            try {
                PrintWriter fout = new PrintWriter(receiveFile);
                //BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                while ((line = in.readLine()) != null) {
                    if (line.equals("\0")) {
                        break;
                    }
                    System.out.println("File content: " + line);
                    fout.println(line);
                    fout.flush();
                    //System.out.println("Line printed successfully");
                }
                fout.close();
                System.out.println("File successfully uploaded");
            } catch (IOException e) {
                e.printStackTrace();
            }
            disconnect();
            //sendFilesList();

        }
        private synchronized void sendFile(String fileName) {
            int index = -1;
            System.out.println("fileName = " + fileName);
            for (FileRecord entry : fileRecordArrayList) {
                System.out.println("entry.getFileName() = " + entry.getFileName());
                if (entry.getFileName().equals(fileName)) {
                    System.out.println("We have a match!");
                    index = fileRecordArrayList.indexOf(entry);
                }
            }
            String line;
            //System.out.println("get(index) = " + fileRecordArrayList.get(index));
            try {
                in = new BufferedReader(new FileReader(serverDirectory + "\\" + fileRecordArrayList.get(index).getFileName()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
                while ((line = in.readLine()) != null) {
                    out.println(line);
                    out.flush();
                }
                out.println("\0");
                out.flush();


            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


}
