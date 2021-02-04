package me.anon.main;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;


public class ActionBarManager {
	  // skidded from bukkit forums tutorial :p
	public static void ActionBar(String input,Player p) {
		PacketPlayOutChat packet = new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"" + input + "\"}"), (byte) 2);
		((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
	}
	
	
}
