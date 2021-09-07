package me.anon.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ParkourPlayer {

	private final UUID uuid; // the uuid of the player
	private HashMap<Integer,ArrayList<Long>> feats; // a hashmap with the id for every level as key, and an arraylist with every completion time as value. used to determine whether gold stars are earned etc.
	private boolean plus; // has this person got plus?
	private ArrayList<ShopCosmetic> owned_packages; // these are the items that the player owns.
	private Integer playerLocation; //this is either a level id or "-1" (in lobby) or -3 in daily challenge
	private Integer failedTimes; // how many times has this player failed on the current parkour
	private String lastKnownName; // used by the leaderboards.
	private Location lastGroundLocation; // where to tp the player back to if they die during practice mdoe
	private boolean pracMode; // practice mode?
	
	private Integer dailyStreak; // the number of dailies this person has done in a row without missing any
	private long dailyTime; // their time on today's daly challenge.
	private Integer dailyLives; // how many lives does this person have in today's daily challenge?
	
	public ParkourPlayer(UUID uid,HashMap<Integer,ArrayList<Long>> feat,boolean pl, Integer dailyL, Integer dailyS, long dailyT,String lnn) {
		this.uuid = uid;
		this.feats = feat;
		this.plus = pl;
		this.dailyLives = dailyL;
		this.playerLocation = -1;
		if (this.feats.size() != Main.LEVELS.size()) {
			Bukkit.getLogger().severe("Unable to load feats for " + uid + ". Feats mismatch; user has " + this.feats.size() + " but there are " + Main.LEVELS.size() + " levels.");
		}
		this.setLastKnownName(lnn);
		lastGroundLocation = null;
		failedTimes = 0;
		pracMode = false;
		this.setDailyStreak(dailyS);
		this.setDailyTime(dailyT);
		
		
		Main.PLAYERS.put(uuid, this);
	}
	public boolean hasPlus() {
		return plus;
	}
	public Integer getFails() {
		return failedTimes;
	}
	public Location getLastGround() {
		return lastGroundLocation;
	}
	public void setLastGround(Location lgl) {
		lastGroundLocation = lgl;
	}
	public void setFails(Integer fails) {
		failedTimes = fails;
	}
	public Integer getLocation() {
		return playerLocation;
	}
	public void setLocation(Integer loc) {
		this.playerLocation = loc;
	}
	public int canPlayLevel(Integer levelid) {
		int levelCondition = Main.LEVELS.get(levelid).getCondition();
		if (this.feats.get(levelid).size() > 0) return 0; // Can play
		if (levelCondition == 0) return 0; // can play
		if (this.feats.get(levelid - 1).size() > 0 && levelCondition == 1) return 0; // Can play, condition is 1 and completed previous level
		if (levelCondition == 3 && this.plus) return 0; // Can play, this level requires plus and this player has it
		if (levelCondition == 2) {
			for (int i = 0; i < Main.LEVELS.size(); i++) {
				System.out.println("Doing test on " + Main.LEVELS.get(i).getName());
				System.out.println("has gold: " + hasGold(i));
				System.out.println("Condition: " + Main.LEVELS.get(i).getCondition());
				System.out.println("Failed checks: " + (!hasGold(i) && Main.LEVELS.get(i).getCondition() == 1));
				if (!hasGold(i) && Main.LEVELS.get(i).getCondition() == 1) return 2; // Can't play; this level requires all gold stars and this player is missing a gold star in at least 1 level
			}
			System.out.println("Player has all gold");
			return 0; // if the previous for loop completed successfully, then this player has gold stars from every level.
		}
		if (levelCondition == 3) return 3; // Can't play; this level requires plus and this player doesn't have it.
		if (levelCondition == 1) return 1; // can't play because hasn't completed previous level
		return 4; // this is a debug mode; the string should never show up in game.
	}
	public void setPlus(boolean targ) {
		plus = targ;
	}
	public UUID getUUID() {return uuid;}
	public Integer getLives() {return dailyLives;}
	public void setLives(Integer targ) {dailyLives = targ;}
	public HashMap<Integer,ArrayList<Long>> getFeats() {return feats;}
	
	public boolean setFeats(HashMap<Integer,ArrayList<Long>> feat) {
		if (feat.size() != Main.LEVELS.size()) return false;
		feats = feat;
		return true;
	}
	
	public boolean addFeat(Long time,Integer levelid) {
		if (Main.LEVELS.get(levelid) != null) {
			this.feats.get(levelid).add(time);
			return true;
		} return false;
	}
	public Long bestTime(Integer levelid) {
		/*Long[] completions = new Long[this.feats.get(levelid).size()+10];
		if (this.feats.get(levelid).size() == 0) return 999999L; 
		for (Long l : this.feats.get(levelid)) {
			completions[completions.length] = l;
		}
		Arrays.sort(completions);
		return completions[0];*/
		ArrayList<Long> comps = this.feats.get(levelid);
		Collections.sort(comps);
		return comps.get(0);
	}

	/**
	 * @param location the location to send the player to. Usually a level id (0 - infinity), or -1 for the lobby, or -3 for daily challenge world
	 * @return nothing lol
	 */
	public boolean sendPlayerToLocation(Integer location) {
		this.setFails(0);
		this.setPracMode(false);
		Main.times.remove(this.uuid);
		Player player = Bukkit.getPlayer(this.uuid);
		player.getInventory().clear();
		player.setGameMode(GameMode.SURVIVAL);
		if (location == -1) {
			this.setLocation(-1);
			player.teleport(Bukkit.getWorld("LOBBY").getSpawnLocation());
		}
		if (location >= 0) {
			if (Main.LEVELS.size() < location) {
				player.teleport(Bukkit.getWorld(Main.LEVELS.get(location).getLocation()).getSpawnLocation());
				Main.times.put(this.uuid, System.currentTimeMillis());
				this.setLocation(location);
				Main.setPlayerInventory(player);
				return true;
			} else {
				player.sendMessage("Tried to teleport you to a level which doesn't exist. Please report this! ID: " + location);
			}
		}
		if (location == -3) {
			if (this.getLives() > 0) {
				Location movloc = Bukkit.getWorld("DC_WORLD").getSpawnLocation();
				movloc.setPitch(0);
				movloc.setYaw(-90);
				Main.times.put(player.getUniqueId(), System.currentTimeMillis());
				player.teleport(movloc);
				Main.setPlayerInventory(player);
				this.setLocation(-3);
				if (this.getLives() < 100)
					player.sendMessage("§aDaily challenge started! You have §4" + this.getLives() + "§a lives.");
				else player.sendMessage("§aDaily challenge started! You have §4 infinite§a lives.");
			} else player.sendMessage("§cYou're out of lives! To get more, you can §b§lVOTE §cfor our server. §7(Click here)");
		}
		return true;
	}
	public boolean hasGold(Integer levelid) {
		if (this.feats.get(levelid).size() > 0) {
			return Main.LEVELS.get(levelid).getGoldTime() > bestTime(levelid);
		}
		return false;
	}
	public boolean isPracMode() {
		return pracMode;
	}
	public void setPracMode(boolean pracMode) {
		this.pracMode = pracMode;
	}
	public Integer getDailyStreak() {
		return dailyStreak;
	}
	public void setDailyStreak(Integer dailyStreak) {
		this.dailyStreak = dailyStreak;
	}
	public long getDailyTime() {
		return dailyTime;
	}
	public void setDailyTime(long dailyTime) {
		this.dailyTime = dailyTime;
	}
	public String getLastKnownName() {
		return lastKnownName;
	}
	public void setLastKnownName(String lastKnownName) {
		this.lastKnownName = lastKnownName;
	}



}


