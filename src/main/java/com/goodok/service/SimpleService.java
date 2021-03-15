package com.goodok.service;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.lang.Thread;
import java.util.ArrayList;

public class SimpleService implements IService {
    private ServerSocket serverSocket;
    private ArrayList<Socket> socketsClients;

    public static SimpleService create() {
        return new SimpleService();
    }

    @Override
    public void init(int port) {
        try {
            serverSocket = createServerSocket(port);
            socketsClients = createSocketsClients();
        } catch (Exception ex) {
            System.err.println("Exception when create socket");
            throw new IllegalArgumentException(
                    "Port value out of range: " + port);
        }
    }

    @Override
    public void run() throws IOException {
        try {
            ArrayList<Thread> threads = new ArrayList<>();
            while (true) {
                startConnection(threads);
            }
        } catch (Exception ex) {
            System.err.println("Exception when client connecting");
            throw ex;
        }
    }

    protected void startConnection(ArrayList<Thread> threads) throws IOException {
        Socket socketClient = getSocketConnection(serverSocket);
        if (socketClient == null) {
            return;
        }
        getSocketsClients().add(socketClient);

        HandlerConnection handlerConnection = getNewHandlerConnection(socketClient);
        runHandlerNewThread(threads, handlerConnection);
    }

    protected void runHandlerNewThread(ArrayList<Thread> threads, HandlerConnection handler) {
        Thread thread = new Thread(handler);
        threads.add(thread);
        thread.start();
    }

    protected HandlerConnection getNewHandlerConnection(Socket socketClient) {
        return new HandlerConnection(socketClient, this::notifyClients, this::removeClient);
    }

    @Override
    public void notifyClients(Socket fromClient, String msg) {
        for (Socket sock: getSocketsClients()) {
            try{
                if (sock == fromClient) {
                    continue;
                }
                if (sock.isConnected()) {
                    DataOutputStream out = getSockStreamOut(sock);
                    out.writeUTF(msg + '\n');
                    out.flush();
                }
            } catch (Exception ex) {
                System.err.println("SimpleService.send");
            }
        }
    }

    protected void removeClient(Socket socketClient) {
        try {
            ArrayList<Socket> sockClients = getSocketsClients();
            sockClients.remove(socketClient);
        } catch (Exception ex) {
            System.err.println("SimpleService.removeClient");
        }
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    protected ServerSocket createServerSocket(int port) throws IOException {
        return new ServerSocket(port);
    }

    protected Socket getSocketConnection(ServerSocket socket) throws IOException {
        return socket.accept();
    }

    protected DataOutputStream getSockStreamOut(Socket sock) throws IOException {
        return new DataOutputStream(sock.getOutputStream());
    }

    protected ArrayList<Socket> getSocketsClients() {
        return socketsClients;
    }

    protected ArrayList<Socket> createSocketsClients() {
        return new ArrayList<>();
    }


}
