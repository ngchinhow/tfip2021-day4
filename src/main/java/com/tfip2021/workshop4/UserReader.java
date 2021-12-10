package com.tfip2021.workshop4;

import java.io.IOException;

public class UserReader implements Runnable {
    private FastPipedOutputStream pos;

    public FastPipedOutputStream getPipedInput() { return this.pos; }

    public UserReader(FastPipedOutputStream pos) {
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
