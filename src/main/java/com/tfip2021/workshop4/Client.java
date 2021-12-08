package com.tfip2021.workshop4;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private Socket socket;
    private String name;

    public Socket getSocket() { return this.socket; }
    public String getName() { return this.name; }

    public Client(String add, int port, String name) throws UnknownHostException, IOException {
        this.socket = new Socket(add, port);
        this.name = name;
    }

    public void interfaceWithUser() throws IOException {
        String operation = "";
        try (
            InputStream is = this.getSocket().getInputStream();
            OutputStream os = this.getSocket().getOutputStream();
            BufferedReader br = new BufferedReader(
                new InputStreamReader(System.in)
            )
        ) {
            System.out.println("Hi I'm here!");
            this.writeToServer(os, "Hi! My name is " + this.getName());
            System.out.println(this.readFromServer(is));
            while (!operation.equals("close")) {
                // try {
                //     while (!br.ready()) Thread.sleep(200);
                // } catch (InterruptedException e) {
                //     System.out.println("Client/User interface terminated");
                // }
                operation = br.readLine().toLowerCase();
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
        String name = args[1];
        Client c = new Client(add, port, name);
        c.interfaceWithUser();
    }
}