package com.tfip2021.workshop4;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Handler {
    private Socket socket;
    private String name;
    private Thread userHandler;
    private FastPipedOutputStream stagingPipe = new FastPipedOutputStream();
    private FastPipedInputStream releasingPipe = new FastPipedInputStream(stagingPipe);

    public Socket getSocket() { return this.socket; }
    public String getName() { return this.name; }
    public Thread getUserHandler() { return this.userHandler; }
    public FastPipedOutputStream getStagingPipe() { return this.stagingPipe; }
    public InputStream getReleasingPipe() { return this.releasingPipe; }

    public void setUserHandler(Thread userHandler) {
        this.userHandler = userHandler;
    }

    public Client(String add, int port, String name) throws UnknownHostException, IOException {
        this.socket = new Socket(add, port);
        this.name = name;
    }

    public void interfaceWithUser() throws IOException {
        String operation = "";
        try (
            InputStream is = this.getSocket().getInputStream();
            OutputStream os = this.getSocket().getOutputStream()
        ) {
            // Handshake with server
            this.write(os, "Hi! My name is " + this.getName());
            System.out.println(this.read(is));

            Thread stagingThread = new Thread(
                new UserReader(this.getStagingPipe())
            );
            stagingThread.setDaemon(true);
            stagingThread.start();
            setUserHandler(new Thread(
                new UserHandler(this.getReleasingPipe(), os)
            ));
            this.getUserHandler().start();
            while (!operation.equals("close")) {
                // Reading from server
                String serverText = this.read(is);
                switch (serverText) {
                    case "kill yourself":
                        operation = "close";
                        this.getStagingPipe().close();
                        this.getReleasingPipe().close();
                        this.getUserHandler().interrupt();
                        break;
                    case "Shutting down...":
                        operation = "close";
                    default:
                        System.out.println(
                            serverText.replaceAll("^cookie-text ", "")
                        );
                        break;
                }
            }
        } finally {
            // Closing resources
            this.getSocket().close();
        }
    }

    public static void main(String[] args) throws UnknownHostException, IOException {
        String add = args[0].split(":")[0];
        int port = Integer.parseInt(args[0].split(":")[1]);
        String name = args[1];
        Client c = new Client(add, port, name);
        c.interfaceWithUser();
    }
}