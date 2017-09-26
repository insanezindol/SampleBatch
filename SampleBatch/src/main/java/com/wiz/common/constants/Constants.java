
package com.wiz.common.constants;

import java.util.Properties;

import org.springframework.stereotype.Component;

@Component
public class Constants{
	
	public static Properties configProp;
	
	public static void setConfigProp(Properties configProp) {
		Constants.configProp = configProp;
	}
	
	public static String SERVER_TYPE = "system.whoami";
	
}