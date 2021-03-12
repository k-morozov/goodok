package com.goodok.application;

import com.goodok.service.SimpleService;

public final class GoodOkApp {
    public static void main(final String[] args) {
        final int port = 8018;
        try {
            SimpleService server = SimpleService.create();
            server.init(port);
            server.run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
