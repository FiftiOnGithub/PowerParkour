package me.anon.main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class ChunkManager {
	
	static HashMap<Location,Material> blockchanges = new HashMap<Location,Material>();
	
	
	public static boolean copyChunk(World worldFrom,int fromX,int fromZ,World worldTo,int toX, int toZ) {
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
		return true;
	}
	
	public static void performUpdates(int num) {
		System.out.println("Starting block updates");
		int i = 0;
		ArrayList<Location> tbr = new ArrayList<Location>();
		for (Entry<Location,Material> block : blockchanges.entrySet()) {
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
