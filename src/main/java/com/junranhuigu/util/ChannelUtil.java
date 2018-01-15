package com.junranhuigu.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 工具类
 * */
public class ChannelUtil {
	/**
	 * http方式请求交互
	 * @param uri 请求的路径
	 * @param params 需要发送的参数内容
	 * @param requestMethod 请求模式 GET POST DELETE PUT
	 * @return 请求地址返回的结果
	 * */
	public static String http(String uri, String params, String requestMethod) throws Exception{
		URL url = new URL(uri);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		connection.getRequestProperty(requestMethod);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestProperty("charset", "utf-8");
		connection.setReadTimeout(30000);
		connection.setConnectTimeout(30000);
		
		if(params != null && !"".equals(params)){
			DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
			dos.writeBytes(params);
			dos.flush();
			dos.close();
		}

		connection.connect();
		
		String line = null;
		BufferedReader bufferedReader = null;
		StringBuilder sb = new StringBuilder();
		InputStreamReader streamReader = null;
		try {
			streamReader = new InputStreamReader(connection.getInputStream(), "UTF-8");
		} finally {
			if (streamReader != null) {
				bufferedReader = new BufferedReader(streamReader);
				sb = new StringBuilder();
				while ((line = bufferedReader.readLine()) != null) {
					sb.append(line);
				}
				streamReader.close();
			}
		}
		return sb.toString();
	}
	
	/**
	 * 获取得到排序好的网址字符串
	 * @param params 请求参数
	 * @return
	 */
	public static String getSortQueryString(Map<String,String> params) {
		Object[] keys = params.keySet().toArray();
		Arrays.sort(keys);
		StringBuffer sb = new StringBuffer();
		for(Object key : keys){
			sb.append(String.valueOf(key)).append("=").append(params.get(String.valueOf(key))).append("&");
		}
		
		String text = sb.toString();
		if(text.endsWith("&")) {
			text=text.substring(0,text.length()-1);
		}
		return text;
	}
	
	/**
	 * https请求交互
	 * @param uri 请求的路径
	 * @param params 需要发送的参数内容
	 * @param requestMethod 请求模式 GET POST DELETE PUT
	 * @return 请求地址返回的结果
	 * */
	public static String https(String path, String params, String requestMethod) throws Exception{
		TrustManager[] managers = {new ChannelUtil().provideTrust()};
		SSLContext context = SSLContext.getInstance("SSL", "SunJSSE");
		context.init(null, managers, new SecureRandom());
		
		HttpsURLConnection connection = (HttpsURLConnection) new URL(path).openConnection();
		connection.getRequestProperty(requestMethod);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setConnectTimeout(3000);
		connection.setSSLSocketFactory(context.getSocketFactory());
		
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
		connection.setRequestProperty("charset", "utf-8");
		
		if(params != null && !"".equals(params)){
			DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
			dos.writeBytes(params);
			dos.flush();
			dos.close();
		}
		
		String line = null;
		BufferedReader bufferedReader = null;
		StringBuilder sb = new StringBuilder();
		InputStreamReader streamReader = null;
		try {
			streamReader = new InputStreamReader(connection.getInputStream(), "UTF-8");
		} catch (IOException e) {
			streamReader = new InputStreamReader(connection.getErrorStream(), "UTF-8");
		} finally {
			if (streamReader != null) {
				bufferedReader = new BufferedReader(streamReader);
				sb = new StringBuilder();
				while ((line = bufferedReader.readLine()) != null) {
					sb.append(line);
				}
				streamReader.close();
			}
		}
		return sb.toString();
	}
	
	private PrivateTrust provideTrust(){
		return new PrivateTrust();
	}
	
	private class PrivateTrust implements X509TrustManager{
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}
	
	/**
	 * 从网络下载文件
	 * */
	public static void download(String url, File file, Charset charset) throws Exception{
		if(file.exists()){
			file.delete();
		}
		file.createNewFile();

		HttpURLConnection connection = null;
		connection = (HttpURLConnection) new URL(url).openConnection();
		
		connection.getRequestProperty("GET");
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(false);
		connection.setRequestProperty("charset", charset.name());
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
		connection.setConnectTimeout(3000);
		connection.setReadTimeout(10000);

		connection.connect();
		
		try (	InputStream is = connection.getInputStream();
				FileOutputStream fos = new FileOutputStream(file);) {
			byte[] bs = new byte[4096];
			int len = 0;
			while((len = is.read(bs)) >= 0){
				fos.write(bs, 0, len);
			}
			fos.flush();
		}
		System.out.println(url + "下载完毕 -> " + file.getName());
	}
}
