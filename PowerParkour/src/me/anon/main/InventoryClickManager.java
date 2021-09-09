package me.anon.main;

import me.anon.util.ParkourPlayer;
import me.anon.util.ShopCosmetic;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickManager {
    public static void inventoryClickEvent(InventoryClickEvent e) {
        if (e.getCurrentItem() != null && e.getWhoClicked().getGameMode() != GameMode.CREATIVE) e.setCancelled(true);
        if (e.getInventory().getName().equals("§aParkour Selector")) {
            if (e.getCurrentItem() == null) return;
            if (!e.getCurrentItem().hasItemMeta()) return;
            if (e.getCurrentItem().getItemMeta().hasLore()) {
                ItemStack modifiableBase = CraftItemStack.asNMSCopy(e.getCurrentItem());
                NBTTagCompound modifiableCompound = (modifiableBase.hasTag()) ? modifiableBase.getTag() : new NBTTagCompound();
                int levelid = Integer.parseInt(modifiableCompound.getString("customid"));
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
                        e.getWhoClicked().closeInventory();
                        return;

                    }
                    return;
                }
                if (pp.canPlayLevel(levelid) == 0) {
                    pp.sendPlayerToLocation(levelid);
                    p.sendMessage("§aYou have started " + Main.LEVELS.get(levelid).getName() + "! Good luck :)");
                } else p.sendMessage("§cYou can't play this level!");
                e.getWhoClicked().closeInventory();
            }
        } else if (e.getInventory().getName().startsWith("§eStore")) {
            if (e.getCurrentItem() == null) return;
            if (!e.getCurrentItem().hasItemMeta()) return;
            if (e.getCurrentItem().getItemMeta().hasLore()) {
                ItemStack modifiableBase = CraftItemStack.asNMSCopy(e.getCurrentItem());
                NBTTagCompound modifiableCompound = (modifiableBase.hasTag()) ? modifiableBase.getTag() : new NBTTagCompound();
                String actionid = modifiableCompound.getString("customid");
                ShopCosmetic cosmeticToBuy;
                try {
                    cosmeticToBuy = ShopCosmetic.valueOf(actionid);
                } catch(Error err) {
                    if (actionid.equals("no_action")) {
                        return;
                    } else {
                        System.out.println("Invalid action: " + actionid);
                        e.getWhoClicked().closeInventory();
                        return;
                    }
                }

                ParkourPlayer pp = Main.PLAYERS.get(e.getWhoClicked().getUniqueId());
                if (pp.getOwnedCosmetics().contains(cosmeticToBuy)) {
                    // Selecting
                    if (e.getCurrentItem().getItemMeta().hasEnchants()) {
                        e.getWhoClicked().sendMessage("§cThis item is already selected.");
                        e.getWhoClicked().closeInventory();
                        return;
                    }
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "selectorinternal " + e.getWhoClicked().getName() + " " + cosmeticToBuy.toString() + " " + cosmeticToBuy.toString().split("_")[0]);
                } else {
                    // Buying

                    if (e.getCurrentItem().getItemMeta().getLore().get(e.getCurrentItem().getItemMeta().getLore().size() - 1).startsWith("§cThis item")) {
                        e.getWhoClicked().sendMessage("§cYou can't buy this item right now!");
                        e.getWhoClicked().closeInventory();
                        return;
                    }

                    try {
                        int price = Integer.parseInt(e.getCurrentItem().getItemMeta().getLore().get(e.getCurrentItem().getItemMeta().getLore().size() - 1).split(" ")[5].substring(2));
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "storeinternal " + e.getWhoClicked().getName() + " " + cosmeticToBuy.toString() + " " + price);

                    } catch(NumberFormatException error){
                        e.getWhoClicked().sendMessage("§cUnable to determine the price of this product. Report this as a bug.");
                    }

                }

            }
            e.getWhoClicked().closeInventory();
        }
    }
}
