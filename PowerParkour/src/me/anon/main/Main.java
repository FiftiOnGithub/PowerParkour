package me.anon.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;


public class Main extends JavaPlugin {
	
	PlayerDataLoader pdl;
	
	public FileConfiguration config;
	static public ArrayList<ParkourLevel> LEVELS = new ArrayList<ParkourLevel>();
	public static HashMap<UUID,ParkourPlayer> PLAYERS = new HashMap<UUID,ParkourPlayer>();
	public static HashMap<UUID,Long> times = new HashMap<UUID, Long>();
	
	public static String readableTimeUnits(Long millis) {
		long sec = Math.floorDiv(millis, 1000L);
		long dispsec = sec % 60;
		String dispsec2 = Long.toString(dispsec);
		if (dispsec2.length() < 2) {
			dispsec2 = "0" + dispsec2; 
		}
		long min = Math.floorDiv(sec, 60L);
		String min2 = Long.toString(min);
		if (min2.length() < 2) {
			min2 = "0" + min2;
		}
		return min2 + ":" + dispsec2 + ":" + millis%1000;
	}
	
	@Override
	public void onDisable() {
		pdl.saveData();
	}
	
	@Override 
	public void onEnable() {

		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(new EventsManager(), this);
		config = getConfig();
		saveDefaultConfig();
		saveConfig();
		pdl = new PlayerDataLoader(this);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
		   for (Entry<UUID,Long> i : times.entrySet()) {
			   if (Bukkit.getPlayer(i.getKey()) != null) {
				   ActionBarManager.ActionBar(ChatColor.GOLD + "Time: " + ChatColor.WHITE + readableTimeUnits(System.currentTimeMillis() - i.getValue()), Bukkit.getPlayer(i.getKey()));
			   }
		   }

		   if (ChunkManager.blockchanges.size() > 0) {
			   ChunkManager.performUpdates(20);
		   }
		   DailyChallenges.runUpdate(false);

		}, 1L , 5);


		
	}

	public static void setPlayerInventory(Player p) {
		// Creating player in game inventory content
		ItemStack nether_star = new ItemStack(Material.NETHER_STAR);
		ItemMeta nether_star_item_meta = nether_star.getItemMeta();
		nether_star_item_meta.setDisplayName("§e§lMore Options §7(Right Click)");
		nether_star.setItemMeta(nether_star_item_meta);
		p.getInventory().setItem(45,nether_star);

		ItemStack iron_door = new ItemStack(Material.IRON_DOOR);
		ItemMeta iron_door_item_meta = iron_door.getItemMeta();
		iron_door_item_meta.setDisplayName("§cReturn to Lobby §7(Right Click)");
		iron_door.setItemMeta(iron_door_item_meta);
		p.getInventory().setItem(44,iron_door);
	}

	public static HashMap<Integer,ArrayList<Long>> getEmptyCompMap() {
		HashMap<Integer,ArrayList<Long>> map = new HashMap<>();
		int ii = 0;
		for (ParkourLevel ignored : LEVELS) {
			map.put(ii, new ArrayList<>());
			ii++;
		}
		return map;
	}
	
	public static void advertisePlus(Player p) {
		p.sendMessage("""
				Interested in §6§lPLUS§f?
				§fPLUS is a §blifetime §fpurchase, which you make once and benefit from for as long as you play on our network. The price of PLUS is §b5.00$§f. For that price, you get:
				- §bPractice Mode
				§f- Special §f[§6§lP§r§f] tag in chat and the tab list
				§f- Join announcements
				§f- Extra daily challenge lives
				§f- 3 more full parkour levels""");
	}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {	
		if (cmd.getName().equalsIgnoreCase("leveldebug")) {
			if (!sender.isOp()) {
				sender.sendMessage("This is an admin-only command.");
				return true;
			}
			for (ParkourLevel i : LEVELS) {
				sender.sendMessage("Name: " + i.getName());
				sender.sendMessage("Location: " + i.getLocation());
				sender.sendMessage("Gold time: " + i.getGoldTime());
				sender.sendMessage("Condition: " + i.getCondition());
				sender.sendMessage("-------------------");
			}
		}
		if (cmd.getName().equalsIgnoreCase("start")) {
			if (!sender.isOp()) {
				sender.sendMessage("This is an admin-only command.");
				return true;
			}
			if (!times.containsKey(((Player) sender).getUniqueId())) {
				times.put(((Player) sender).getUniqueId(), System.currentTimeMillis());
				sender.sendMessage("Timing started!");
			} else sender.sendMessage("You're already being timed!");
		}
		
		if (cmd.getName().equalsIgnoreCase("parkourmenu")) {
			System.out.println("Opening parkour inventory");
			((Player) sender).openInventory(ParkourGUI.getInv(PLAYERS.get(((Player)sender).getUniqueId())));
		}
		
		if (cmd.getName().equalsIgnoreCase("practice")) {
			ParkourPlayer pp = Main.PLAYERS.get(((Player)sender).getUniqueId());
			if (!pp.hasPlus()) {
				sender.sendMessage("§cThis feature requires §6§lPLUS§c. To learn more, do §b/plus§c.");
				return true;
			}
			if (Main.times.get(pp.getUUID()) == null || pp.getLocation() == -3) {
				sender.sendMessage("§cYou must be in a parkour level to use this feature!");
				return true;
			}
			pp.setPracMode(!pp.isPracMode());
			if (pp.isPracMode()) sender.sendMessage("§aYou have entered §bPractice Mode!"); else {
				sender.sendMessage("§aYou have exited §bPractice Mode §a. To prevent abuse, you're being teleported back to the start of the level.");
				((Player)sender).teleport(((Player)sender).getWorld().getSpawnLocation());
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("plus")) {
			advertisePlus((Player) sender);
		}
		if (cmd.getName().equalsIgnoreCase("grantplus")) {
			if (!sender.isOp()) {
				sender.sendMessage("This is an admin-only command.");
				return true;
			}
			if (args.length == 1) {
				if (Bukkit.getOfflinePlayer(args[0]) != null) {
					Main.PLAYERS.get(Bukkit.getOfflinePlayer(args[0]).getUniqueId()).setPlus(true);
					sender.sendMessage("§aThis person now has PLUS.");
					
					if (Bukkit.getPlayer(args[0]) != null) {
						Bukkit.getPlayer(args[0]).sendMessage("""
								§aYou have become a §6§lPLUS §amember! Congratulations!\s
								§fTo receive the full benefits of the package, please re-log onto the server.

								§a§lThanks for your support!""");
					}
					
				} else sender.sendMessage("§cPlayer not found.");
			} else sender.sendMessage("§cUsage: /grantplus <ign>");
			
		}
		if (cmd.getName().equalsIgnoreCase("dailytop")) {
			String msg = "§2§lDaily Challenge fastest times: \n" + DailyChallenges.getTop();
			int pos = DailyChallenges.getPos(((Player)sender).getUniqueId());
			if (pos > 0) msg = msg + "§2Your position on today's leaderboard is: §e#" + pos;
			sender.sendMessage(msg);
		}
		if (cmd.getName().equalsIgnoreCase("forcedaily")) {
			if (!sender.isOp()) {
				sender.sendMessage("This is an admin-only command.");
				return true;
			}
			DailyChallenges.runUpdate(true);
			sender.sendMessage("Force updating daily challenge.");
		}
		if (cmd.getName().equalsIgnoreCase("myloc")) {
			sender.sendMessage("your integer location: " + Main.PLAYERS.get(((Player)sender).getUniqueId()).getLocation());
		}
		if (cmd.getName().equalsIgnoreCase("blockme")) {
			if (!sender.isOp()) {
				sender.sendMessage("This is an admin-only command.");
				return true;
			}
			((Player)sender).getLocation().getBlock().setType(Material.QUARTZ_BLOCK);
		}
		if (cmd.getName().equalsIgnoreCase("testchunkcopy")) {
			if (!sender.isOp()) {
				sender.sendMessage("This is an admin-only command.");
				return true;
			}
			sender.sendMessage("starting");
			ChunkManager.copyChunk(Bukkit.getWorld("DC_COPY"), 1, 1, Bukkit.getWorld("DC_WORLD"), 1, 1);
			sender.sendMessage("done");
		}
		if (cmd.getName().equalsIgnoreCase("voteinternal")) {
			if (!sender.isOp()) {
				sender.sendMessage("This is an admin-only command.");
				return true;
			}
			if (args.length == 1) {
				if (Bukkit.getOfflinePlayer(args[0]) != null) {
					if (Bukkit.getPlayer(args[0]).getUniqueId() != null) Bukkit.getPlayer(args[0]).sendMessage("§aThank you for your §b§lVOTE §afor the server. You now have infinite daily challenge lives!\n\nVoting for the server really helps us grow and become larger, so please keep doing it if you'd like to support us :)"); 
					Main.PLAYERS.get(Bukkit.getOfflinePlayer(args[0]).getUniqueId()).setLives(9999);
				} else sender.sendMessage("§cPlayer not found.");
			} else sender.sendMessage("§cUsage: /voteinternal <username>");
		}
		if (cmd.getName().equalsIgnoreCase("endparkour")) {
			if (!sender.isOp()) {
				sender.sendMessage("This is an admin-only command.");
				return true;
			}
			if (args.length == 1) {
				if (times.containsKey(((Player) sender).getUniqueId())) {
					long time = System.currentTimeMillis() - times.get(((Player) sender).getUniqueId());
					//sender.sendMessage("Timing ended! Your time was " + readableTimeUnits(time));
					
					if (Main.PLAYERS.get(((Player)sender).getUniqueId()).getLocation() == -3) {
						ParkourPlayer pp = Main.PLAYERS.get(((Player)sender).getUniqueId());
						sender.sendMessage("§aYou've completed today's parkour challenge! §fYour time was §6" + readableTimeUnits(time) + "§f. To check your streak, hover over the clock in the menu.");
						if (pp.getDailyTime() > 0) {
							if (pp.getDailyTime() > time) pp.setDailyTime(time);
						} else pp.setDailyTime(time);
						pp.setDailyStreak(pp.getDailyStreak() + 1);
						pp.sendPlayerToLocation(-1);
						return true;
					}
					
					sender.sendMessage("§a> §lParkour Challenge Complete!"
							+ "\n§a> §rYour time was §a§l"+readableTimeUnits(time)+"§r. To earn "
							+ "\n§a> §ra §6§lGold star§r, your time needs to be"
							+ "\n§a> §rfaster than §7" + readableTimeUnits(LEVELS.get(Integer.parseInt(args[0])).getGoldTime()) + "§r.");
					PLAYERS.get(((Player)sender).getUniqueId()).addFeat(time, Integer.parseInt(args[0]));
					times.remove(((Player) sender).getUniqueId());
				} else sender.sendMessage("You're not being timed!");
			} else sender.sendMessage("§cusage: /endparkour <id>");
			
		}
		
		return true;

}
		
		
		
}