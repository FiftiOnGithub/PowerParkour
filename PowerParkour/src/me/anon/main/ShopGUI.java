package me.anon.main;

import me.anon.util.ParkourPlayer;
import me.anon.util.ShopCosmetic;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static me.anon.main.UtilFunctions.itemFactory;

public class ShopGUI {

    /*
    CM_NONE,CM_BASIC,CM_FLEX,
    PF_NONE,PF_PLUS,PF_PRO,PF_SMALLSTAR,PF_BIGSTAR,PF_STAFF,PF_CHRISTMAS_TREE,
    JM_NONE,JM_BASIC,JM_RAINBOW,
    MDT_ONE,
    DC_EXTRA_ONE
     */
    public static Inventory getInventory(ParkourPlayer p, boolean op) {
        Inventory inventory = Bukkit.createInventory(null,54,"§eStore");
        ArrayList<String> lore = new ArrayList<String>();

        // Prefixes
        {
            // If the player has plus, then allow them to buy the cosmetic for 0 coins (meaning it will unlock automatically in the ownership function). otherwise, ban purhase.
            if (p.hasPlus()) {
                lore.add("§7Show your support for the server");
                lore.add("§7with this cool chat prefix!");
                inventory.setItem(10, setBasedOnOwnership(p, "PF", ShopCosmetic.PF_PLUS, 0, "[§6§lP§r§f] Tag", Material.GOLD_INGOT, lore));
            } else {
                lore.add("§7Show your support for the server");
                lore.add("§7with this cool chat prefix!");
                inventory.setItem(10, setBasedOnOwnership(p, "PF", ShopCosmetic.PF_PLUS, -1, "[§6P§l§r§f] Tag", Material.GOLD_INGOT, lore));

            }
            lore.clear();

            if (!op) {
                lore.add("§7A prefix to show that the staff");
                lore.add("§7are better than everyone else :)");
                inventory.setItem(11, setBasedOnOwnership(p, "PF", ShopCosmetic.PF_STAFF, -1, "[§a§lSTAFF§r§f] Tag", Material.EMERALD_BLOCK, lore));
            } else {
                lore.add("§7A prefix to show that the staff");
                lore.add("§7are better than everyone else :)");
                inventory.setItem(11, setBasedOnOwnership(p, "PF", ShopCosmetic.PF_STAFF, 0, "[§a§lSTAFF§r§f] Tag", Material.EMERALD_BLOCK, lore));

            }
            lore.clear();
            lore.add("§7A prefix to bring on the christmas cheer!");
            lore.add("§7Only buyable during §cDecember§7.");
            // The name was determined before i found out that the christmas tree emote doesnt work in mc.
            inventory.setItem(12, setBasedOnOwnership(p, "PF", ShopCosmetic.PF_CHRISTMAS_TREE, -1, "[§2❄§f] Tag", Material.SNOW_BALL, lore));
            lore.clear();
            lore.add("§7Shine like a (small) star!");
            inventory.setItem(13, setBasedOnOwnership(p, "PF", ShopCosmetic.PF_SMALLSTAR, 200, "[§d✩§f] Tag", Material.GLOWSTONE_DUST, lore));
            lore.clear();
            lore.add("§7A big star for a great player!");
            inventory.setItem(14, setBasedOnOwnership(p, "PF", ShopCosmetic.PF_BIGSTAR, 800, "[§5✯§f] Tag", Material.GLOWSTONE, lore));
            lore.clear();
            lore.add("§7Show your dedication to the server!");
            inventory.setItem(15, setBasedOnOwnership(p, "PF", ShopCosmetic.PF_PRO, 1500, "[§cPRO§f] Tag", Material.REDSTONE, lore));
            lore.clear();
            lore.add("§7Remove your tag");
            inventory.setItem(16, setBasedOnOwnership(p, "PF", ShopCosmetic.PF_NONE, 0, "No Tag", Material.BARRIER, lore));
        }


        // Join Messages
        {
            lore.clear();
            lore.add("§7Tell other players when you join and leave!");
            inventory.setItem(28, setBasedOnOwnership(p, "JM", ShopCosmetic.JM_BASIC, 250, "§7Basic join & leave messages (Gray)", Material.DEAD_BUSH, lore));
            lore.clear();
            lore.add("§7Send a " + Main.rainbow("colourful") + "§7message to everyone when you join or leave!");
            inventory.setItem(29, setBasedOnOwnership(p, "JM", ShopCosmetic.JM_RAINBOW, 10000, Main.rainbow("Rainbow Join & Leave messages"), Material.PAINTING, lore));
            lore.clear();
            lore.add("§7No join & leave messages");
            inventory.setItem(30, setBasedOnOwnership(p, "JM", ShopCosmetic.JM_NONE, 0, "§7Remove join and leave messages", Material.BARRIER, lore));

        }

        // Completion Messages
        {
            lore.clear();
            lore.add("§7Show others when you complete a level!");
            inventory.setItem(32, setBasedOnOwnership(p, "CM", ShopCosmetic.CM_BASIC, 500, "§7Basic completion messages", Material.DEAD_BUSH, lore));
            lore.clear();
            lore.add("§7Flex the amount of times you've completed a level!");
            inventory.setItem(33, setBasedOnOwnership(p, "CM", ShopCosmetic.CM_FLEX, 3000, "§7Special Completion Messages (§cFlex§7)", Material.REDSTONE_BLOCK, lore));
            lore.clear();
            lore.add("§7No level completion messages");
            inventory.setItem(34, setBasedOnOwnership(p, "CM", ShopCosmetic.CM_NONE, 0, "§7No level complete messages", Material.BARRIER, lore));

        }

        {
            // Midas Touch
            lore.clear();
            lore.add("§7Earn §620%§7 more coins!");
            inventory.setItem(47, setBasedOnOwnership(p, "MDT", ShopCosmetic.MDT_ONE, 300, "§6Midas Touch", Material.GOLD_ORE, lore));

            // Daily Challenge extra life
            lore.clear();
            lore.add("§7Get one extra life in daily challenge!");
            inventory.setItem(51, setBasedOnOwnership(p, "DC", ShopCosmetic.DC_EXTRA_ONE, 600, "§bExtra Daily Challenge Life", Material.SEA_LANTERN, lore));

        }

        inventory.setItem(49,itemFactory("no_action","§7Your coins: §6" + p.getCoinBalance() + "§7.", Material.GOLD_INGOT, null, false));

        return inventory;
    }
    // Here the price information is added based on whether the player owns the item. the lore should not include it.
    public static ItemStack setBasedOnOwnership(ParkourPlayer p, String category, ShopCosmetic requirement, int price, String displayName,Material material, ArrayList<String> lore) {
        if (p.getOwnedCosmetics().contains(requirement)) {
            if (p.getSelectedItems().get(category) != null && p.getSelectedItems().get(category).equals(requirement)) {
                lore.add("§a");
                lore.add("§eSelected!");
                return itemFactory(requirement.toString(),displayName,material,lore,true);
            } else {
                lore.add("§a");
                lore.add("§aClick to select!");
                return itemFactory(requirement.toString(),displayName,material,lore,false);
            }
        } else {
            if (price > 0) {
                lore.add("§a");
                lore.add("§aClick here to buy for §6" + price + " §acoins");
                return itemFactory(requirement.toString(),displayName,material,lore,false);
            } else if (price != 0) {
                lore.add("§a");
                lore.add("§cThis item can't be bought!");
                return itemFactory(requirement.toString(),displayName,material,lore,false);
            } else {
                /*
                This cosmetic costs 0 coins, so it is automatically bought for you.
                 */
                ArrayList<ShopCosmetic> cosmetics = p.getOwnedCosmetics();
                cosmetics.add(requirement);
                p.setOwnedCosmetics(cosmetics);
                lore.add("§a");
                lore.add("§aClick to select!");
                return itemFactory(requirement.toString(),displayName,material,lore,false);
            }
        }
    }


}
