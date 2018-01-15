package com.junranhuigu.housePrice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.junranhuigu.housePrice.vo.From;
import com.junranhuigu.housePrice.vo.HousePriceVo;
import com.junranhuigu.housePrice.vo.MapVo;
import com.junranhuigu.simpleJson.JsonUtil;
import com.junranhuigu.util.ChannelUtil;
import com.junranhuigu.util.ServerConfig;
import com.junranhuigu.util.StringUtil;

/**
 * 爬虫
 * */
public class Crawler {
	
	public static void main(String[] args) throws Exception{
//		List<HousePriceVo> list = houseDataByWoai("http://bj.5i5j.com/rent");
//		List<HousePriceVo> list = houseDataByXiangyu("http://xiangyu.5i5j.com/houseForPc/getHouseInitList?flag=0");
//		List<HousePriceVo> list = houseDataByDanke("http://www.dankegongyu.com/room/bj?page=");
//		FileUtil.save(list, "C:\\Users\\jiawei\\Desktop\\housePrice.txt", Charset.forName(ServerConfig.getInstance().get("charset")));
	}
	
	/**
	 * 读取数据文件
	 * */
	public static Map<String, HousePriceVo> houseData() throws Exception{
		Map<String, HousePriceVo> map = new HashMap<>();
		try(	BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(ServerConfig.getInstance().get("saveFilePath"))), ServerConfig.getInstance().get("charset")))){
			while(reader.ready()){
				String line = reader.readLine();
				HousePriceVo vo = JSON.parseObject(line, HousePriceVo.class);
				map.put(vo.id(), vo);
			}
		}
		return map;
	}
	
	/**
	 * 爬取租房房价数据——我爱我家
	 * */
	public static List<HousePriceVo> houseDataByWoai(String url) throws IOException {
		List<HousePriceVo> resultlist = new ArrayList<>();
		Set<String> names = new HashSet<>();
		
		Document doc = Jsoup.connect(url).get();
		int total = Integer.parseInt(doc.select("font[class=font-houseNum]").text());
		Elements es = doc.select("ul[class=list-body] > li");
		int size = es.size();
		int top = (int) Math.ceil(total / (size + 0.0f));
		for(int i = 1; i <= top; ++ i){
			String _url = url + "/n" + i;
			LoggerFactory.getLogger(Crawler.class).info(_url + "抓取数据");
			try {
				doc = Jsoup.connect(_url).get();
				es = doc.select("ul[class=list-body] > li");
				for(Element e : es){
					String id = e.select("h2").select("a").attr("href").replace("/rent/", "");
					String address = StringUtil.filteHeadOrTailBlank(e.select("h3[class=rent-font]").text().replace(" ", ""));
					Elements spans = e.select("li[class=font-balck]").select("span");
					String structure = StringUtil.filteHeadOrTailBlank(e.select("h2").select("a").text().replace(address, ""));
					String area = spans.get(1).text();
					String direction = spans.get(2).text();
					String floor = spans.get(3).text();
					String price = e.select("div[class=list-info-r]").select("h3").text();
					String mode = e.select("div[class=list-info-r]").select("p").text();
					
					HousePriceVo vo = new HousePriceVo(From.我爱我家, id, address, structure, area, direction, floor, price, mode);
					names.add(vo.getAddress());
					resultlist.add(vo);
				}
			} catch (Exception e) {
				LoggerFactory.getLogger(Crawler.class).error(_url + "抓取数据出现错误");
				if(App.SHOW_ERROR_DETAIL){
					LoggerFactory.getLogger(Crawler.class).error("", e);
				}
			}
		}
		//封装地图信息
		Map<String, MapVo> map = mapPosition(new ArrayList<>(names), "北京");
		for(HousePriceVo vo : resultlist){
			MapVo mapVo = map.get(vo.getAddress());
			if(mapVo != null){
				vo.setMap(mapVo);
			} else {
				System.out.println(vo.getAddress() + " "  + mapVo);
			}
		}
		return resultlist;
	}
	
	/**
	 * 爬取租房房价数据——相寓
	 * */
	public static List<HousePriceVo> houseDataByXiangyu(String url) throws IOException {
		List<HousePriceVo> resultlist = new ArrayList<>();
		Set<String> names = new HashSet<>();
		
		Document doc = Jsoup.connect(url).get();
		int total = Integer.parseInt(doc.select("p[class=house-num] > span").text());
		int size = doc.select("ul[class=list-details] > li").size();
		int top = (int) Math.ceil(total / (size + 0.0f));
		for(int i = 1; i <= top; ++ i){
			String path = "http://xiangyu.5i5j.com/houseForPc/getHouseList?flag=0&pageNum=" + i;
			LoggerFactory.getLogger(Crawler.class).info(path + "抓取数据");
			try {
				doc = Jsoup.connect(path).get();
				for(Element e : doc.select("ul[class=list-details] > li")){
					Pattern pattern = Pattern.compile("\\d+");
					String idStr = e.attr("onclick");
					Matcher matcher = pattern.matcher(idStr);
					StringBuilder idBuilder = new StringBuilder();
					while(matcher.find()){
						idBuilder.append(matcher.group()).append("-");
					}
					String id = idBuilder.toString().replace("-2-", "");
					
					String address = e.select("h2").text();
					Elements es = e.select("p[style]");
					String structure = StringUtil.filteHeadOrTailBlank(es.get(0).text().split("\\|")[1]);
					String area = StringUtil.filteHeadOrTailBlank(es.get(0).text().split("\\|")[0]);
					String direction = es.size() >= 2 ? es.get(1).text().replace("| ", "") : null;
					String floor = null;
					String price = e.getElementById("cost").text();
					String mode = e.select("span").get(2).text();
					
					HousePriceVo vo = new HousePriceVo(From.相寓, id, address, structure, area, direction, floor, price, mode);
					System.out.println(vo);
					resultlist.add(vo);
					names.add(vo.getAddress());
				}
				Thread.sleep(10);
			} catch (Exception e) {
				LoggerFactory.getLogger(Crawler.class).error(path + "抓取数据出现错误");
				if(App.SHOW_ERROR_DETAIL){
					LoggerFactory.getLogger(Crawler.class).error("", e);
				}
			}
			break;
		}
		//封装地图信息
		Map<String, MapVo> map = mapPosition(new ArrayList<>(names), "北京");
		for(HousePriceVo vo : resultlist){
			MapVo mapVo = map.get(vo.getAddress());
			if(mapVo != null){
				vo.setMap(mapVo);
			} else {
				System.out.println(vo.getAddress() + " "  + mapVo);
			}
		}
		return resultlist;
	}
	
	/**
	 * 爬取租房房价数据——蛋壳公寓
	 * */
	public static List<HousePriceVo> houseDataByDanke(String url) throws IOException {
		List<HousePriceVo> resultlist = new ArrayList<>();
		Set<String> names = new HashSet<>();
		
		int top = 1;
		while(true){
			boolean has = false;
			String _url = url + (top ++);
			try {
				LoggerFactory.getLogger(Crawler.class).info(_url + "抓取数据");
				Document doc = Jsoup.connect(_url).get();
				for(Element e : doc.select("div[class=r_lbx]")){
					has = true;
					Pattern pattern = Pattern.compile("\\d+");
					Matcher matcher = pattern.matcher(e.select("> a").attr("href"));
					matcher.find();
					String id = matcher.group();
					
					String mode = e.select("div[class=r_lbx_cenb] > i").text();
					mode = StringUtil.isEmpty(mode) ? "整租" : mode + "租";
					String[] infos1 = e.select("div[class=r_lbx_cen] > a").text().split(" ");
					String[] infos2 = e.select("div[class=r_lbx_cenb]").text().replace(mode, "").split("\\|");
					String address = infos1[1];
					String structure = StringUtil.filteHeadOrTailBlank(infos2[2]);
					String area = StringUtil.filteHeadOrTailBlank(infos2[0]);
					String direction = (infos1.length > 3 ? StringUtil.filteHeadOrTailBlank(infos1[2]) + " " + StringUtil.filteHeadOrTailBlank(infos1[4]) + " " : "") + StringUtil.filteHeadOrTailBlank(infos2[3]);
					String floor = StringUtil.filteHeadOrTailBlank(infos2[1]);
					String price = e.select("div[class=r_lbx_moneya]").text();
					
					HousePriceVo vo = new HousePriceVo(From.蛋壳公寓, id, address, structure, area, direction, floor, price, mode);
					resultlist.add(vo);
					names.add(vo.getAddress());
				}
				if(!has){
					break;
				}
			} catch (Exception e) {
				LoggerFactory.getLogger(Crawler.class).error(_url + "抓取数据出现错误");
				if(App.SHOW_ERROR_DETAIL){
					LoggerFactory.getLogger(Crawler.class).error("", e);
				}
			}
		}
		//封装地图信息
		Map<String, MapVo> map = mapPosition(new ArrayList<>(names), "北京");
		for(HousePriceVo vo : resultlist){
			MapVo mapVo = map.get(vo.getAddress());
			if(mapVo != null){
				vo.setMap(mapVo);
			} else {
				System.out.println(vo.getAddress() + " "  + mapVo);
			}
		}
		return resultlist;
	}
	
	
	private static MapVo mapPosition(String locationName, String cityName){
		List<String> loList = new ArrayList<>();
		loList.add(locationName);
		return mapPosition(loList, cityName).get(locationName);
	}
	
	private static Map<String, MapVo> mapPosition(List<String> locationName, String cityName){
		String url = "http://restapi.amap.com/v3/geocode/geo?";
		Map<String, String> params = new HashMap<>();
		Map<String, MapVo> result = new HashMap<>();
		for(int i = 0; i < locationName.size(); i += 10){
			params.clear();
			params.put("key", ServerConfig.getInstance().get("mapKey"));
			List<String> sublist = null;
			if(i + 10 > locationName.size()){//剩余数量不足10个
				sublist = locationName.subList(i, locationName.size());
			} else {
				sublist = locationName.subList(i, i + 10);
			}
			StringBuilder locationNames = new StringBuilder();
			for(int j = 0; j < sublist.size(); ++ j){
				String a = sublist.get(j);
				locationNames.append(a).append("|");
			}
			locationNames.deleteCharAt(locationNames.length() - 1);
			params.put("address", locationNames.toString());
			params.put("city", cityName);
			params.put("batch", true + "");
			
			String path = url + ChannelUtil.getSortQueryString(params);
			try {
				String response = ChannelUtil.http(path, null, "GET");
				
				List<String> detailAddress = JsonUtil.select("root.geocodes.formatted_address", response, String.class);
				List<String> location = JsonUtil.select("root.geocodes.location", response, String.class);
				for(int j = 0; j < sublist.size(); ++ j){
					Object da = detailAddress.get(j);
					Object l = location.get(j);
					MapVo vo = new MapVo(da.toString(), l.toString());
					result.put(sublist.get(j), vo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
