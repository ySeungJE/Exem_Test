package com.Exem_Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client2 { // 서버를 구역별로 다 켜놓고 클라이언트가 켜지면 JSON 파일 읽어서 각 구역을 담당하는 서버로
    public static void main(String[] args) {
        try {
            // 서버에 연결
            Socket socket = new Socket("localhost", 12002);

            // 클라이언트와 통신하기 위한 입출력 스트림 생성
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // 키보드 입력을 받기 위한 BufferedReader 생성
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

            String message;
            while (true) {
                // 사용자로부터 메시지 입력
                System.out.print("나: ");
                message = keyboard.readLine();

                // 서버로 메시지 전송
                out.println(message);

                // 서버에서 받은 메시지 출력
                System.out.println("서버: " + in.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
