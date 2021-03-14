package com.goodok.service;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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

        assertTrue(serverSocket.equals(service.createServerSocket(7777)));
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
        assertTrue(service.getServerSocket().equals(serverSocket));
    }

    @Test
    public void checkRunBasic() throws IOException {

    }

}