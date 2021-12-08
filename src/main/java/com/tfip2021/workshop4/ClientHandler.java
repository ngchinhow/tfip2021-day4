package com.tfip2021.workshop4;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private String fileName;
    
    public Socket getSocket() { return this.socket; }
    public String getFileName() { return this.fileName; }

    protected void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ClientHandler(Socket socket, String name) {
        this.socket = socket;
        this.fileName = name;
    }

    @Override
    public void run() {
        Cookie cookie = new Cookie(this.getFileName());
        String operation = "";
        String response = "";
        try (
            InputStream is = this.getSocket().getInputStream();
            OutputStream os = this.getSocket().getOutputStream()
        ) {
            System.out.println("Reading from Client");
            String initialMessage = this.readFromClient(is);
            System.out.println(initialMessage);
            System.out.println("Successful connection made!");
            this.writeToClient(os, "Successful connection made!");
            while (!operation.equals("close")) {
                operation = readFromClient(is);
                switch (operation) {
                    case "get-cookie":
                        response = "cookie-text " + cookie.getCookie();
                        break;
                    case "close":
                        response = "Shutting down...";
                        break;
                    default:
                        response = "Invalid request made!";
                        break;
                }
                System.out.println(response);
                this.writeToClient(os, response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readFromClient(InputStream is) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        DataInputStream dis = new DataInputStream(bis);
        return dis.readUTF();
    }

    public void writeToClient(OutputStream os, String request) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(os);
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeUTF(request);
        dos.flush();
    }
}
