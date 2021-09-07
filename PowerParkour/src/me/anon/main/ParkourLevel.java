package me.anon.main;

public class ParkourLevel {
	
	private final String name;
	private final Integer condition;
	private final Integer chestPos;
	private final String location;
	private final long goldTime;
	
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
