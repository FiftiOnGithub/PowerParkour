package me.anon.main;

public class ParkourLevel {
	
	private String name;
	private Integer condition;
	private Integer chestPos;
	private String location;
	private long goldTime;
	
	public ParkourLevel(String n,Integer co,Integer cp,String loc,long gt) {
		this.name = n;
		this.condition = co;
		this.chestPos = cp;
		this.location = loc;
		this.goldTime = gt;
		Main.LEVELS.add(this);
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
	
}
