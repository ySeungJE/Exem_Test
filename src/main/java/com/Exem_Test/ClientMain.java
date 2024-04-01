package com.Exem_Test;

import java.util.HashMap;
import java.util.Map;

public class ClientMain {
    public static final Map<String, Integer> regionPortMapping = new HashMap<>();

    /** ============== Main 함수 시작 ==============
     * 자치구 별 서버 소켓 port에 연결하기 위해 client thread 생성
     * DB에서 레코드가 제대로 읽히지 않는 문제로 3초씩 sleep...
     * -> JdbcSQLNonTransientException : The object is already closed
     */
    public static void main(String[] args) {
        dataInit();
        regionPortMapping.forEach((region, port) -> {
            new ClientThread(region).start();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    static void dataInit() {
        String[] regions = {"중구", "종로구", "용산구","광진구", "성동구", "중랑구", "동대문구", "성북구", "도봉구", "은평구", "서대문구", "마포구", "강서구",
                "구로구", "영등포구", "동작구", "관악구", "강남구", "서초구", "송파구", "강동구", "금천구", "강북구", "양천구", "노원구"};

        for (int i = 12001; i < 12001+regions.length; i++) {
            regionPortMapping.put(regions[i-12001], i);
        }
    }
}