package com.goodok.service;

import org.junit.Test;

import static org.junit.Assert.*;

public class SimpleServiceTest {

    @Test
    public void createValid() {
        IService service = SimpleService.create();
        assertNotNull(service);
    }

    @Test
    public void initValidPort() {
        SimpleService service = SimpleService.create();
        assertNull(service.getServerSocket());
        final int port = 8018;
        service.init(port);
        assertNotNull(service.getServerSocket());
    }

    @Test(expected = IllegalArgumentException.class)
    public void initFailedPort() {
        SimpleService service = SimpleService.create();
        assertNull(service.getServerSocket());
        final int port = -1;
        service.init(port);
    }
}