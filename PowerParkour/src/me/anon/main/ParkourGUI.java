package me.anon.main;

import java.util.ArrayList;

import me.anon.util.ParkourLevel;
import me.anon.util.ParkourPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ParkourGUI {
	
	public static Inventory getInv(ParkourPlayer p) {
		Inventory base = Bukkit.createInventory(null, 54,"§aParkour Selector");
		int ii = 0;
		for (ParkourLevel i : Main.LEVELS) {
			{
				String displayName = "§5§lBUG HAPPENED!";
				ArrayList<String> lore = new ArrayList<>();
				Material mat = Material.COMMAND;
				if (p.getFeats().get(ii).size() > 0) {
					if (p.hasGold(ii)) {
						displayName = "§6§l✭ " + i.getName()+ " ✭";
						mat = Material.GOLD_BLOCK;
					} else {
						displayName = "§a§l" + i.getName();
						mat = Material.IRON_BLOCK;
					}
					lore.add("§7Your best time: " + Main.readableTimeUnits(p.bestTime(ii)));
					lore.add("§6Gold star §7time: " + Main.readableTimeUnits(i.getGoldTime()));
					lore.add("§7Times you've completed: " + p.getFeats().get(ii).size());
				} else {
					int cpl = p.canPlayLevel(ii);
					switch (cpl) {
						case 0: {
							mat = Material.EMERALD;
							displayName = "§a§l" + i.getName();
							lore.add("§aYou can play this level right now!");
							lore.add("§aClick here to enter!");
							break;
						}
						case 1: {
							mat = Material.WOOD_BUTTON;
							displayName = "§e§l" + i.getName();
							lore.add("§cYou haven't yet completed");
							lore.add("§cthe level before this!");
							break;
						}
						case 2: {
							mat = Material.STONE_PLATE;
							displayName = "§6§l" + i.getName();
							lore.add("§cTo play this level,");
							lore.add("§cyou need to get a §6§lGold star");
							lore.add("§cin every other level (except plus ones)");
							break;
						}
						case 3: {
							mat = Material.STONE_BUTTON;
							displayName = "§b§l" + i.getName();
							lore.add("§cThis level is for §6PLUS §crank only.");
							lore.add("§cTo learn more about §6PLUS§c, do /plus");
							break;
						}
						case 4: {
							displayName = "§4§l" + i.getName();
							lore.add("§4A bug has happened. Contact admins about this with the level name and your username.");
							break;
						}
					}
				}
			 	// Entry is ii
				ItemStack item = UtilFunctions.itemFactory(String.valueOf(ii),displayName,mat,lore,false);
				base.setItem(i.getChestPos(), item);
			}
			ii++;
		}

		ArrayList<String> lore = new ArrayList<>();
		lore.add("§7Returns you to the PowerParkour Hub.");
		base.setItem(40,UtilFunctions.itemFactory("-1","§c§lReturn to Hub",Material.FEATHER,lore,false));

		ArrayList<String> plore = new ArrayList<>();
		plore.add("§7Our server offers the ability for you to purchase §6§lPLUS");
		plore.add("§7membership using real life money. Some of the features are:");
		plore.add("§7- Access to §bPractice Mode");
		plore.add("§7- Increased daily challenge lives");
		plore.add("§7- Special §f[§6§lP§r§f]§7 tag in chat and tab");
		plore.add("§7... And much more! §aClick here to learn more!");
		base.setItem(49,UtilFunctions.itemFactory("-2","§a§lLearn about §6§lPLUS",Material.GOLD_INGOT,plore,false));

		ArrayList<String> dlore = new ArrayList<>();

		dlore.add("§7The daily challenge is a parkour level that");
		double htnc = Math.floor(DailyChallenges.timeTillNextChallenge() / 3600000f);
		String tts = "§a" + htnc + "h";
		if (htnc < 1.0D) tts =  "§aless than 1 hour";
		dlore.add("§7changes every day. The next change will happen in " + tts + "§7.");
		dlore.add("§7Try to get a high daily streak!");
		if (p.getDailyStreak() != 0) {
			if (p.getDailyTime() != 0) {
				dlore.add("§7Your daily streak is " + (p.getDailyStreak()));
			} else {
				dlore.add("§7Your daily streak is " + p.getDailyStreak());
				dlore.add("§cYou haven't completed today's daily challenge yet.");
				dlore.add("§cYou'll lose your streak if you don't play the level!");
			}

		}
		if (p.getDailyTime() != 0) dlore.add("§7Your best time today: §2" + Main.readableTimeUnits(p.getDailyTime()));
		base.setItem(4,UtilFunctions.itemFactory("-3","§b§lDaily Challenge",Material.WATCH,dlore,false));
		return base;
	}
	
	
}
