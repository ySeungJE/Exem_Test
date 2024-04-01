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
import java.util.*;

public class ServerMain {
	public static final Map<String, Integer> regionPortMapping = new HashMap<>();

	public static EntityManagerFactory emf = Persistence.createEntityManagerFactory("exem");
	public static EntityManager em = emf.createEntityManager();
	public static EntityTransaction tx = em.getTransaction();


	/** ============== Main 함수 시작 ==============
	 * JSON 파일 Read
	 * 미세먼지 경보 측정 로직, H2 Database에 저장
	 * 자치구 별 서버 소켓 port를 열기 위해 Server Thread 생성
	 */
	public static void main(String[] args) throws IOException, ParseException {

		dataInit();

		String path = System.getProperty("user.dir");
		Reader reader = new FileReader(path + "\\data.json");

		JSONParser parser = new JSONParser();
		Object obj = parser.parse(reader);
		JSONArray jsonArr = (JSONArray)obj;

		tx.begin();

		if (!jsonArr.isEmpty()){
			for (int i=1; i<jsonArr.size(); i++) {
				JSONObject dustData = (JSONObject) jsonArr.get(i);
				JSONObject formerDustData = (JSONObject) jsonArr.get(i-1);

				String[] s = ((String) dustData.get("dateTime")).split(" ");
				String date = s[0];
				String time = s[1];

				// 현재 시간의 미세먼지 정보
				String region = (String) dustData.get("region");
				int pm10 = (dustData.get("PM10")==null) ? 0 : Integer.parseInt((String) dustData.get("PM10"));
				int pm2_5 = (dustData.get("PM2.5")==null) ? 0 : Integer.parseInt((String) dustData.get("PM2.5"));

				// 한 시간 전의 미세먼지 정보
				String formerRegion = (String) formerDustData.get("region");
				int formerPm10 = (formerDustData.get("PM10")==null) ? 0 : Integer.parseInt((String) formerDustData.get("PM10"));
				int formerPm2_5 = (formerDustData.get("PM2.5")==null) ? 0 : Integer.parseInt((String) formerDustData.get("PM2.5"));

				// 자치구가 바뀌면 로직을 진행하지 않는다
				if (!region.equals(formerRegion)) continue;

				// 경보 단계를 측정한 후 발령할 지 말지 결정한다
				if (pm2_5 >= 150 && formerPm2_5 >= 150) {
					int grade = 1;
					alertOrNot(em, region, date, time, grade,"초미세먼지(pm2.5)경보("+pm2_5+"㎍/㎥)");
				}
				else if (pm10 >= 300 && formerPm10 >= 300) {
					int grade = 2;
					alertOrNot(em, region, date, time, grade,"미세먼지(pm10)경보("+pm10+"㎍/㎥)");
				}
				else if (pm2_5 >= 75 && formerPm2_5 >= 75) {
					int grade = 3;
					alertOrNot(em, region, date, time, grade,"초미세먼지(pm2.5)주의보("+pm2_5+"㎍/㎥)");
				}
				else if (pm10 >= 150 && formerPm10 >= 150) {
					int grade = 4;
					alertOrNot(em, region, date, time, grade,"미세먼지(pm10)주의보("+pm10+"㎍/㎥)");
				}
			}
		}

		tx.commit();

		regionPortMapping.forEach((region, port) -> new ServerThread(region, em).start());
	}

	static void alertOrNot(EntityManager em, String region, String date, String time, int grade, String degree) {

		// 같은 날에 발령된 경보 등급을 따져보고, 경보 등급이 상승하면 알림 발송. 이미 발령된 윗 등급의 경보의 해제 여부는 고려하지 않는다
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
					.time(time)
					.degree(degree).build());
		}
	}

	static void dataInit() {
		String[] regions = {"중구", "종로구", "용산구","광진구", "성동구", "중랑구", "동대문구", "성북구", "도봉구", "은평구", "서대문구", "마포구", "강서구",
				"구로구", "영등포구", "동작구", "관악구", "강남구", "서초구", "송파구", "강동구", "금천구", "강북구", "양천구", "노원구"};

		for (int i = 12001; i < 12001+regions.length; i++) {
			regionPortMapping.put(regions[i-12001], i);
		}
	}
}