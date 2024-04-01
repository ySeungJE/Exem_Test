package com.Exem_Test;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static com.Exem_Test.Main.*;

public class ServerTestThread extends Thread{
    private final String region;
    private final int port;
    public ServerTestThread(String region) {
        this.region = region;
        this.port =  regionPortMapping.get(region);
    }
    public void run() {  // Thread 를 상속하면 run 메서드를 구현해야 한다.
        try {
            // 서버 소켓 생성 (포트 번호 12345 사용)
            ServerSocket serverSocket = new ServerSocket(regionPortMapping.get(region));
            System.out.println(region + "의 port가 열렸습니다 || port:12001");

            // 클라이언트의 연결을 기다림
            Socket clientSocket = serverSocket.accept();
            System.out.println("클라이언트가 연결되었습니다.");

            // 클라이언트와 통신하기 위한 입출력 스트림 생성
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // 클라이언트로부터 메시지 수신 및 출력
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("클라이언트: " + message);
                out.println("서버: " + message); // 클라이언트에게 다시 전송
            }

            // 연결 종료
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException, ParseException {

    }
}
