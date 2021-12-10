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

public class UserHandler implements Runnable {
    private boolean running = false;
    private InputStream in = System.in;
    private InputStream sockIS; // from socket
    private OutputStream sockOS; // from socket

    public boolean isRunning() { return this.running; }
    public InputStream getUserInput() { return this.in; }
    public InputStream getSocketInput() { return this.sockIS; }
    public OutputStream getSocketOutput() { return this.sockOS; }

    public void setRunning(boolean run) {
        this.running = run;
    }

    public UserHandler(InputStream input, OutputStream output) {
        this.sockIS = input;
        this.sockOS = output;
    } 

    @Override
    public void run() {
        this.setRunning(true);
        String operation = "";
        try (BufferedReader br = new BufferedReader(
            new InputStreamReader(System.in)
        )) {
            while (!operation.equals("close")) {
                while (!br.ready() && this.isRunning()) { 
                    // Run indefinitely until stream is ready
                }
                if (this.isRunning()) {
                    System.out.print ("Looking for user input: ");
                    operation = br.readLine().toLowerCase();
                    this.writeToServer(this.getSocketOutput(), operation);
                } else {
                    operation = "close";
                    System.out.println("Closing client now");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        setRunning(false);
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
}
