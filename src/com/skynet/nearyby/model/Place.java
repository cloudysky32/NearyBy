package com.skynet.nearyby.model;

public class Place {
	private String name;
	private String addr;
	private String json;
	private String icon;
	private double lat;
	private double lng;
	
	public Place(String name, String addr, double lat, double lng, String icon, String json) {
		this.setName(name);
		this.setAddr(addr);
		this.setLat(lat);
		this.setLng(lng);
		this.setIcon(icon);
		this.setJson(json);
	}

	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	public double getLat() {
		return lat;
	}

	private void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	private void setLng(double lng) {
		this.lng = lng;
	}

	public String getAddr() {
		return addr;
	}

	private void setAddr(String addr) {
		this.addr = addr;
	}

	public String getJson() {
		return json;
	}

	private void setJson(String json) {
		this.json = json;
	}

	public String getIcon() {
		return icon;
	}

	private void setIcon(String icon) {
		this.icon = icon;
	}
}
