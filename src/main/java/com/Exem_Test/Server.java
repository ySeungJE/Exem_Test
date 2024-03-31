package com.Exem_Test;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException, ParseException {
        // .json 파일일 경우
        Reader reader = new FileReader("C:\\Users\\test.json");
        // reader를 Object로 parse
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(reader);
        JSONArray jsonArr = (JSONArray)obj;
        if (!jsonArr.isEmpty()){
            for (Object o : jsonArr) {
                //여기에 경보 인식 알고리즘 작성
                //경보 울릴 때마다 Alert 객체 만들어서 클라이언트에 전송해주든가 하고 persist
                //for문 다 끝나고 commit 후 마무리
                JSONObject jsonObj = (JSONObject) o;

                System.out.println((String) jsonObj.get("date"));
            }
        }

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("exem"); // 데이터베이스당 하나 있어야됨
        EntityManager em = emf.createEntityManager(); // 엔티티 메니저를 통해서 작업

        EntityTransaction tx = em.getTransaction(); // 데이터베이스 모든 변경은 트랜잭션 안에서 일어나야함
        tx.begin();

        Alert alert = new Alert();
        alert.setPhase(3);
        alert.setArea("중구");
        alert.setDateTime("2024-12-10");

        Alert alert2 = new Alert();
        alert2.setPhase(3);
        alert2.setArea("중구");
        alert2.setDateTime("2024-12-10");

        em.persist(alert); // 저장!
        em.persist(alert2); // 저장!
        tx.commit(); // 커밋안하면 반영이 안된다
        em.close();
        emf.close();

        try {
            // 서버 소켓 생성 (포트 번호 12345 사용)
            ServerSocket serverSocket = new ServerSocket(12500);
            System.out.println("서버가 시작되었습니다.");

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
}
