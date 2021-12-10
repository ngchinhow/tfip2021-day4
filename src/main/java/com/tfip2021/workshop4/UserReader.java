package com.tfip2021.workshop4;

import java.io.IOException;
import java.io.PipedOutputStream;

public class UserReader implements Runnable {
    private PipedOutputStream pos;

    public PipedOutputStream getPipedInput() { return this.pos; }

    public UserReader(PipedOutputStream pos) {
        this.pos = pos;
    } 

    @Override
    public void run() {
        try {
            while(true) {
                pos.write(System.in.read());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
