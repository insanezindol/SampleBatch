package com.wiz.samplebatch.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
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
		
		String springConfig = "context/"+ serverType +"_BaseContext.xml";
		ApplicationContext context =  new ClassPathXmlApplicationContext(springConfig);
		Main main = context.getBean(Main.class);

		main.summaryBatch();
		
		((ConfigurableApplicationContext) context).close();
		log.info("brmcmstat batch end");
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


