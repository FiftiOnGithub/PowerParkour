package me.anon.main;

import java.util.*;
import java.util.Map.Entry;

import me.anon.dataManager.PlayerDataLoader;
import me.anon.util.ParkourLevel;
import me.anon.util.ParkourPlayer;
import me.anon.util.ShopCosmetic;
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
	public static ArrayList<ParkourLevel> LEVELS = new ArrayList<ParkourLevel>();
	public static HashMap<UUID, ParkourPlayer> PLAYERS = new HashMap<UUID,ParkourPlayer>();
	public static HashMap<UUID,Long> times = new HashMap<UUID, Long>();
	public static HashMap<String, ShopCosmetic[]> buyable_items;
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

		buyable_items.put("CM",new ShopCosmetic[]{ShopCosmetic.CM_BASIC, ShopCosmetic.CM_FLEX, ShopCosmetic.CM_NONE});

		buyable_items.put("JM",new ShopCosmetic[]{ShopCosmetic.JM_BASIC, ShopCosmetic.JM_RAINBOW, ShopCosmetic.JM_NONE});

		buyable_items.put("PF",new ShopCosmetic[]{ShopCosmetic.PF_BIGSTAR, ShopCosmetic.PF_CHRISTMAS_TREE, ShopCosmetic.PF_PLUS,
									ShopCosmetic.PF_STAFF, ShopCosmetic.PF_SMALLSTAR, ShopCosmetic.PF_PRO, ShopCosmetic.PF_NONE});

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

	public static String rainbow(String message) {
		String[] rainbow = {"4","c","6","e","6","c"};
		int pos = 0;
		ArrayList<String> messageSplit = new ArrayList<String>(Arrays.asList(message.split("")));
		String newmessage = "";
		for (String i : messageSplit) {
			newmessage+=("§"+rainbow[pos]+i);
			pos++;
			if (rainbow.length == pos) pos = 0;
		}
		return newmessage;
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
		if (cmd.getName().equalsIgnoreCase("openstoreinventory")) {
			if (args.length == 1) {
				if (sender.isOp()) {
					Player p = Bukkit.getPlayer(args[0]);
					if (p != null) {
						ShopGUI.getInventory(Main.PLAYERS.get(p.getUniqueId()),p.isOp());
					} else sender.sendMessage("Player not found.");
				} else sender.sendMessage("This is an admin-only command.");
			}
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

		if (cmd.getName().equalsIgnoreCase("storeinternal")) {
			if (!sender.isOp()) {
				sender.sendMessage("this is an admin-only command.");
				return true;
			}
			// Argument 1: Player making purchase
			// Argument 2: Item being bought
			// Argument 3: Price
			if (args.length == 3) {
				Player buyer = Bukkit.getPlayer(args[0]);
				if (buyer == null) {
					sender.sendMessage("§cPlayer not found.");
					return true;
				}
				ParkourPlayer buyer_wrapped = PLAYERS.get(buyer.getUniqueId());
				ShopCosmetic ItemToBuy;
				try {
					ItemToBuy = ShopCosmetic.valueOf(args[1]);
				} catch(Error e) {
					buyer.sendMessage("§cItem not found. Please report this!");
					sender.sendMessage("Tried to buy invalid item: " + args[1]);
					return true;
				}
				int price;
				try{
					price = Integer.parseInt(args[2]);
				}
				catch (NumberFormatException ex){
					buyer.sendMessage("§cCouldn't buy this item, price is set incorrectly. Report this as a bug!");
					sender.sendMessage("Couldn't sell item, invalid price.");
					return true;
				}

				if (buyer_wrapped.getCoinBalance() >= price) {
					ArrayList<ShopCosmetic> itemsOwned = buyer_wrapped.getOwnedCosmetics();
					if (!itemsOwned.contains(ItemToBuy)) {
						buyer_wrapped.setCoinBalance(buyer_wrapped.getCoinBalance() - price);
						itemsOwned.add(ItemToBuy);
						System.out.println(buyer_wrapped.getOwnedCosmetics().toString());
						buyer_wrapped.setOwnedCosmetics(itemsOwned);
						buyer.sendMessage("§ePurchase successful!");
					} else {
						buyer.sendMessage("§cYou already own this item!");
					}
				} else {
					buyer.sendMessage("§cYou can't afford to purchase this item!");
					return true;
				}
			}
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

		if (cmd.getName().equalsIgnoreCase("selectorinternal")) {
			if (args.length == 3) {
				/*
				Argument 1: Player
				Argument 2: Option
				Argument 3: Category
				 */

				Player target = Bukkit.getPlayer(args[0]);
				if (target == null) {
					sender.sendMessage("§cPlayer not found.");
					return true;
				}
				ParkourPlayer targetWrapped = PLAYERS.get(target.getUniqueId());

				ShopCosmetic ItemToBuy;
				try {
					ItemToBuy = ShopCosmetic.valueOf(args[1]);
				} catch(Error e) {
					target.sendMessage("§cItem not found. Please report this!");
					sender.sendMessage("Tried to buy invalid item: " + args[1]);
					return true;
				}

				boolean result = setItemAsSelected(ItemToBuy,targetWrapped,args[2]);
				if (result) {
					target.sendMessage("§7Selected successfully!");
				} else {
					target.sendMessage("§cUnable to select item! You don't own it!");
				}
				return true;

			}
		}

		if (cmd.getName().equalsIgnoreCase("endparkour")) {
			if (args.length != 1) {
				sender.sendMessage("§cIncorrect arguments. /endparkour <player>");
				return true;
			}
			Player player = Bukkit.getPlayer(args[0]);
			ParkourPlayer pp = Main.PLAYERS.get(player.getUniqueId());


			if (!sender.isOp()) {
				sender.sendMessage("This is an admin-only command.");
				return true;
			}

			if (times.containsKey(player.getUniqueId())) {
				long time = System.currentTimeMillis() - times.get(player.getUniqueId());

				if (pp.getLocation() == -3) {
					player.sendMessage("§aYou've completed today's parkour challenge! §fYour time was §6" + readableTimeUnits(time) + "§f. To check your streak, hover over the clock in the menu.");
					if (pp.getDailyTime() > 0) {
						if (pp.getDailyTime() > time) pp.setDailyTime(time);
					} else {
						pp.setDailyTime(time);
						int coinsToAdd = 40 + (10 * pp.getDailyStreak());
						player.sendMessage("§6You earned " + coinsToAdd + " coins (Daily Challenge)!");
						pp.setCoinBalance(pp.getCoinBalance() + coinsToAdd);
					}

					pp.setDailyStreak(pp.getDailyStreak() + 1);
					return true;
				}
				int coinsToAdd;
				String desc = "";
				// Calculate coins earned
				if (pp.bestTime(pp.getLocation()) > 0L) {
					coinsToAdd = 100 + (10 * pp.getLocation());
					desc+= "First Completion";
				} else {
					if (time < LEVELS.get(pp.getLocation()).getGoldTime()) {
						// gold star time
						coinsToAdd = 15 + (3 * pp.getLocation());
						desc+= "Gold Star Time";
					} else {
						coinsToAdd = 5 + (2 * pp.getLocation());
						desc+= "Level Completion";
					}
				}
				pp.setCoinBalance(pp.getCoinBalance() + coinsToAdd);



				player.sendMessage("§a> §lParkour Challenge Complete!"
						+ "\n§a> §rYour time was §a§l"+readableTimeUnits(time)+"§r. To earn "
						+ "\n§a> §ra §6§lGold star§r, your time needs to be"
						+ "\n§a> §rfaster than §7" + readableTimeUnits(LEVELS.get(pp.getLocation()).getGoldTime()) + "§r.");
				pp.addFeat(time, pp.getLocation());
				times.remove(player.getUniqueId());
				player.sendMessage("§6You earned " + coinsToAdd + " coins (" + desc + ")!");
			} else player.sendMessage("You're not being timed!");
			pp.sendPlayerToLocation(-1);
		}
		return true;

}

public boolean setItemAsSelected(ShopCosmetic item, ParkourPlayer target,String category) {
		// Check if item is within given category (and if the category exists)
	if (buyable_items.containsKey(category)) {
		if (Arrays.asList(buyable_items.get(category)).contains(item)) {
			if (target.getOwnedCosmetics().contains(item)) {
				HashMap<String, ShopCosmetic> selections = target.getSelectedItems();
				selections.put(category,item);
				return true;
			}
		}
	}
	return false;
}

		
}