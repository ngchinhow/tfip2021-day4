package com.tfip2021.workshop4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;

public class UserHandler extends Handler implements Runnable {
    private InputStream input;
    private OutputStream output;
    
    public InputStream getInputStream() { return this.input; }
    public OutputStream getOutputStream() { return this.output; }

    public UserHandler(InputStream is, OutputStream os) {
        this.input = is;
        this.output = os;
    }

    @Override
    public void run() {
        String operation = "";
        try (
            BufferedReader br = new BufferedReader(
                new InputStreamReader(this.getInputStream())
            )
        ) {
            while (!operation.equals("close")) {
                operation = br.readLine().toLowerCase();
                this.write(this.getOutputStream(), operation);
            }
        } catch (InterruptedIOException e) {
            System.out.println("Client terminated from server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
