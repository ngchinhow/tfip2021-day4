package com.tfip2021.workshop4;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket server;
    private Socket socket;
    private String fileName;

    public ServerSocket getServer() { return server; }
    public Socket getSocket() { return socket; }
    public String getFileName() { return fileName; }

    public Server(int port, String name) throws IOException {
        this.fileName = name;
        this.server = new ServerSocket(port);
        this.socket = server.accept();
    }

    public void interfaceWithClient() throws IOException {
        Cookie cookie = new Cookie(this.getFileName());
        String operation = "";
        String response = "";
        try (
            InputStream is = this.getSocket().getInputStream();
            OutputStream os = this.getSocket().getOutputStream()
        ) {
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
        }
        this.close();
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

    public void close() throws IOException {
        this.getSocket().close();
        this.getServer().close();
    }

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);
        String fileName = args[1];
        Server s = new Server(port, fileName);
        s.interfaceWithClient();
    }
}