package com.goodok.service;

import java.io.IOException;
import java.net.Socket;

public interface IService {
    void init(int port);
    void run() throws IOException;
    void notifyClients(Socket fromClient, String msg);
}
