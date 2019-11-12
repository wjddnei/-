package com.example.hoonkaotalk;

import io.socket.client.IO;
import io.socket.client.Socket;

public class wSocket {
    private static Socket socket;

    public static void connect() {
        try{
            socket=IO.socket("http://45.32.38.196:5000/");
            socket.connect();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static Socket get(){
        return socket;
    }
}
