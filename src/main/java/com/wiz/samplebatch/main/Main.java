package com.wiz.samplebatch.main;

import java.net.URISyntaxException;
import java.net.URL;

import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.wiz.samplebatch.test.service.TestService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Main {

	@Autowired
	private TestService testService;
	
	public static void main(String[] args) throws Exception{
		
		System.out.println("brmcmstat batch start");
		
		// 환경설정 분기를 위한 argument가 전달 되었는지 체크
		if (args.length == 0){
			throw new Exception("args is null. local|tb|real");
		}
		
		String serverType = "";
		
		try {
			serverType = args[0];
		} catch (Exception e) {}
		
		// 정확한 argument가 전달되었는지 체크
		if (!"local".equals(serverType) && !"tb".equals(serverType) && !"real".equals(serverType))
			throw new Exception("args is invalid ");
		
		// 환경별 Log4j2 설정 적용 (Spring 5 부터 Log4jConfigurer 가 제거되어 직접 초기화)
		initLogging("log4j/"+ serverType +"_log4j2.xml");
		
		String springConfig = "context/"+ serverType +"_BaseContext.xml";
		try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(springConfig)) {
			Main main = context.getBean(Main.class);
			main.summaryBatch();
		}
		
		log.info("brmcmstat batch end");
	}

	private static void initLogging(String configLocation) throws URISyntaxException {
		URL configUrl = Main.class.getClassLoader().getResource(configLocation);
		if (configUrl == null) {
			throw new IllegalStateException("log4j2 config not found: " + configLocation);
		}
		Configurator.reconfigure(configUrl.toURI());
	}

	public void summaryBatch() throws Exception{
		
		try {
			testService.runTest2();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			testService.runTest1();
		} catch (Exception e) {}
		
	}

}


