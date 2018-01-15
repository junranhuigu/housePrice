package com.junranhuigu.housePrice.vo;

public class MapVo {
	private String detailMapAddress;//详细地图地址
	private String location;//坐标
	
	public MapVo() {
		// TODO Auto-generated constructor stub
	}
	
	public MapVo(String detailMapAddress, String location) {
		this.detailMapAddress = detailMapAddress;
		this.location = location;
	}

	public String getDetailMapAddress() {
		return detailMapAddress;
	}
	public void setDetailMapAddress(String detailMapAddress) {
		this.detailMapAddress = detailMapAddress;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
}
