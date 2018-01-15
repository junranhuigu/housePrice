package com.junranhuigu.util;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	/**
	 * 验证字符串是否为空
	 * */
	public static boolean isEmpty(String s){
		boolean result = true;
		if(s != null && !"".equals(s)){
			result = false;
		}
		return result;
	}
	
	/**
	 * 输出两位数的值
	 * 如：1输出01,12输出12,100以上和负数输出为00
	 * */
	public static String getDoubleNumber(int number){
		String result = null;
		if(number < 0 || number >= 100 ){
			result = "00";
		} else if(number < 10){
			result = "0" + number;
		} else {
			result = "" + number;
		}
		return result;
	}
	/**
	 * 输出两位数的值
	 * 如：1输出01,12输出12,100以上和负数输出为00
	 * */
	public static String getDoubleNumber(String numberString){
		int number = Integer.parseInt(numberString);
		String result = null;
		if(number < 0 || number >= 100 ){
			result = "00";
		} else if(number < 10){
			result = "0" + number;
		} else {
			result = "" + number;
		}
		return result; 
	}
	/**
	 * String如果为"",则转化为null，反之则返回原结果
	 * */
	public static String nullFilter(String result){
		if("".equals(result)){
			result = null;
		}
		return result;
	}
	/**
	 * 获取一个由 n位的由0-9、a-z、A-Z 构成的字符串（用于验证啦~~、序列号啦~~之类的）
	 * */
	public static String getRandomString(int n){
		StringBuilder builder = new StringBuilder();
		Random random = new Random();
		for(int i = 0; i < n; ++ i){
			int randomNum = random.nextInt(62);//随机数 0-9为数字0-9,10-35为字母a-z,36-61为字母A-Z
			if(randomNum <= 9){
				builder.append((char)('0' + randomNum));
			} else if(randomNum <= 35){
				builder.append((char)(randomNum - 10 + 'a'));
			} else {
				builder.append((char)(randomNum - 36 + 'A'));
			}
		}
		return builder.toString();
	}
	
	/**
	 * 验证字符串是否含有特殊字符
	 * */
	public static boolean hasSpecialChar(String s){
		boolean result = true;
		if(s != null && !"".equals(s)){
			try {
				Pattern pattern = Pattern.compile("[^a-z0-9]");
				Matcher matcher = pattern.matcher(s.toLowerCase());
				result = matcher.find();
			} catch (Exception e) {
				System.out.println("字符串验证匹配错误");
				e.printStackTrace();
			}
		}
		return result;
	}
	/**
	 * 将一种格式的字符串转为另一种格式
	 * */
	public static String convert(String s, String oldCharSet, String newCharSet){
		if(s != null && !"".equals(s)){
			try {
				return new String(s.getBytes(oldCharSet), newCharSet);
			} catch (Exception e) {
				System.out.println("字符串转换 from " + oldCharSet + " to " + newCharSet +" 失败");
				e.printStackTrace();
			}
		}
		return s;
	}
	/**
	 * 将long值转为中文时间字符串
	 * */
	public static String parseLong2TimeStr(long time){
		if(time >= 0){
			StringBuilder builder = new StringBuilder();
			long secondUnit = 1000L;
			long minuteUnit = 60 * secondUnit;
			long hourUnit = 60 * minuteUnit;
			long dayUnit = 24 * hourUnit;
			if(time >= dayUnit){//日
				builder.append(time / dayUnit).append("日");
				time %= dayUnit;
			}
			if(time >= hourUnit){
				builder.append(time / hourUnit).append("时");
				time %= hourUnit;
			}
			if(time >= minuteUnit){
				builder.append(time / minuteUnit).append("分");
				time %= minuteUnit;
			}
			if(time >= secondUnit){
				builder.append(time / secondUnit).append("秒");
				time %= secondUnit;
			}
			if(time > 0){
				builder.append(time).append("毫秒");
			}
			return builder.toString();
		}
		return null;
	}
	
	/**
	 * MD5加密
	 * */
	public static String md5(byte[] bytes){
		if(bytes != null){
			try {
				MessageDigest digest = MessageDigest.getInstance("MD5"); 
				digest.update(bytes);
				byte[] bs = digest.digest();
				StringBuilder builder = new StringBuilder();
				for(int i = 0; i < bs.length; ++ i){
					int md = bs[i];
					if(md < 0){
						md += 256;
					}
					if(md < 16){
						builder.append("0");
					}
					builder.append(Integer.toHexString(md));
				}
				return builder.toString();
			} catch (Exception e) {
				System.out.println("MD5加密字符串失败");
			}
		}
		return "";
	}
	
	/**
	 * 将字符串第一字母转换为大写字母
	 * */
	public static String upperFirstLetter(String content){
		char firstLetter = content.charAt(0);
		if(firstLetter >= 'a' && firstLetter <= 'z'){
			char replacement = (char) (firstLetter - 32);
			content = content.replaceFirst(firstLetter + "", replacement + "");
		}
		return content;
	}
	
	/**
	 * 分割字符串
	 * */
	public static List<String> parseList(String content, String seperator){
		List<String> result = new ArrayList<>();
		for(String s : content.split(seperator)){
			result.add(s);
		}
		return result;
	}
	
	/**
	 * 分割字符串并转为int类型
	 * */
	public static List<Integer> parseList2Int(String content, String seperator){
		List<Integer> result = new ArrayList<>();
		for(String s : content.split(seperator)){
			result.add(Integer.parseInt(s));
		}
		return result;
	}
	
	public static boolean hasChinese(String content){
		Pattern pattern = Pattern.compile("[\u4E00-\u9FA5]{1,}");
		Matcher matcher = pattern.matcher(content);
		return matcher.find();
	}
	
	/**
	 * 去除非中文字符
	 * */
	public static String filteNotChinese(String content){
		Pattern pattern = Pattern.compile("[^\u4E00-\u9FA5]{1,}");
		Matcher matcher = pattern.matcher(content);
		return matcher.replaceAll("");
	}
	
	/**
	 * 去除两边文字两边空格
	 * */
	public static String filteHeadOrTailBlank(String content){
		Pattern pattern = Pattern.compile("(^\\s*)|(\\s*$)");
		Matcher matcher = pattern.matcher(content);
		return matcher.replaceAll("");
	}
	
	public static void main(String[] args) {
		System.out.println(getRandomString(10));
	}
}
