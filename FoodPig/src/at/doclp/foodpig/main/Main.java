package at.doclp.foodpig.main;


import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import at.doclp.foodpig.util.FoodPig;


public class Main extends JavaPlugin{
	public static Main plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		this.saveDefaultConfig();
		FoodPig foodPig = new FoodPig();
		getCommand("foodpig").setExecutor(foodPig);
		PluginManager pluginManager = Bukkit.getPluginManager();
		pluginManager.registerEvents(foodPig, this);
	}
	public static Main getPlugin() {
		return plugin;
	}
}
