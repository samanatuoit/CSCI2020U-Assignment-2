package sample;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        // Check what command server received and call the appropriate method
        String line;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
            while ((line = in.readLine()) != null) {
                System.out.println("Command received: " + line);
                out.println("The command you gave me was " + line);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
    private void getDirectoryListing() {
        File remoteDir = new File("C:\\Users\\Saman\\Desktop\\remotetest");
        //File[] fileList = remoteDir.list();
    }

}