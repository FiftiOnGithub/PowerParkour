package me.anon.main;


import me.anon.util.ParkourPlayer;
import me.anon.util.ShopCosmetic;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class EventsManager implements Listener {


	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (Main.PLAYERS.containsKey(e.getPlayer().getUniqueId())) {
			e.getPlayer().sendMessage("§b ");
			e.getPlayer().sendMessage("§6§lWelcome back!");
			e.getPlayer().sendMessage("§rWelcome back to ParkourPower.");
			e.getPlayer().sendMessage("§b ");
			ParkourPlayer p = Main.PLAYERS.get(e.getPlayer().getUniqueId());
			if (!Objects.equals(p.getLastKnownName(), e.getPlayer().getName())) p.setLastKnownName(e.getPlayer().getName());
			if (p.getDailyTime() == 0) {
				e.getPlayer().sendMessage("§aYou haven't completed today's daily challenge yet! To play the daily challenge, click the clock in the parkour menu!");
			}
			if (p.hasPlus()) e.getPlayer().setPlayerListName("§f[§6§lP§f] " + e.getPlayer().getName());
			if (p.getSelectedItems().get("JM") != null) {
				switch(p.getSelectedItems().get("JM")) {
					case JM_BASIC:
						if (p.hasPlus()) {
							e.setJoinMessage("§6§l" + e.getPlayer().getName() + "§7 has joined the game.");
						} else {
							e.setJoinMessage("§7" + e.getPlayer().getName() + "§7 has joined the game.");
						}
						break;
					case JM_RAINBOW:
						e.setJoinMessage(Main.rainbow(e.getPlayer().getName() + " has joined the game!"));
						break;
				}
			}

		} else {
			e.getPlayer().sendMessage("§b>");
			e.getPlayer().sendMessage("§6§lWelcome to ParkourPower!");
			e.getPlayer().sendMessage("§rParkourPower is a new minecraft server made specifically for parkour. \nIf you want to jump straight in, then go click the §c§lCaptain §rin front of you.\n\n§b> §rIf you want to learn about our rules, do §e/rules§r. If you want to learn about the server, do §e/info§r.");
			e.getPlayer().sendMessage("§b>");
			new ParkourPlayer(e.getPlayer().getUniqueId(),Main.getEmptyCompMap(),false,3,0,0,e.getPlayer().getName(),new ArrayList<ShopCosmetic>(),0,new HashMap<String,ShopCosmetic>());
		}
	}
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		System.out.println("inventory click");
		InventoryClickManager.inventoryClickEvent(e);

	}
	@EventHandler
	public void FoodChangeEvent(FoodLevelChangeEvent e) {
		e.setCancelled(true);
	}
	@EventHandler
	public void BlockPlaceEvent(BlockPlaceEvent e) {
		if (!e.getPlayer().isOp()) e.setCancelled(true);
	}
	@EventHandler
	public void BlockBreakEvent(org.bukkit.event.block.BlockBreakEvent e) {
		if (!e.getPlayer().isOp()) e.setCancelled(true);
	}
	
	@EventHandler
	public void EntityDamageEvent(org.bukkit.event.entity.EntityDamageEvent e) {
		e.setCancelled(true);
	}



	@EventHandler
	public void PlayerInteractEvent(PlayerInteractEvent e) {
		System.out.println("PlayerInteractEvent!");
		ParkourPlayer player = Main.PLAYERS.get(e.getPlayer().getUniqueId());
		if (player.getLocation() >= 0 || player.getLocation() == -3) {
			// Return to lobby
			if (e.getItem() != null) {
				System.out.println(e.getItem().getType().toString());
				if (e.getItem().getType() == Material.IRON_DOOR) {
					player.sendPlayerToLocation(-1);
					e.getPlayer().sendMessage("§aReturning you to lobby.");
				}
				// More options
				if (e.getItem().getType() == Material.NETHER_STAR) {
					//TODO: Add more options inventory
				/*
				The more options inventory will contain additional choices, such as:
				- Hide players in world (this might be a PLUS feature?) Probably not
				- Enter practice mode
				- cosmetic selections?
				*/
					e.getPlayer().sendMessage("§eHere is the part where the more options inventory will open.");
				}
			}

		}
	}
	@EventHandler 
	public void AsyncPlayerChatEvent(org.bukkit.event.player.AsyncPlayerChatEvent e) {
		e.setCancelled(true);
		String tag = "";
		ParkourPlayer p = Main.PLAYERS.get(e.getPlayer().getUniqueId());
		if (p.getSelectedItems().get("PF") != null) {
			switch (p.getSelectedItems().get("PF")) {
				case PF_PLUS:
					tag = "§r[§6§lP§r] ";
					break;
				case PF_STAFF:
					tag = "§r[§a§lSTAFF§r] ";
					break;
				case PF_PRO:
					tag = "[§cPRO§f] ";
					break;
				case PF_CHRISTMAS_TREE:
					tag = "[§2❄§f]";
					break;
				case PF_BIGSTAR:
					tag = "[§5✯§f] ";
					break;
				case PF_SMALLSTAR:
					tag = "[§d✩§f]";
					break;
				case PF_NONE:
					tag = "";
					break;
			}
		}

		String msg = e.getMessage();
		if (!p.hasPlus()) msg = "§7" + msg;
		String name = e.getPlayer().getName();
		Bukkit.broadcastMessage(tag + name + ": " + msg);
	}
	
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if (e.getTo().getY() < 30) {
			
			ParkourPlayer pp = Main.PLAYERS.get(e.getPlayer().getUniqueId());
			
			if (pp.getLocation() == -3) {
				if (!(pp.getLives() > 100)) pp.setLives(pp.getLives() - 1);
				if (pp.getLives() == 0) {
					pp.sendPlayerToLocation(-1);
					
					e.getPlayer().sendMessage("§bYou ran out of lives!"
							+ "\n§fYou've run out of lives. The daily challenge has a limited number of lives. If you want to try the challenge as many times as you want, you can always §b§lVOTE§f for our server. It's completely free and helps our server a lot!");
				} else {
					if (pp.getLives() > 100) e.getPlayer().sendMessage("§cYou died! §fSince you've voted, you didn't lose any lives.."); else e.getPlayer().sendMessage("§cYou died! §fYou lost a life. You now have §4" + pp.getLives() + "§f lives remaining.");
					
					Main.times.remove(e.getPlayer().getUniqueId());
					Main.times.put(e.getPlayer().getUniqueId(),System.currentTimeMillis());
					Location movloc = e.getPlayer().getWorld().getSpawnLocation();
					movloc.setPitch(0);
					movloc.setYaw(-90);
					e.getPlayer().teleport(movloc);
				}
				return;
			}
			
			
			if (pp.getLastGround() != null) {
				e.getPlayer().teleport(pp.getLastGround());
				return;
			}
			e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation());
			if (pp.getLocation() >= 0) {
			Main.times.remove(e.getPlayer().getUniqueId());
			Main.times.put(e.getPlayer().getUniqueId(),System.currentTimeMillis());
			pp.setFails(pp.getFails() + 1);
			if (pp.getFails() % 20 == 0 && !pp.hasPlus()) {
				e.getPlayer().sendMessage("§a> §3§lHaving trouble?" +
						"\n§a> §6§lPLUS §rusers are able to use §bPractice Mode §rwhich" +
						"\n§a> §rallows them to respawn right on the jump they fell." +
						"\n§a> §bPractice Mode §rcompletions do not count as completions though." +
						"\n§a> §rTo learn more about §6§lPLUS§r do §b/plus§r.");
			}
			return;
			}
			//FlightStopper.stopflight(e);
			
		}
		if (!(Math.floor(e.getFrom().getY()) == e.getFrom().getY())) {
			if ((Math.floor(e.getTo().getY()) == e.getTo().getY())) {
				ParkourPlayer pp = Main.PLAYERS.get(e.getPlayer().getUniqueId());
				if (pp.getLocation() == -1) return;
				if (pp.isPracMode()) {
					Location location = new Location(e.getPlayer().getWorld(),e.getFrom().getX(),e.getFrom().getY(),e.getFrom().getZ());
					location.setYaw(e.getPlayer().getLocation().getYaw());
					pp.setLastGround(location);
				} else {
					int x = e.getTo().getBlockX();
					int y = e.getTo().getBlockY() - 1;
					int z = e.getTo().getBlockZ();
					Material blockBelowMaterial = e.getPlayer().getWorld().getBlockAt(x,y,z).getType();
					System.out.println(blockBelowMaterial.toString());
					if (blockBelowMaterial == Material.GOLD_BLOCK) {
						if (pp.getLastGround() == null || pp.getLastGround().distance(e.getPlayer().getLocation()) > 10) {
							Location loc = e.getPlayer().getLocation();
							loc.setPitch(0L);
							pp.setLastGround(loc);
							e.getPlayer().sendMessage(" \n§eYou have reached a §lCheckpoint§r§e!\n " +
									"§eIf you fall now, you will be returned to this point. \n ");
						}
					} else if (blockBelowMaterial == Material.EMERALD_BLOCK) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "endparkour " + e.getPlayer().getName());
					}
				}
			}
		}
	}
	
}
