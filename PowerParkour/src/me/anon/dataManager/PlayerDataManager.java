package me.anon.dataManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerDataManager {
		 
	    private final String fileName;
	    private final JavaPlugin plugin;
	    private final File configFile;
	    private FileConfiguration fileConfiguration;
	 
	    @SuppressWarnings("deprecation")
		public PlayerDataManager(JavaPlugin plugin, String fileName) {
	        if (plugin == null)
	            throw new IllegalArgumentException("plugin cannot be null");
	        if (!plugin.isInitialized())
	            throw new IllegalArgumentException("plugin must be initiaized");
	        this.plugin = plugin;
	        this.fileName = fileName;
	        File dataFolder = plugin.getDataFolder();
	        if (dataFolder == null)
	            throw new IllegalStateException();
	        this.configFile = new File(plugin.getDataFolder(), fileName);
	    }
	 
	    @SuppressWarnings("deprecation")
		public void reloadConfig() {
	        fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
	 
	        // Look for defaults in the jar
	        InputStream defConfigStream = plugin.getResource(fileName);
	        if (defConfigStream != null) {
	            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	            fileConfiguration.setDefaults(defConfig);
	        }
	    }
	 
	    public FileConfiguration getConfig() {
	        if (fileConfiguration == null) {
	            this.reloadConfig();
	        }
	        return fileConfiguration;
	    }

		// TODO: Load player data one-by-one with loadPlayer function and save new data (owned cosmetics, selections, balance). Requires player data get abstraction

	    public void saveConfig() {
	        if (fileConfiguration == null || configFile == null) {
				System.out.println("Couldn't save to config! fileconfiguration or configfile was null! [PowerParkour]");
	        } else {
	            try {
	                getConfig().save(configFile);
	            } catch (IOException ex) {
	                plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
	            }
	        }
	    }
	    public void saveDefaultConfig() {
	        if (!configFile.exists()) {
	            this.plugin.saveResource(fileName, false);
	        }
	    }
}