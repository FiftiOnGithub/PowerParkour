package me.anon.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;

public class PlayerDataLoader {
	// this file is used to manage and load all data about the levels and players. Level information is stored natively in config.yml, while playerdata is stored externally.
	Main main;
	PlayerDataManager pdm;
	// loading level information
	public PlayerDataLoader(Main m) {
		main = m;
		System.out.println("Starting data load. Levels first");
		ConfigurationSection levels = m.config.getConfigurationSection("levels");
		for (String levelid : levels.getKeys(false)) {
			System.out.println("Loading level " + levelid);
			String name = m.config.getString("levels."+levelid+".name");
			Integer condition = m.config.getInt("levels."+levelid+".condition");
			Integer chestPos = m.config.getInt("levels."+levelid+".chestPos");
			String location = m.config.getString("levels."+levelid+".location");
			long goldTime = m.config.getLong("levels."+levelid+".goldTime");
			new ParkourLevel(name,condition,chestPos,location,goldTime);
			System.out.println("Loaded level " + name + " with condition " + condition);
		}
		// player information
		pdm = new PlayerDataManager(m,"players.yml");
		pdm.saveDefaultConfig();
		if (pdm.getConfig().getConfigurationSection("players") != null) {
			for (String i : pdm.getConfig().getConfigurationSection("players").getKeys(false)) {
				
				UUID uuid = UUID.fromString(i);
				
				HashMap<Integer,ArrayList<Long>> feats = new HashMap<>();
				for (String ii : pdm.getConfig().getConfigurationSection("players."+i+".feats").getKeys(false)) {
					List<Integer> lint = pdm.getConfig().getIntegerList("players."+i+".feats."+ii);
					ArrayList<Long> comps = new ArrayList<>();
					for (Integer inti : lint) {
						comps.add(Long.valueOf(inti));
					}
					feats.put(Integer.parseInt(ii), comps);
				}
				
				boolean plus = pdm.getConfig().getBoolean("players."+i+".hasPlus");
				
				Integer dailyLives = pdm.getConfig().getInt("players."+i+".dailyLives");
				
				Integer dailyStreak = pdm.getConfig().getInt("players."+i+".dailyStreak");
				
				long dailyTime = pdm.getConfig().getLong("players."+i+".dailyTime");
				String lnn = pdm.getConfig().getString("players."+i+".username");
				new ParkourPlayer(uuid,feats,plus,dailyLives,dailyStreak,dailyTime,lnn);
				System.out.println("Loaded " + uuid + ". They have " + feats.size() + " feats.");
				
			}
		}
		
		// daily challenge information
		// daily challenge information
		ConfigurationSection daily = m.config.getConfigurationSection("daily");
		DailyChallenges.lastdaily = daily.getLong("lastDaily");
		DailyChallenges.CD1 = daily.getInt("currentDaily.one");
		DailyChallenges.CD2 = daily.getInt("currentDaily.two");
		DailyChallenges.CD3 = daily.getInt("currentDaily.three");
		DailyChallenges.CD4 = daily.getInt("currentDaily.four");
		DailyChallenges.CD5 = daily.getInt("currentDaily.five");
		DailyChallenges.mpt = daily.getInt("mapsPerType");
		DailyChallenges.ready = true;
		System.out.println("Loaded daily info");
		
	}
	public void saveData() {
		System.out.println("Starting data save");
		for (Entry<UUID, ParkourPlayer> ent : Main.PLAYERS.entrySet()) {
			pdm.getConfig().set("players."+ent.getKey().toString()+".hasPlus", ent.getValue().hasPlus());
			pdm.getConfig().set("players."+ent.getKey().toString()+".dailyLives", ent.getValue().getLives());
			for (Entry<Integer,ArrayList<Long>> comp : ent.getValue().getFeats().entrySet()) {
				List<Integer> lint = new ArrayList<>();

				for (Long ic : comp.getValue()) {
					lint.add(ic.intValue());
				}
				pdm.getConfig().set("players."+ent.getKey().toString()+".feats."+comp.getKey().toString(), lint);
			}
			
			pdm.getConfig().set("players."+ent.getKey().toString()+".dailyStreak", ent.getValue().getDailyStreak());
			pdm.getConfig().set("players."+ent.getKey().toString()+".dailyTime", ent.getValue().getDailyTime());
			pdm.getConfig().set("players."+ent.getKey().toString()+".username", ent.getValue().getLastKnownName());
			System.out.println("Saved player " + ent.getValue().getLastKnownName());
		}
		System.out.println("Completed player data save.");
		pdm.saveConfig();
		
		
		/*daily:
   lastDaily: 0
   currentDaily:
      one: 1
      two: 1
      three: 1
      four: 1
      five: 1
   mapsPerType: 1		
		 */
		
		main.config.set("daily.lastDaily", DailyChallenges.lastdaily);
		main.config.set("daily.currentDaily.one", DailyChallenges.CD1);
		main.config.set("daily.currentDaily.two", DailyChallenges.CD2);
		main.config.set("daily.currentDaily.three", DailyChallenges.CD3);
		main.config.set("daily.currentDaily.four", DailyChallenges.CD4);
		main.config.set("daily.currentDaily.five", DailyChallenges.CD5);
		main.config.set("daily.currentDaily.mapsPerType", DailyChallenges.mpt);
		System.out.println("Saved daily data");
		main.saveConfig();
	}
	
	
}
