package com.goodok.service;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SimpleService implements IService {
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    private ServerSocket serverSocket;
    private ArrayList<Socket> socketsClients;

    public static SimpleService create() {
        return new SimpleService();
    }

    @Override
    public void init(int port) {
        try {
            serverSocket = new ServerSocket(port);
            socketsClients = new ArrayList<>();

            System.out.println("Server init. Wait connections...");
        } catch (Exception ex) {
            System.out.println("Exception when create socket");
            throw new IllegalArgumentException(
                    "Port value out of range: " + port);
        }
    }

    @Override
    public void run() {
        try {
            ArrayList<Thread> threads = new ArrayList<>();
            while (true) {
                Socket socketClient = serverSocket.accept();
                if (socketClient == null) {
                    break;
                }
                socketsClients.add(socketClient);

                HandlerConnection handlerConnection = new HandlerConnection(socketClient, this::notifyClients, this::removeClient);
                Thread thread = new Thread(handlerConnection);
                threads.add(thread);
                thread.start();
            }
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (Exception ex) {
            System.out.println("Exception when client connecting");
            ex.printStackTrace();
        }
    }

    @Override
    public void notifyClients(Socket fromClient, String msg) {
        for (Socket sock: socketsClients) {
            try{
                if (sock == fromClient) {
                    continue;
                }
                if (sock.isConnected()) {
                    OutputStream os = sock.getOutputStream();
                    DataOutputStream out = new DataOutputStream(os);
                    out.writeUTF(msg + '\n');
                    out.flush();
                }
            } catch (Exception ex) {
                System.out.println("SimpleService.send");
            }
        }
    }

    private void removeClient(Socket socketClient) {
        try {
            socketsClients.remove(socketClient);
        } catch (Exception ex) {
            System.out.println("SimpleService.removeClient");
        }
    }


}
