package com.tfip2021.workshop4;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    private Socket socket;

    public Socket getSocket() { return this.socket; }

    public Client(String add, int port) throws UnknownHostException, IOException {
        this.socket = new Socket(add, port);
    }

    public void interfaceWithUser() throws IOException {
        String operation = "";
        try (
            InputStream is = this.getSocket().getInputStream();
            OutputStream os = this.getSocket().getOutputStream();
            Scanner scan = new Scanner(System.in)
        ) {
            while (!operation.equals("close")) {
                operation = scan.nextLine().toLowerCase();
                this.writeToServer(os, operation);
                String cookieText = this.readFromServer(is);
                System.out.println(cookieText.replaceAll("^cookie-text ", ""));
            }
        }
        this.close();
    }

    public String readFromServer(InputStream is) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        DataInputStream dis = new DataInputStream(bis);
        return dis.readUTF();
    }

    public void writeToServer(OutputStream os, String request) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(os);
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeUTF(request);
        dos.flush();
    }

    public void close() throws IOException {
        this.getSocket().close();
    }

    public static void main(String[] args) throws UnknownHostException, IOException {
        String add = args[0].split(":")[0];
        int port = Integer.parseInt(args[0].split(":")[1]);
        Client c = new Client(add, port);
        c.interfaceWithUser();
    }
}