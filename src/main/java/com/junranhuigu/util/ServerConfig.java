package com.junranhuigu.util;

import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.LoggerFactory;

public class ServerConfig {
	private static ServerConfig instance;
	private Properties properties;
	
	private ServerConfig() {
		try {
			properties = new Properties();
			String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
			properties.load(new FileInputStream(path + "config.properties"));
		} catch (Exception e) {
			LoggerFactory.getLogger(ServerConfig.class).error("", e);
		}
	}

	public static ServerConfig getInstance() {
		if(instance == null){
			instance = new ServerConfig();
		}
		return instance;
	}
	
	public String get(String key){
		return properties.getProperty(key, null);
	}
}
