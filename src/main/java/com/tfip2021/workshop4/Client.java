package com.tfip2021.workshop4;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Client {
    private Socket socket;
    private String name;
    private UserToServer uts;
    // Used to store the UserToServer Runnable
    private ExecutorService threadPool = Executors.newFixedThreadPool(1);
    private PipedOutputStream stagingPipe = new PipedOutputStream();
    private PipedInputStream releasingPipe = new PipedInputStream(stagingPipe);

    public Socket getSocket() { return this.socket; }
    public String getName() { return this.name; }
    public UserToServer getUserToServer() { return this.uts; }
    public ExecutorService getThreadPool() { return this.threadPool; }
    public PipedOutputStream getStagingPipe() { return this.stagingPipe; }
    public InputStream getReleasingPipe() { return this.releasingPipe; }

    public void setUserToServer(UserToServer uts) {
        this.uts = uts;
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
            System.out.println("Hi I'm here!");
            this.writeToServer(os, "Hi! My name is " + this.getName());
            System.out.println(this.readFromServer(is));

            Thread stagingThread = new Thread(
                new UserReader(this.getStagingPipe())
            );
            stagingThread.setDaemon(true);
            stagingThread.start();
            setUserToServer(new UserToServer(this.getReleasingPipe(), os));
            this.getThreadPool().submit(this.getUserToServer());
            while (!operation.equals("close")) {
                System.out.println("Reading from server");
                String serverText = this.readFromServer(is);
                if (serverText.equals("kill yourself")) {
                    operation = "close";
                    this.getUserToServer().stop();
                } else {
                    System.out.println(
                        serverText.replaceAll("^cookie-text ", "")
                    );
                }
            }
        }
        System.out.println("Closing resources");
        this.close();
    }

    public void close() throws IOException {
        this.getThreadPool().shutdown();
        try {
            // Wait a while for existing tasks to terminate
            if (!this.getThreadPool().awaitTermination(10, TimeUnit.SECONDS)) {
                this.getThreadPool().shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!this.getThreadPool().awaitTermination(10, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            this.getThreadPool().shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
        this.getSocket().close();
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

    public static void main(String[] args) throws UnknownHostException, IOException {
        String add = args[0].split(":")[0];
        int port = Integer.parseInt(args[0].split(":")[1]);
        String name = args[1];
        Client c = new Client(add, port, name);
        c.interfaceWithUser();
    }
}