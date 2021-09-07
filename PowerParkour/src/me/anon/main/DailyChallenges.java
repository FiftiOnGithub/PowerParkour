package me.anon.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class DailyChallenges {
	
	public static long lastdaily;
	public static int CD1;
	public static int CD2;
	public static int CD3;
	public static int CD4;
	public static int CD5;
	public static boolean ready = false;
	public static int mpt; 
	
	public static void runUpdate(boolean force) {
		if (ready) {
			int rmpt = mpt - 1;
			if (timeTillNextChallenge() < 0 || force) {
				lastdaily = System.currentTimeMillis();
				// Generate new CDs (Chunk Daily)
				Random r = new Random();
				CD1 = r.nextInt(rmpt+1);
				CD2 = r.nextInt(rmpt+1);
				CD3 = r.nextInt(rmpt+1);
				CD4 = r.nextInt(rmpt+1);
				CD5 = r.nextInt(rmpt+1);
				System.out.println("Generated new daily challenge: " + CD1 + ","+ CD2 + ","+ CD3 + ","+ CD4 + ","+ CD5 + ". RMPT: " + rmpt);
				Location targetLocation = Bukkit.getWorld("LOBBY").getSpawnLocation();
				for (Player p : Bukkit.getWorld("DC_WORLD").getPlayers()) {
					p.sendMessage("§cThe time on the Daily Challenge has ran out. You will be moved to the lobby while the next daily challenge is prepared.");
					ParkourPlayer pp = Main.PLAYERS.get(p.getUniqueId());
					pp.sendPlayerToLocation(-1);
				}
				World from = Bukkit.getWorld("DC_COPY");
				World to = Bukkit.getWorld("DC_WORLD");
				ChunkManager.copyChunk(from, CD1, 0, to, 1, 0);
				ChunkManager.copyChunk(from, CD2, 1, to, 2, 0);
				ChunkManager.copyChunk(from, CD3, 2, to, 3, 0);
				ChunkManager.copyChunk(from, CD4, 3, to, 4, 0);
				ChunkManager.copyChunk(from, CD5, 4, to, 5, 0);
				
				
				// Reset the daily challenge data for everyone:
				for (Entry<UUID, ParkourPlayer> p : Main.PLAYERS.entrySet()) {
					ParkourPlayer pp = p.getValue();
					
					if (pp.getDailyTime() == 0) {
						pp.setDailyStreak(0);
					} else {
						pp.setDailyTime(0);
					}
					pp.setLives(3);
					
				}
				Bukkit.broadcastMessage("§a§lA new daily challenge has arrived! To start it, click the clock item in the parkour selector!");
				
			}
		}
	}
	
	public static long timeTillNextChallenge() {
		return lastdaily - (System.currentTimeMillis() - 86400000);
	}
	
	public static String getTop() {
		String result = "";
		Map<String,Long> unsorted = new HashMap<>();
		for (Entry<UUID,ParkourPlayer> i : Main.PLAYERS.entrySet()) {
			if (i.getValue().getDailyTime() != 0) {
				unsorted.put(i.getValue().getLastKnownName(), i.getValue().getDailyTime());
			}
			
		}
		Map<String, Long> sorted = sortByValue(unsorted);
		int ii = 0;
		for (Entry<String,Long> ent : sorted.entrySet()) {
			ii++;
			result = result + "§2" + (ii) + ") §e" + ent.getKey() + "§2 - " + Main.readableTimeUnits(ent.getValue()) + "\n";
			if (ii == 10) break;
		}
		return result;
	}
	public static int getPos(UUID u) {
		if (Main.PLAYERS.get(u).getDailyTime() < 1) return 0;
		Map<UUID,Long> unsorted = new HashMap<>();
		for (Entry<UUID,ParkourPlayer> i : Main.PLAYERS.entrySet()) {
			if (i.getValue().getDailyTime() != 0) {
				unsorted.put(i.getKey(), i.getValue().getDailyTime());
			}
			
		}
		Map<UUID, Long> sorted = sortByValue(unsorted);
		int ii = 0;
		for (Entry<UUID,Long> ent : sorted.entrySet()) {
			ii++;
			if (ent.getKey().equals(u)) return ii;
		}
		return 0;
	}
	// copied straight off google
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
	        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
	        list.sort(Entry.comparingByValue());

	        Map<K, V> result = new LinkedHashMap<>();
	        for (Entry<K, V> entry : list) {
	            result.put(entry.getKey(), entry.getValue());
	        }

	        return result;
	}	
	
}
