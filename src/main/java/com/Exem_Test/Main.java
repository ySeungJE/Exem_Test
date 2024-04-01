package com.Exem_Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
	//map 하나 만들어서 지역구 별 포트 번호 매핑해 놓자
	public static final Map<String, Integer> regionPortMapping = new HashMap<>();

	public static void main(String[] args) throws IOException, ParseException {
		init();
		String path = System.getProperty("user.dir"); // 현재 작업 경로
		// .json 파일일 경우
		Reader reader = new FileReader(path + "\\data.json");
		// reader를 Object로 parse
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(reader);
		JSONArray jsonArr = (JSONArray)obj;

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("exem"); // 데이터베이스당 하나 있어야됨
		EntityManager em = emf.createEntityManager(); // 엔티티 메니저를 통해서 작업

		EntityTransaction tx = em.getTransaction(); // 데이터베이스 모든 변경은 트랜잭션 안에서 일어나야함
		tx.begin();


		/**
		 * pm10이 150 이상이면?
		 * -> 이 전 시간 pm10이 150 이상인지 확인해서 4단계 경보 발령
		 * pm10이 300 이상이면?
		 * -> 이 전 시간 pm10이 300 이상인지 확인해서 2단계 경보 발령
		 * pm2.5가 75 이상이면?
		 * -> 이 전 시간 pm2.5가 75 이상인지 확인해서 3단계 경보 발령
		 * pm2.5이 150 이상이면?
		 * -> 이 전 시간 pm2.5가 150 이상인지 확인해서 1단계 발령
		 * 같은 날에 발령된 경보 등급을 따져보고 경보 등급이 상승하면 알림 발송. 이미 발령된 윗 등급의 경보의 해제 여부는 고려하지 않는다.
		 */
		if (!jsonArr.isEmpty()){
			for (int i=1; i<jsonArr.size(); i++) {
				JSONObject dustData = (JSONObject) jsonArr.get(i);
				JSONObject formerDustData = (JSONObject) jsonArr.get(i-1);

				String[] s = ((String) dustData.get("dateTime")).split(" ");
				String date = s[0];
				String time = s[1];
				String region = (String) dustData.get("region");
				int pm10 = (dustData.get("PM10")==null) ? 0 : Integer.parseInt((String) dustData.get("PM10"));
				int pm2_5 = (dustData.get("PM2.5")==null) ? 0 : Integer.parseInt((String) dustData.get("PM2.5"));

				String[] s2 = ((String) formerDustData.get("dateTime")).split(" ");
				String formerDate = s2[0];
				String formerRegion = (String) formerDustData.get("region");
				int formerPm10 = (formerDustData.get("PM10")==null) ? 0 : Integer.parseInt((String) formerDustData.get("PM10"));
				int formerPm2_5 = (formerDustData.get("PM2.5")==null) ? 0 : Integer.parseInt((String) formerDustData.get("PM2.5"));

				if (!region.equals(formerRegion)) continue;

				if (pm2_5 >= 150 && formerPm2_5 >= 150) {
					int grade = 1;

					alertOrNot(em, region, date, time, grade);
				}
				else if (pm10 >= 300 && formerPm10 >= 300) {
					int grade = 2;
					alertOrNot(em, region, date, time, grade);
				}
				else if (pm2_5 >= 75 && formerPm2_5 >= 75) {
					int grade = 3;
					alertOrNot(em, region, date, time, grade);
				}
				else if (pm10 >= 150 && formerPm10 >= 150) {
					int grade = 4;
					alertOrNot(em, region, date, time, grade);
				}
			}
		}
		tx.commit(); // 커밋안하면 반영이 안된다
		em.close();
		emf.close();


		ServerTestThread serverTestThread1 = new ServerTestThread("중구");
//		ServerTestThread serverTestThread2 = new ServerTestThread(12002);
//		ServerTestThread serverTestThread3 = new ServerTestThread(12003);
//		ServerTestThread serverTestThread4 = new ServerTestThread(12004);
//		ServerTestThread serverTestThread5 = new ServerTestThread(12005);
//		ServerTestThread serverTestThread6 = new ServerTestThread(12006);
//		ServerTestThread serverTestThread7 = new ServerTestThread(12007);
//		ServerTestThread serverTestThread8 = new ServerTestThread(12008);
//		ServerTestThread serverTestThread4 = new ServerTestThread(12009);
//		ServerTestThread serverTestThread5 = new ServerTestThread(12010);
//		ServerTestThread serverTestThread1 = new ServerTestThread(12011);
//		ServerTestThread serverTestThread2 = new ServerTestThread(12012);
//		ServerTestThread serverTestThread3 = new ServerTestThread(12013);
//		ServerTestThread serverTestThread4 = new ServerTestThread(12014);
//		ServerTestThread serverTestThread5 = new ServerTestThread(12015);
//		ServerTestThread serverTestThread1 = new ServerTestThread(12016);
//		ServerTestThread serverTestThread2 = new ServerTestThread(12017);
//		ServerTestThread serverTestThread3 = new ServerTestThread(12018);
//		ServerTestThread serverTestThread4 = new ServerTestThread(12019);
//		ServerTestThread serverTestThread5 = new ServerTestThread(12020);
//		ServerTestThread serverTestThread1 = new ServerTestThread(12021);
//		ServerTestThread serverTestThread2 = new ServerTestThread(12022);
//		ServerTestThread serverTestThread3 = new ServerTestThread(12023);
//		ServerTestThread serverTestThread4 = new ServerTestThread(12024);
//		ServerTestThread serverTestThread5 = new ServerTestThread(12025);
		serverTestThread1.start();
		serverTestThread2.start();
		serverTestThread3.start();
		serverTestThread4.start();
		serverTestThread5.start();
	}

	static void alertOrNot(EntityManager em, String region, String date, String time, int grade) {
		List<Alert> alertList = em.createQuery("select a from Alert a " +
						"where a.region = :region " +
						"and a.date = :date " +
						"and a.grade <= :grade", Alert.class)
				.setParameter("region", region)
				.setParameter("date", date)
				.setParameter("grade", grade).getResultList();

		if (alertList.isEmpty()) {
			em.persist(Alert.builder()
					.grade(grade)
					.region(region)
					.date(date)
					.time(time).build());
		}
	}

	static void init() {
		regionPortMapping.put("중구", 12001);
		regionPortMapping.put("종로구", 12002);
		regionPortMapping.put("용산구", 12003);
		regionPortMapping.put("광진구", 12004);
		regionPortMapping.put("성동구", 12005);
		regionPortMapping.put("중랑구", 12006);
		regionPortMapping.put("동대문구", 12007);
		regionPortMapping.put("성북구", 12008);
		regionPortMapping.put("도봉구", 12009);
		regionPortMapping.put("은평구", 12010);
		regionPortMapping.put("서대문구", 12011);
		regionPortMapping.put("마포구", 12012);
		regionPortMapping.put("구로구", 12014);
		regionPortMapping.put("영등포구", 12015);
		regionPortMapping.put("동작구", 12016);
		regionPortMapping.put("관악구", 12017);
		regionPortMapping.put("강남구", 12018);
		regionPortMapping.put("서초구", 12019);
		regionPortMapping.put("송파구", 12020);
		regionPortMapping.put("강동구", 12021);
		regionPortMapping.put("금천구", 12022);
		regionPortMapping.put("강북구", 12023);
		regionPortMapping.put("양천구", 12024);
		regionPortMapping.put("노원구", 12025);
	}
}