package com.tfip2021.workshop4;

// Common functions found in interface handler

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Handler {
    public String read(InputStream is) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        DataInputStream dis = new DataInputStream(bis);
        return dis.readUTF();
    }

    public void write(OutputStream os, String request) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(os);
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeUTF(request);
        dos.flush();
    }
    
}
