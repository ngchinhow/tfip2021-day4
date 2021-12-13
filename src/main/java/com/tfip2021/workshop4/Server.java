package com.tfip2021.workshop4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;

public class Server {
    private ServerSocket server;
    private String fileName;
    private Thread serverThread;
    private FastPipedOutputStream stagingPipe = new FastPipedOutputStream();
    private FastPipedInputStream releasingPipe = new FastPipedInputStream(stagingPipe);

    public ServerSocket getServer() { return this.server; }
    public Thread getServerThread() { return this.serverThread; }
    public FastPipedOutputStream getStagingPipe() { return this.stagingPipe; }
    public FastPipedInputStream getReleasingPipe() { return this.releasingPipe; }



    public Server(int port, String name) throws IOException {
        this.fileName = name;
        this.server = new ServerSocket(port);
        this.serverThread = new Thread(new ServerThread(this.server, this.fileName));
        // this.server.setSoTimeout(30000);
    }

    public void interfaceWithUser() throws IOException {
        Thread stagingThread = new Thread(
                new UserReader(this.getStagingPipe())
        );
        stagingThread.setDaemon(true);
        stagingThread.start();
        this.getServerThread().start();
        String operation = "";
        try (
            BufferedReader br = new BufferedReader(
                new InputStreamReader(this.getReleasingPipe())
            )
        ) {
            while (!operation.equals("close")) {
                operation = br.readLine().toLowerCase();
            }
        } finally {
            this.getServer().close();
        }
    }

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);
        String fileName = args[1];
        Server s = new Server(port, fileName);
        s.interfaceWithUser();
    }
}