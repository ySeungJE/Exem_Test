package com.Exem_Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class Main {
	public static void main(String[] args) throws IOException, ParseException {
		String path = System.getProperty("user.dir"); // 현재 작업 경로

		// .json 파일일 경우
		Reader reader = new FileReader(path + "\\test.json");
		// reader를 Object로 parse
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(reader);
		JSONArray jsonArr = (JSONArray)obj;
		if (!jsonArr.isEmpty()){
			for (int i=0; i<jsonArr.size(); i++) {
				//여기에 경보 인식 알고리즘 작성
				//우선 Main 함수 켜면 모든 경보 정보를 DB에 넣어둔다
				//for문 다 끝나고 commit 후 마무리
				//클라이언트가 연결되면 DB에서 해당 지역
				JSONObject jsonObject = (JSONObject) jsonArr.get(i);

				String dateTime = (String) jsonObject.get("dateTime");
				String region = (String) jsonObject.get("region");
				int pm10 = Integer.parseInt((String) jsonObject.get("PM10"));
				int pm2_5 = Integer.parseInt((String) jsonObject.get("PM2.5"));

				System.out.println(dateTime + region + pm10 + pm2_5);

//				if()
			}
		}

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("exem"); // 데이터베이스당 하나 있어야됨
		EntityManager em = emf.createEntityManager(); // 엔티티 메니저를 통해서 작업

		EntityTransaction tx = em.getTransaction(); // 데이터베이스 모든 변경은 트랜잭션 안에서 일어나야함
		tx.begin();


		Alert alert = Alert.builder()
				.phase(5)
				.area("중구").dateTime("2024-12-11").build();
		Alert alert2 = Alert.builder()
				.phase(5)
				.area("중구").dateTime("2024-12-11").build();

		em.persist(alert); // 저장!
		em.persist(alert2); // 저장!
		tx.commit(); // 커밋안하면 반영이 안된다
		em.close();
		emf.close();

		ServerTestThread serverTestThread1 = new ServerTestThread(12001);
		ServerTestThread serverTestThread2 = new ServerTestThread(12002);
		ServerTestThread serverTestThread3 = new ServerTestThread(12003);
		ServerTestThread serverTestThread4 = new ServerTestThread(12004);
		ServerTestThread serverTestThread5 = new ServerTestThread(12005);
		serverTestThread1.start();
		serverTestThread2.start();
		serverTestThread3.start();
		serverTestThread4.start();
		serverTestThread5.start();
	}
}