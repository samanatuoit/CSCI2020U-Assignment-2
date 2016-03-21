package sample;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket serverSocket;
    private Socket sock;

    public Server (Socket sock) {
        this.sock = sock;
    }

    public static void main(String[] args) {



    }
    public class ClientHandler implements Runnable {
        private Socket sock;

        public ClientHandler(Socket sock) {
            this.sock = sock;
        }

        private void handleConnections() {
            try {
                serverSocket = new ServerSocket(7000);
                sock = serverSocket.accept();
                System.out.println("Connection received");
                // Send the server the client socket
                Thread t = new Thread(new Server(sock));
                t.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            // Check what command server received and call the appropriate method
        }
        private void getDirectoryListing() {
            File remoteDir = new File("C:\\Users\\Saman\\Desktop\\remotetest");
            //File[] fileList = remoteDir.list();
        }

    }



}
