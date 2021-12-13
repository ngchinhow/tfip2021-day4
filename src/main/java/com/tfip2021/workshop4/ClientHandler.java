package com.tfip2021.workshop4;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler extends Handler implements Runnable {
    private Socket socket;
    private String fileName;
    
    public Socket getSocket() { return this.socket; }
    public String getFileName() { return this.fileName; }

    protected void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ClientHandler(Socket socket, String name) {
        this.socket = socket;
        this.fileName = name;
    }

    @Override
    public void run() {
        Cookie cookie = new Cookie(this.getFileName());
        String operation = "";
        String response = "";
        try (
            InputStream is = this.getSocket().getInputStream();
            OutputStream os = this.getSocket().getOutputStream()
        ) {
            System.out.println("Reading from Client");
            String initialMessage = this.read(is);
            System.out.println(initialMessage);
            System.out.println("Successful connection made!");
            this.write(os, "Successful connection made!");
            while (!operation.equals("close")) {
                operation = read(is);
                switch (operation) {
                    case "get-cookie":
                        response = "cookie-text " + cookie.getCookie();
                        break;
                    case "close":
                        response = "Shutting down...";
                        break;
                    default:
                        response = "Invalid request made!";
                        break;
                }
                System.out.println(response);
                this.write(os, response);
            }
        } catch (SocketException e) {
            System.out.println("Socket has been closed!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try (OutputStream os = this.getSocket().getOutputStream()) {
            this.write(os, "kill yourself");
            this.getSocket().close();
        } catch (SocketException e) {
            // Socket already closed, possibly by client
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
