package com.Exem_Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import static com.Exem_Test.ClientMain.regionPortMapping;


public class ClientThread extends Thread{
    private final String region;
    private final int port;
    public ClientThread(String region) {
        this.region = region;
        this.port = regionPortMapping.get(region);
    }

    /** ============== Client Thread ==============
     * 서버 소켓과 연결
     * 서버에서 발신한 미세먼지 경보 수신
     */
    public void run() {
        try {
            Socket socket = new Socket("localhost", port);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.print('\n'+"================ "+"["+region+"]"+" 미세먼지 경보 || port:"+port+" ================" + '\n' + '\n');

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
