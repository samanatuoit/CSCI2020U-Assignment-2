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
                clientSocket = serverSocket.accept();
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

        private void getClientCommand() {
            String clientCommand;
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                clientCommand = in.readLine();
                if (clientCommand.equals("DIR")) {
                    System.out.println("Received command: " + clientCommand);
                    sendFilesList();
                }
                //else if (clientCommand.equals()

            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        private void sendFilesList() {
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
                }
                out.println("\0");
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


}
