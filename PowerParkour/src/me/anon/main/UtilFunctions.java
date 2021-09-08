package me.anon.main;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class UtilFunctions {

	static HashMap<Location,Material> blockchanges = new HashMap<>();

	public static void ActionBar(String input,Player p) {
		PacketPlayOutChat packet = new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"" + input + "\"}"), (byte) 2);
		((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
	}
	public static ItemStack itemFactory(String customid, String displayName, Material material, ArrayList<String> lore, boolean enchanted) {
		ItemStack stack = new ItemStack(material);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("§r" + displayName);
		if (lore != null) {
			meta.setLore(lore);
		}
		if (enchanted) {
			meta.addEnchant(Enchantment.DURABILITY, 1, true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		stack.setItemMeta(meta);
		if (customid != null) {
			net.minecraft.server.v1_8_R3.ItemStack modifiableStack = CraftItemStack.asNMSCopy(stack);
			NBTTagCompound modCompound = (modifiableStack.hasTag()) ? modifiableStack.getTag() : new NBTTagCompound();
			modCompound.set("customid",new NBTTagString(customid));
			stack = CraftItemStack.asBukkitCopy(modifiableStack);
		}



		return stack;
	}

	public static void copyChunk(World worldFrom, int fromX, int fromZ, World worldTo, int toX, int toZ) {
		// the daily challenge chunks have some very specific rules in order to cut down on processing time. Only blocks at y35 or above are copied, and only blocks at y50 or below are copied.
		Chunk from = worldFrom.getChunkAt(fromX, fromZ);
		System.out.println(from.getX());
		from.getBlock(0, 0, 0).setType(Material.WOOL);
		System.out.println("x: " + from.getBlock(16, 0, 16).getLocation().getX() + " y: " + from.getBlock(16, 0, 16).getLocation().getY() + " z: " + from.getBlock(16, 0, 16).getLocation().getZ());
		if (!from.isLoaded()) from.load();
		Chunk to = worldTo.getChunkAt(toX,toZ);
		for (int x = 0; x < 16; x++) {
			for (int y = 35; y < 50; y++) {
				for (int z = 0; z < 16; z++) {
					if (to.getBlock(x, y, z).getType().equals(from.getBlock(x, y, z).getType())) {
						continue;
					}
					//to.getBlock(x, y, z).setType(from.getBlock(x, y, z).getType());
					blockchanges.put(new Location(worldTo,x + (to.getX()*16),y,z+(to.getZ()*16)), from.getBlock(x, y, z).getType());
					//System.out.println("Copied block at x" +x + " y"+y+" z"+z+". Type: " + from.getBlock(x, y, z).getType().name());
					//System.out.println("target world X: " + (x + to.getX()));
				}
			}
		}
		System.out.println("Blockchanges size: " + blockchanges.size());
	}

	public static void performUpdates(int num) {
		System.out.println("Starting block updates");
		int i = 0;
		ArrayList<Location> tbr = new ArrayList<>();
		for (Map.Entry<Location,Material> block : blockchanges.entrySet()) {
			if (!block.getKey().getChunk().isLoaded()) block.getKey().getChunk().load();
			block.getKey().getBlock().setType(block.getValue());
			//System.out.println(block.getKey().getBlock().getType().name());
			i++;
			tbr.add(block.getKey());
			if (i == num) break;
		}
		for (Location ii : tbr) {
			blockchanges.remove(ii);
		}
	}
	
}
