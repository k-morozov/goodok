package com.goodok.service;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class HandlerConnectionTest {

    @Test(expected = IllegalArgumentException.class)
    public void checkSendNull() throws IOException {
        final BufferedReader fakeBufReader = Mockito.mock(BufferedReader.class);
        Mockito.when(fakeBufReader.readLine()).thenReturn(null);

        HandlerConnection handler = new HandlerConnection(null, null, null) {
            @Override
            protected void sendRemoveClient(Socket socketClient) { }
        };
        handler.getNewMsg(fakeBufReader);
    }

    @Test
    public void sendToClientValidOneMsg() throws IOException {
        final BufferedReader fakeBufReader = Mockito.mock(BufferedReader.class);
        final Socket fakeSocketClient = Mockito.mock(Socket.class);
        Mockito.when(fakeBufReader.readLine()).thenReturn(null);

        ArrayList<String> responses = new ArrayList<>();

        HandlerConnection handler = new HandlerConnection(null, null, null) {
            @Override
            protected void sendToClient(Socket socketClient, String msg) {
                responses.add(msg);
            }
        };

        String testMsg = "test message";

        handler.sendToClient(fakeSocketClient, testMsg);
        assertFalse(responses.isEmpty());
        assertEquals(responses.get(0), testMsg);
    }

    @Test
    public void sendToClientValidTwoMsg() throws IOException {
        final BufferedReader fakeBufReader = Mockito.mock(BufferedReader.class);
        final Socket fakeSocketClient = Mockito.mock(Socket.class);
        Mockito.when(fakeBufReader.readLine()).thenReturn(null);

        ArrayList<String> responses = new ArrayList<>();
        ArrayList<String> expected = new ArrayList<>();

        HandlerConnection handler = new HandlerConnection(null, null, null) {
            @Override
            protected void sendToClient(Socket socketClient, String msg) {
                responses.add(msg);
            }
        };

        String testMsg = "test message";

        handler.sendToClient(fakeSocketClient, testMsg);
        expected.add(testMsg);

        testMsg = "test #2 message";
        handler.sendToClient(fakeSocketClient, testMsg);
        expected.add(testMsg);

        assertEquals(expected, responses);
    }

    @Test(expected = IllegalArgumentException.class)
    public void run() {
        HandlerConnection handler = new HandlerConnection(null, null, null);
        handler.run();
    }

    @Test
    public void getNewMsgValidOne() throws IOException {
        final BufferedReader fakeBufReader = Mockito.mock(BufferedReader.class);
        String text = "test msg #1";
        Mockito.when(fakeBufReader.readLine()).thenReturn(text);

        ArrayList<String> responses = new ArrayList<>();
        ArrayList<String> expected = new ArrayList<>();
        HandlerConnection handler = new HandlerConnection(null, null, null) {
            @Override
            protected void sendToClient(Socket socketClient, String msg) {
                responses.add(msg);
            }
        };

        handler.getNewMsg(fakeBufReader);
        assertEquals(1, responses.size());
        assertEquals(text, responses.get(0));
    }

    @Test
    public void testRun() {
    }
}