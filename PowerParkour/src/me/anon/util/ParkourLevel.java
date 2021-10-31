package me.anon.util;

import me.anon.main.Main;

public class ParkourLevel {
	
	private final String name;
	private final Integer condition;
	private final Integer chestPos;
	private final String location;
	private final long goldTime;
	private final Integer id;
	
	public ParkourLevel(Integer levelid, String n,Integer co,Integer cp,String loc,long gt) {
		this.name = n;
		this.condition = co;
		this.chestPos = cp;
		this.location = loc;
		this.goldTime = gt;
		this.id = levelid;
		Main.LEVELS.put(levelid,this);
	}
	
	
	public long getGoldTime() {
		return goldTime;
	}
	public String getLocation() {
		return location;
	}
	public Integer getChestPos() {
		return chestPos;
	}
	public Integer getCondition() {
		return condition;
	}
	public String getName() {
		return name;
	}
	public Integer getID() { return id; }
	
}
