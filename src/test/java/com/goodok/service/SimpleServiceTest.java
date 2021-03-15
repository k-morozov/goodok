package com.goodok.service;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class SimpleServiceTest {

    @Test
    public void prepareService() throws IOException {
        final ServerSocket serverSocket = Mockito.mock(ServerSocket.class);
        final Socket clientSocket = Mockito.mock(Socket.class);
        Mockito.when(serverSocket.accept()).thenReturn(clientSocket);

        SimpleService service = new SimpleService() {
            @Override
            protected ServerSocket createServerSocket(int port) {
                return serverSocket;
            }
        };

        assertEquals(serverSocket, service.createServerSocket(7777));
    }

    @Test(expected = IllegalArgumentException.class)
    public void initWithExcept() {
        SimpleService service = new SimpleService() {
          @Override
          protected ServerSocket createServerSocket(int port) throws IOException {
              throw new IllegalArgumentException("");
          }
        };

        service.init(7777);
    }

    @Test(expected = IllegalArgumentException.class)
    public void runWithException() throws IOException {
        SimpleService service = new SimpleService() {
            @Override
            protected void startConnection(ArrayList<Thread> threads) throws IOException {
                throw new IllegalArgumentException("");
            }
        };

        service.run();
    }

    @Test
    public void checkInitBasic() throws IOException {
        final ServerSocket serverSocket = Mockito.mock(ServerSocket.class);

        SimpleService service = new SimpleService() {
            @Override
            protected ServerSocket createServerSocket(int port) {
                return serverSocket;
            }
        };

        service.init(7777);
        assertNotNull(service.getServerSocket());
        assertEquals(service.getServerSocket(), serverSocket);
    }

    @Test
    public void testNotifyZeroClients() throws IOException {
        final Socket socketClient = Mockito.mock(Socket.class);
        DataOutputStream output = Mockito.mock(DataOutputStream.class);

        SimpleService service = new SimpleService() {
            @Override
            protected DataOutputStream getSockStreamOut(Socket sock) throws IOException {
                return output;
            }
        };
        Mockito.when(socketClient.isConnected()).thenReturn(true);

        service.init(7778);
        String text = "test";
        service.notifyClients(null, text);

        Mockito.verify(output, Mockito.times(0)).writeUTF(text + '\n');
    }

    @Test
    public void testNotifyOneClients() throws IOException {
        final Socket socketClient = Mockito.mock(Socket.class);
        DataOutputStream output = Mockito.mock(DataOutputStream.class);

        SimpleService service = new SimpleService() {
            @Override
            protected ArrayList<Socket> createSocketsClients() {
                ArrayList<Socket> sockets = new ArrayList<>();
                sockets.add(socketClient);
                return sockets;
            }

            @Override
            protected DataOutputStream getSockStreamOut(Socket sock) throws IOException {
                return output;
            }
        };
        Mockito.when(socketClient.isConnected()).thenReturn(true);

        service.init(7777);
        String text = "test";
        service.notifyClients(null, text);

        Mockito.verify(output, Mockito.times(1)).writeUTF(text + '\n');
    }

    @Test
    public void testNotifyTwoClients() throws IOException {
        final Socket socketClient1 = Mockito.mock(Socket.class);
        final Socket socketClient2 = Mockito.mock(Socket.class);
        DataOutputStream output = Mockito.mock(DataOutputStream.class);

        SimpleService service = new SimpleService() {
            @Override
            protected ArrayList<Socket> createSocketsClients() {
                ArrayList<Socket> sockets = new ArrayList<>();
                sockets.add(socketClient1);
                sockets.add(socketClient2);
                return sockets;
            }

            @Override
            protected DataOutputStream getSockStreamOut(Socket sock) throws IOException {
                return output;
            }
        };
        Mockito.when(socketClient1.isConnected()).thenReturn(true);
        Mockito.when(socketClient2.isConnected()).thenReturn(true);

        service.init(7779);
        String text = "test";
        service.notifyClients(null, text);

        Mockito.verify(output, Mockito.times(2)).writeUTF(text + '\n');
    }

    @Test
    public void testNotifyTwoClientsFromOne() throws IOException {
        final Socket socketClient1 = Mockito.mock(Socket.class);
        final Socket socketClient2 = Mockito.mock(Socket.class);
        DataOutputStream output = Mockito.mock(DataOutputStream.class);

        SimpleService service = new SimpleService() {
            @Override
            protected ArrayList<Socket> createSocketsClients() {
                ArrayList<Socket> sockets = new ArrayList<>();
                sockets.add(socketClient1);
                sockets.add(socketClient2);
                return sockets;
            }

            @Override
            protected DataOutputStream getSockStreamOut(Socket sock) throws IOException {
                return output;
            }
        };
        Mockito.when(socketClient1.isConnected()).thenReturn(true);
        Mockito.when(socketClient2.isConnected()).thenReturn(true);

        service.init(7780);
        String text = "test";
        service.notifyClients(socketClient1, text);

        Mockito.verify(output, Mockito.times(1)).writeUTF(text + '\n');
    }

    @Test
    public void removeClient() {
        final Socket socketClient1 = Mockito.mock(Socket.class);

        SimpleService service = new SimpleService() {
            @Override
            protected ArrayList<Socket> createSocketsClients() {
                ArrayList<Socket> sockets = new ArrayList<>();
                sockets.add(socketClient1);
                return sockets;
            }
        };

        assertNull(service.getSocketsClients());

        service.init(7781);
        assertNotNull(service.getSocketsClients());
        assertEquals(1, service.getSocketsClients().size());

        service.removeClient(null);
        assertNotNull(service.getSocketsClients());
        assertEquals(1, service.getSocketsClients().size());

        service.removeClient(socketClient1);
        assertNotNull(service.getSocketsClients());
        assertEquals(0, service.getSocketsClients().size());
    }

    @Test
    public void startOneCorrectConnection() throws IOException, InterruptedException {
        final Socket socketClient = Mockito.mock(Socket.class);
        final HandlerConnection fakeHandlerConnection = Mockito.mock(HandlerConnection.class);

        SimpleService service = new SimpleService() {
            @Override
            protected Socket getSocketConnection(ServerSocket socketUnused) {
                return socketClient;
            }
            @Override
            protected HandlerConnection getNewHandlerConnection(Socket socketClient) {
                return fakeHandlerConnection;
            }
        };

        service.init(7782);
        assertNotNull(service.getSocketsClients());
        assertEquals(0, service.getSocketsClients().size());

        ArrayList<Thread> threads = new ArrayList<>();
        service.startConnection(threads);

        assertNotNull(service.getSocketsClients());
        assertEquals(1, service.getSocketsClients().size());
        assertEquals(1, threads.size());
        threads.get(0).join();
        Mockito.verify(fakeHandlerConnection, Mockito.times(1)).run();
    }

    @Test
    public void startOneNullConnection() throws IOException {
        final HandlerConnection fakeHandlerConnection = Mockito.mock(HandlerConnection.class);

        SimpleService service = new SimpleService() {
            @Override
            protected Socket getSocketConnection(ServerSocket socketUnused) {
                return null;
            }
            @Override
            protected HandlerConnection getNewHandlerConnection(Socket socketClient) {
                return fakeHandlerConnection;
            }
        };

        service.init(7783);
        assertNotNull(service.getSocketsClients());
        assertEquals(0, service.getSocketsClients().size());

        ArrayList<Thread> threads = new ArrayList<>();
        service.startConnection(threads);

        assertNotNull(service.getSocketsClients());
        assertEquals(0, service.getSocketsClients().size());
        assertEquals(0, threads.size());

        Mockito.verify(fakeHandlerConnection, Mockito.times(0)).run();
    }

    @Test
    public void runHandlerNewThreadBasic() throws InterruptedException {
        final HandlerConnection handler = Mockito.mock(HandlerConnection.class);
        SimpleService service = new SimpleService();

        ArrayList<Thread> threads = new ArrayList<>();
        service.runHandlerNewThread(threads, handler);

        assertEquals(1, threads.size());
        threads.get(0).join();
        Mockito.verify(handler, Mockito.times(1)).run();
    }

}