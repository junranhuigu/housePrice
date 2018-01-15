package com.junranhuigu.housePrice;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.junranhuigu.housePrice.vo.HousePriceVo;
import com.junranhuigu.util.FileUtil;
import com.junranhuigu.util.ServerConfig;


/**
 * Hello world!
 * 
 */
public class App {
	public static boolean SHOW_ERROR_DETAIL = true;
	
	public static void main(String[] args) throws Exception {
		Map<String, HousePriceVo> map = null;
		try {
			map = Crawler.houseData();
		} catch (Exception e) {
			map = new HashMap<>();
		}
		List<HousePriceVo> list = new ArrayList<>();
		list.addAll(Crawler.houseDataByWoai("http://bj.5i5j.com/rent"));
		list.addAll(Crawler.houseDataByXiangyu("http://xiangyu.5i5j.com/houseForPc/getHouseInitList?flag=0"));
		list.addAll(Crawler.houseDataByDanke("http://www.dankegongyu.com/room/bj?page="));
		
		for(HousePriceVo vo : list){
			if(!map.containsKey(vo.id())){
				map.put(vo.getId(), vo);
			}
		}
		FileUtil.cover(new ArrayList<>(map.values()), "C:\\Users\\jiawei\\Desktop\\housePrice.txt", Charset.forName(ServerConfig.getInstance().get("charset")));
	}
}
