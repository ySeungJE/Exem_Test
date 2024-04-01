package com.Exem_Test;


import jakarta.persistence.EntityManager;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import static com.Exem_Test.ServerMain.*;

public class ServerThread extends Thread{
    private final String region;
    private final int port;
    private final EntityManager em;
    public ServerThread(String region, EntityManager em) {
        this.region = region;
        this.port =  regionPortMapping.get(region);
        this.em = em;
    }

    /** ============== Server Thread ==============
     * 서버 소켓 생성
     * 자치구 별 측정된 경보 DB에서 조회
     * 클라이언트에 경보 알림 발송
     */
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            System.out.println("[" + region + "]" + "의 port가 열렸습니다 || port:" + port);
            Socket clientSocket;

            // 클라이언트의 연결을 기다림
            while ((clientSocket = serverSocket.accept()) != null) {
                System.out.println("[" + region + "] port:"+port+ " 연결되었습니다. 미세먼지 경보를 전달합니다.");

                // 클라이언트와 통신하기 위한 출력 스트림 생성
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                // 해당 지역에 발령된 미세먼지 경보 객체 List를 가져온 뒤 발신
                List<Alert> alertList = em.createQuery("select a from Alert a where a.region = :region ", Alert.class)
                        .setParameter("region", region).getResultList();

                alertList.forEach(x -> out.println(x.getDate()+" "+x.getTime()+"시를 기하여 " +x.getRegion()+"에 "+'\n'
                        +x.getDegree()+"를 발령합니다." +'\n'+ "야외활동 자제, 마스크 착용 등 건강에 유의 바랍니다." +'\n'));

//            em.close();
//            clientSocket.close();
//            serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
