package com.goodok.service;

import java.net.Socket;

public interface IService {
    void init(int port);
    void run();
    void notifyClients(Socket fromClient, String msg);
}
