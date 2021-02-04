package me.anon.main;

import org.bukkit.event.player.PlayerMoveEvent;

public class FlightStopper {

	public static boolean isflight(PlayerMoveEvent e) {
		
		if (e.getPlayer().getVelocity().getY() == 0) {
			if (e.getPlayer().isOnGround() || e.getPlayer().isFlying()) return false;
			
			ParkourPlayer pp = Main.PLAYERS.get(e.getPlayer().getUniqueId());
			if (pp.getLastGround().distance(e.getPlayer().getLocation()) > 5) {
				if (pp.getLastGround().getBlockY() == e.getPlayer().getLocation().getBlockY()) {
					return true;
				}
			}
			
		}
		return false;
		
	}
	
}
