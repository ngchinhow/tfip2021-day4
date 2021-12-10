package com.tfip2021.workshop4;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;

public class UserToServer implements Runnable {
    private InputStream input; // from FastPipedInputStream
    private OutputStream output; // from Socket.getOutputStream
    
    public InputStream getInputStream() { return this.input; }
    public OutputStream getOutputStream() { return this.output; }

    public UserToServer(InputStream is, OutputStream os) {
        this.input = is;
        this.output = os;
    }

    @Override
    public void run() {
        String operation = "";
        try (BufferedReader br = new BufferedReader(
            new InputStreamReader(this.getInputStream())
        )) {
            while (!operation.equals("close")) {
                operation = br.readLine().toLowerCase();
                this.writeToServer(this.getOutputStream(), operation);
            }
        } catch (InterruptedIOException e) {
            System.out.println("UserToServer was interrupted");
        }
         catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() throws IOException {
        Thread.currentThread().interrupt();
        this.getInputStream().close();
        this.getOutputStream().close();
        System.out.println("Client stopped from server");
    }

    public void writeToServer(OutputStream os, String request) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(os);
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeUTF(request);
        dos.flush();
    }
}
