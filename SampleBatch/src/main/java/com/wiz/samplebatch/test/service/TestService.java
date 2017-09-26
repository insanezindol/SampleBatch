package com.wiz.samplebatch.test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wiz.samplebatch.test.mapper.ktmusic.TestMapperKtmusic;
import com.wiz.samplebatch.test.mapper.wsound.TestMapperWsound;
import com.wiz.samplebatch.test.mapper.xring.TestMapperXring;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("testService")
public class TestService {

	@Autowired
	private TestMapperKtmusic testMapperKtmusic;

	@Autowired
	private TestMapperXring testMapperXring;
	
	@Autowired
	private TestMapperWsound testMapperWsound;
	
	
    public void runTest1() throws Exception{
    	
    	int cnt =  testMapperKtmusic.selectKtmusicTest();
    	log.debug("Ktmusic cnt: "+cnt);

    	cnt =  testMapperXring.selectXringTest();
    	log.debug("Xring cnt: "+cnt);
    	
    	cnt =  testMapperWsound.selectWsoundTest();
    	log.debug("Wsound cnt: "+cnt);
    }
    
	@Transactional(value = "ktmusic_transactionManager", rollbackFor = Exception.class)
    public void runTest2() throws Exception{
    	
    	testMapperKtmusic.insertKtmusicTest();
    	
    	throw new Exception();
    	
    }
    
}
