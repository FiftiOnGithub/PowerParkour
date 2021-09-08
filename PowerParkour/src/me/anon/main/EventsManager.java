package me.anon.main;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Objects;

public class EventsManager implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (Main.PLAYERS.containsKey(e.getPlayer().getUniqueId())) {
			e.getPlayer().sendMessage("§b>");
			e.getPlayer().sendMessage("§6§lWelcome back!");
			e.getPlayer().sendMessage("§rWelcome back to ParkourPower. Hopefully you have a good time :D");
			e.getPlayer().sendMessage("§b>");
			ParkourPlayer p = Main.PLAYERS.get(e.getPlayer().getUniqueId());
			if (!Objects.equals(p.getLastKnownName(), e.getPlayer().getName())) p.setLastKnownName(e.getPlayer().getName());
			if (p.getDailyTime() == 0) {
				e.getPlayer().sendMessage("§aYou haven't completed today's daily challenge yet! To play the daily challenge, click the clock in the parkour menu!");
			}
			if (p.hasPlus()) e.getPlayer().setPlayerListName("§f[§6§lP§f] " + e.getPlayer().getName());
			if (p.hasPlus()) e.setJoinMessage("§6§l" + e.getPlayer().getName() + " §7has joined the game!"); else e.setJoinMessage(null);

		} else {
			e.getPlayer().sendMessage("§b>");
			e.getPlayer().sendMessage("§6§lWelcome to ParkourPower!");
			e.getPlayer().sendMessage("§rParkourPower is a new minecraft server made specifically for parkour. \nIf you want to jump straight in, then go click the §c§lCaptain §rin front of you.\n\n§b> §rIf you want to learn about our rules, do §e/rules§r. If you want to learn about the server, do §e/info§r.");
			e.getPlayer().sendMessage("§b>");
			new ParkourPlayer(e.getPlayer().getUniqueId(),Main.getEmptyCompMap(),false,3,0,0,e.getPlayer().getName());
		}
	}
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getInventory().getName().equals("§aParkour Selector")) {
			e.setCancelled(true);
			if (!e.getCurrentItem().hasItemMeta()) return;
			if (e.getCurrentItem().getItemMeta().hasLore()) {
				int levelid = Integer.parseInt(e.getCurrentItem().getItemMeta().getLore().get(e.getCurrentItem().getItemMeta().getLore().size()-1).substring(2));
				Player p = (Player) e.getWhoClicked();
				ParkourPlayer pp = Main.PLAYERS.get(p.getUniqueId());
				//((Player) e.getWhoClicked()).sendMessage(levelid + " - " + Main.LEVELS.get(levelid).getName());
				if (levelid < 0) {
					if (levelid == -1) {
						// return to lobby
						p.sendMessage("§aYou have been returned to the lobby.");
						pp.sendPlayerToLocation(-1);
						return;
					}
					if (levelid == -2) {
						e.getWhoClicked().closeInventory();
						Main.advertisePlus((Player)e.getWhoClicked());
						return;
					}
					if (levelid == -3) {
						pp.sendPlayerToLocation(-3);
						return;
						
					}
					return;
				}
				if (pp.canPlayLevel(levelid) == 0) {
				pp.sendPlayerToLocation(levelid);
				p.sendMessage("§aYou have started " + Main.LEVELS.get(levelid).getName() + "! Good luck :)");
				} else p.sendMessage("§cYou can't play this level!");
			}
		}
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
	public void PlayerInventoryMoveEvent(InventoryEvent e) {
		System.out.println("Inventory Event! More specifically: " + e.getEventName() + ". Inventory Name: " + e.getInventory().getName() + ". Inventory 'title': " + e.getInventory().getTitle() + ". Inventory owner: " + e.getInventory().getViewers().get(0).getName() + ".");
	}

	@EventHandler
	public void PlayerInteractEvent(PlayerInteractEvent e) {
		System.out.println("PlayerInteractEvent!");
		ParkourPlayer player = Main.PLAYERS.get(e.getPlayer().getUniqueId());
		if (player.getLocation() >= 0) {
			// Return to lobby
			if (e.getItem().getType() == Material.IRON_DOOR_BLOCK) {
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
	@EventHandler 
	public void AsyncPlayerChatEvent(org.bukkit.event.player.AsyncPlayerChatEvent e) {
		e.setCancelled(true);
		String tag = "";
		ParkourPlayer p = Main.PLAYERS.get(e.getPlayer().getUniqueId());
		if (p.hasPlus()) tag = "§f[§6§lP§f] ";
		if (e.getPlayer().isOp()) tag = "§f[§4§lSTAFF§f] ";
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
			
			
			if (pp.isPracMode() && pp.getLastGround() != null) {
				e.getPlayer().teleport(pp.getLastGround());
				return;
			}
			e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation());
			if (pp.getLocation() >= 0) {
			Main.times.remove(e.getPlayer().getUniqueId());
			Main.times.put(e.getPlayer().getUniqueId(),System.currentTimeMillis());
			pp.setFails(pp.getFails() + 1);
			if (pp.getFails() % 20 == 0) {
				e.getPlayer().sendMessage("""
						§a> §3§lHaving trouble?
						§a> §6§lPLUS §rusers are able to use §bPractice Mode §rwhich
						§a> §rallows them to respawn right on the jump they fell.
						§a> §bPractice Mode §rcompletions do not count as completions though.
						§a> §rTo learn more about §6§lPLUS§r do §b/plus§r.""");
			}
			return;
			}
			//FlightStopper.stopflight(e);
			
		}
		if (!(Math.floor(e.getFrom().getY()) == e.getFrom().getY())) {
			if ((Math.floor(e.getTo().getY()) == e.getTo().getY())) {
				ParkourPlayer pp = Main.PLAYERS.get(e.getPlayer().getUniqueId());
				if (pp.isPracMode()) {
					pp.setLastGround(new Location(e.getPlayer().getWorld(),e.getFrom().getX(),e.getFrom().getY(),e.getFrom().getZ()));
					e.getPlayer().sendMessage("§aPractice Mode checkpoint!");
				}
			}
		}
	}
	
}
