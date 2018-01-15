package com.junranhuigu.housePrice.vo;

import com.alibaba.fastjson.JSON;

public class HousePriceVo {
	private From from;//来源
	private String id;//编号
	private String address;//小区地址
	private String structure;//房屋结构 几室几厅
	private String area;//面积
	private String direction;//朝向
	private String floor;//层数
	private String price;//价格
	private String mode;//出租模式 整租/合租
	private long grepTime;//抓取时间
	private MapVo map;
	
	public HousePriceVo() {
		// TODO Auto-generated constructor stub
	}
	
	public HousePriceVo(From from, String id, String address, String structure,
			String area, String direction, String floor, String price,
			String mode) {
		this.from = from;
		this.id = id;
		this.address = address;
		this.structure = structure;
		this.area = area;
		this.direction = direction;
		this.floor = floor;
		this.price = price;
		this.mode = mode;
		this.grepTime = System.currentTimeMillis();
	}
	
	public String id(){
		return from + "-" + id;
	}

	public From getFrom() {
		return from;
	}
	public void setFrom(From from) {
		this.from = from;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getStructure() {
		return structure;
	}
	public void setStructure(String structure) {
		this.structure = structure;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public long getGrepTime() {
		return grepTime;
	}
	public void setGrepTime(long grepTime) {
		this.grepTime = grepTime;
	}
	public MapVo getMap() {
		return map;
	}
	public void setMap(MapVo map) {
		this.map = map;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this) + "\n";
	}
}
