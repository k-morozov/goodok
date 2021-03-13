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
            serverSocket = new ServerSocket(port);
            socketsClients = new ArrayList<>();

            System.out.println("Server init. Wait connections...");
        } catch (Exception ex) {
            System.out.println("Exception when create socket");
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
                SimpleService.Handler handler = new SimpleService.Handler(socketClient);
                Thread thread = new Thread(handler);
                threads.add(thread);
                thread.start();
            }
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (Exception ex) {
            System.out.println("Exception when client connecting");
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

    private void removeClient(Socket socketClient) throws IOException {
        if (!socketClient.isClosed()) {
            serverSocket.close();
        }
        socketsClients.remove(socketClient);
    }

    class Handler implements Runnable {
        private final Socket _socketClient;

        public Handler(Socket socketClient) {
            _socketClient = socketClient;
        }

        @Override
        public void run() {
            if (_socketClient == null) {
                return;
            }

            try {
                System.out.println("New client from: " +
                        _socketClient.getRemoteSocketAddress());
                InputStream sin = _socketClient.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(sin));
                while (true) {
                    String line = in.readLine();
                    if (line == null) {
                        System.out.println("Close connection");
                        SimpleService.this.removeClient(_socketClient);
                        break;
                    }
                    System.out.println("read: " + line);
                    SimpleService.this.notifyClients(_socketClient, line);
                }
            } catch (Exception ex) {
                System.out.println("Exception in thread");
            }
        }


    }
}
