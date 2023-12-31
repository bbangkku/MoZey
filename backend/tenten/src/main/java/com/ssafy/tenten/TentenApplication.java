package com.ssafy.tenten;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.tenten.api.service.CoinInfoService;
import com.ssafy.tenten.domain.CoinInfo;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@EnableScheduling
@SpringBootApplication
@Slf4j
public class TentenApplication {
	@Autowired
	private CoinInfoService coinInfoService;

	public static void main(String[] args) {
		SpringApplication.run(TentenApplication.class, args);
	}

	@Bean
	public static ModelMapper modelMapper(){
		return new ModelMapper();
	}

	@Bean
	JPAQueryFactory jpaQueryFactory(EntityManager em) {
		return new JPAQueryFactory(em);
	}

	@Bean
	FirebaseMessaging firebaseMessaging() throws IOException {
		ClassPathResource resource = new ClassPathResource("templates/firebase-key.json");

		InputStream refreshToken = resource.getInputStream();

		FirebaseApp firebaseApp = null;
		List<FirebaseApp> firebaseAppList = FirebaseApp.getApps();

		if (firebaseAppList != null && !firebaseAppList.isEmpty()) {
			for (FirebaseApp app : firebaseAppList) {
				if (app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)) {
					firebaseApp = app;
				}
			}
		} else {
			FirebaseOptions options = FirebaseOptions.builder()
					.setCredentials(GoogleCredentials.fromStream(refreshToken))
					.build();

			firebaseApp = FirebaseApp.initializeApp(options);
		}

		return FirebaseMessaging.getInstance(firebaseApp);
	}
	@Transactional
	@Scheduled(cron = "0 0 9 * * *")
	public void saveTodayCoinInfo() throws Exception {
		log.info("코인 정보 Update");
		try{
			CoinInfo coinInfo = coinInfoService.createCoinInfo();
		}catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

}
