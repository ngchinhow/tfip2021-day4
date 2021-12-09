package com.tfip2021.workshop4;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Server {
    private ServerSocket server;
    private Socket socket;
    private String fileName;
    private ExecutorService threadPool = Executors.newFixedThreadPool(3);
    private ArrayList< ClientHandler > clientThreads = new ArrayList< ClientHandler >();

    public ServerSocket getServer() { return this.server; }
    public Socket getSocket() { return this.socket; }
    public String getFileName() { return this.fileName; }
    public ExecutorService getThreadPool() { return this.threadPool; }
    public ArrayList< ClientHandler > getClientThreads() { return this.clientThreads; }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    public void setClientThread(ClientHandler thread) {
        this.clientThreads.add(thread);
    }

    public Server(int port, String name) throws IOException {
        this.fileName = name;
        this.server = new ServerSocket(port);
        this.server.setSoTimeout(30000);
    }

    public void interfaceWithClient() throws IOException {
        try {
            while (true) {
                System.out.println("Waiting for new connection...");
                setSocket(this.getServer().accept());
                System.out.println("Got socket");
                ClientHandler ch = new ClientHandler(
                    this.getSocket(),
                    this.getFileName()
                );
                setClientThread(ch);
                System.out.println("Created thread");
                this.getThreadPool().submit(ch);
                System.out.println("Started thread");
            }
        } catch (SocketTimeoutException e) {
            for (ClientHandler t : this.getClientThreads()) {
                t.stop();
            }
            this.close();
        }
    }

    public void close() throws IOException {
        // Disable new tasks from being submitted
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
        this.getServer().close();
    }

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);
        String fileName = args[1];
        Server s = new Server(port, fileName);
        s.interfaceWithClient();
    }
}