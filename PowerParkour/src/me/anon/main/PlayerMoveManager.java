package me.anon.main;

import me.anon.util.ParkourPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveManager {
    public static void playerMoveHandler(PlayerMoveEvent e) {
        if (e.getTo().getY() < 30 && e.getPlayer().getGameMode() != GameMode.CREATIVE) {

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
                }
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
