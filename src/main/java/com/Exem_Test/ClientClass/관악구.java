package com.Exem_Test.ClientClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class 관악구 {  

    /** ============== Client Class ==============
     * 서버 소켓과 연결
     * 서버에서 발신한 미세먼지 경보 수신
     */
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 12017);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.print('\n'+"================ "+"[관악구]"+" 미세먼지 경보 || port:12017 ================" + '\n' + '\n');

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
