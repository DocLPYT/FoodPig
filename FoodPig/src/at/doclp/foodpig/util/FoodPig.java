package at.doclp.foodpig.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;


import at.doclp.foodpig.main.Main;

public class FoodPig implements CommandExecutor, Listener{
	
	public FileConfiguration config = Main.getPlugin().getConfig();
	private String PIG_TITLE = Main.getPlugin().getConfig().getString("settings.pig-name").replace('&', '§');
	public Material ItemType = Material.COOKED_BEEF;
	public int ItemCount = 3;
	public boolean killmode = false;
	public boolean rlMessage = true;
	public void spawnFoodPig(Location location, Player player) {
		Pig pig = (Pig) location.getWorld().spawnEntity(location, EntityType.PIG);
		pig.setAI(false);
		pig.setGravity(true);
		pig.setHealth(2);
		pig.setCustomName(PIG_TITLE);
		pig.setCustomNameVisible(true);
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(args.length > 0) {
				if(args[0].equalsIgnoreCase("spawn")) {				
					setFoodPig(p);
				} else if(args[0].equalsIgnoreCase("setdrop")) {
					setDrop(p);
				}else if(args[0].equalsIgnoreCase("default")) {
					setDefault(p);
				}else if(args[0].equalsIgnoreCase("remove")) {
					setKillmode(p);
				}else if(args[0].equalsIgnoreCase("xp")) {
					setXP(p);
				}else if(args[0].equalsIgnoreCase("reload")){
					reloadConf(p);
				}else if(args[0].equalsIgnoreCase("boat")) {
					if(Main.getPlugin().getConfig().getBoolean("settings.boat-stealing")) {
						Main.getPlugin().getConfig().set("settings.boat-stealing", false);
						Main.getPlugin().saveConfig();
						p.sendMessage(Main.getPlugin().getConfig().getString("messages.boatstealingoff").replace('&', '§'));
					}else if(!Main.getPlugin().getConfig().getBoolean("settings.boat-stealing")) {
						Main.getPlugin().getConfig().set("settings.boat-stealing", true);
						Main.getPlugin().saveConfig();
						p.sendMessage(Main.getPlugin().getConfig().getString("messages.boatstealingon").replace('&', '§'));
					}
					
				}else if(args[0].equalsIgnoreCase("help")) {
					p.sendMessage(Main.getPlugin().getConfig().getString("messages.usage").replace('&', '§'));
				}
			}
			else if(args.length == 0){
				try {
					p.sendMessage(ChatColor.GREEN + "Du hast das " + ChatColor.GOLD + "FoodPig GUI " + ChatColor.GREEN + "geöffnet");
					openGui3(p);									
				}catch(Exception ex) {return false;}
			}
		}
		return false;
	}
	
	public void setXP(Player sender) {
		Player p = (Player) sender;
		if(Main.getPlugin().getConfig().getBoolean("settings.dropxp") == true) {
			Main.getPlugin().getConfig().set("settings.dropxp", false);
			Main.getPlugin().saveConfig();
			p.sendMessage("§aDer §6XP Drop §avom FoodPig wurde §cdeaktiviert");
		}else if(Main.getPlugin().getConfig().getBoolean("settings.dropxp") == false) {
			Main.getPlugin().getConfig().set("settings.dropxp",true);
			Main.getPlugin().saveConfig();
			p.sendMessage("§aDer §6XP Drop §avom FoodPig wurde §6aktiviert");
		}
	}
	public void setKillmode(Player sender) {
		Player player = (Player) sender;
		if(player.hasPermission("foodpig.admin")) {						
			if(!killmode) {
				killmode = true;
				player.sendMessage(Main.getPlugin().getConfig().getString("messages.removingmodeon").replace('&', '§'));
			}
			else if(killmode) {
				killmode=false;
				player.sendMessage(Main.getPlugin().getConfig().getString("messages.removingmodeoff").replace('&', '§'));
			}
		}else if(!player.hasPermission("foodpig.admin")) {
			player.sendMessage(Main.getPlugin().getConfig().getString("messages.noperms").replace('&', '§'));
		}
	}
	public void setDefault(Player sender) {
		Player player = (Player) sender;
		Main.getPlugin().getConfig().set("settings.pig-name", "&6Essenssau");
		Main.getPlugin().getConfig().set("settings.itemtype", (Material.COOKED_BEEF).toString());
		Main.getPlugin().getConfig().set("settings.itemcount", 3);
		Main.getPlugin().getConfig().set("settings.dropxp", true);
		Main.getPlugin().getConfig().set("settings.boat-stealing", false);
		Main.getPlugin().saveConfig();
		rlMessage = false;
		reloadConf(player);
		rlMessage = true;
		player.sendMessage("§aDie Einstellungen vom §6FoodPig §awurden wieder auf die Standarteinstellungen gesetzt");
	}
	public void setFoodPig(Player sender) {
		Player player = (Player) sender;
		try {
			Material.valueOf(Main.getPlugin().getConfig().getString("settings.itemtype"));			
		}catch (Exception ex){
			Main.getPlugin().getConfig().set("settings.itemtype", Material.COOKED_BEEF.toString());
			if(Main.getPlugin().getConfig().getInt("settings.itemcount") < 0 || Main.getPlugin().getConfig().getInt("settings.itemcount") > 64 ) {
				Main.getPlugin().getConfig().set("settings.itemcount", 3);
			}
			Main.getPlugin().saveConfig();
			player.sendMessage(ChatColor.RED + "Das Item in der Config ist ungültig, deshalb wurde der Drop wieder auf Default gesetzt!");
		}
		Material item = Material.valueOf(Main.getPlugin().getConfig().getString("settings.itemtype"));
		if(Main.getPlugin().getConfig().getInt("settings.itemcount") <= 0 || Main.getPlugin().getConfig().getInt("settings.itemcount") > 64 ) {
			Main.getPlugin().getConfig().set("settings.itemcount", 3);			
			Main.getPlugin().saveConfig();
			player.sendMessage(ChatColor.RED + "Das Item in der Config ist ungültig, deshalb wurde der Drop wieder auf Default gesetzt!");
		}
		if(item == Material.PUFFERFISH) {
			Main.getPlugin().getConfig().set("settings.itemtype", "COOKED_BEEF");
			Main.getPlugin().getConfig().set("settings.itemcount", 3);
			Main.getPlugin().saveConfig();
			player.sendMessage(ChatColor.RED + "Willst du jemanden vergiften? Der Drop wurde wieder auf Default gesetzt");
		}
		if(!item.isEdible()){
			Main.getPlugin().getConfig().set("settings.itemtype", Material.COOKED_BEEF.toString());
			Main.getPlugin().saveConfig();
			player.sendMessage(ChatColor.RED + "Das Item in der Config ist ungültig, deshalb wurde der Drop wieder auf Default gesetzt!");
		}else {
		}
		
		if(player.hasPermission("foodpig.admin")) {
		spawnFoodPig(player.getLocation(), player);
		player.sendMessage(Main.getPlugin().getConfig().getString("messages.created").replace('&', '§'));

		player.sendMessage("§aDie §6Essenssau §a droppt zurzeit §6" + String.valueOf(Main.getPlugin().getConfig().getInt("settings.itemcount") + "§a Stück §6" + Material.valueOf(Main.getPlugin().getConfig().getString("settings.itemtype"))));
		}else if(!(player.hasPermission("foodpig.admin"))){
		player.sendMessage(Main.getPlugin().getConfig().getString("messages.noperms").replace('&', '§'));
		}
	}
	public void reloadConf(Player sender) {
			Player p = (Player) sender;
			String pig_title_before = PIG_TITLE;
			Main.getPlugin().reloadConfig();
			Main.getPlugin().saveConfig();
			p.sendMessage(ChatColor.GOLD + "--- FoodPig Information ---");
			p.sendMessage(ChatColor.GREEN + "FoodPig Name: " + ChatColor.RESET + Main.getPlugin().getConfig().getString("settings.pig-name").replace('&', '§'));
			p.sendMessage(ChatColor.GREEN + "FoodPig Item Type: " + ChatColor.RESET + Main.getPlugin().getConfig().getString("settings.itemtype"));
			p.sendMessage(ChatColor.GREEN + "FoodPig Item Count: " + ChatColor.RESET + Main.getPlugin().getConfig().getInt("settings.itemcount"));
			if(Main.getPlugin().getConfig().getBoolean("settings.dropxp") == true) {
				p.sendMessage(ChatColor.GREEN + "FoodPig Drop XP: " + ChatColor.GREEN + "ON");
			}else if (Main.getPlugin().getConfig().getBoolean("settings.dropxp") == false) {
				p.sendMessage(ChatColor.GREEN + "FoodPig Drop XP: " + ChatColor.RED + "OFF");
			}
			if(Main.getPlugin().getConfig().getBoolean("settings.boat-stealing") == true) {
				p.sendMessage(ChatColor.GREEN + "FoodPig Boat Stealing: " + ChatColor.GREEN + "ON");
			}else if(Main.getPlugin().getConfig().getBoolean("settings.boat-stealing") == false) {
				p.sendMessage(ChatColor.GREEN + "FoodPig Boat Stealing: " + ChatColor.RED + "OFF");
			}
			p.sendMessage(ChatColor.GOLD + "------------------------");
			for(Entity n : p.getWorld().getEntities()) {
				if(n instanceof Pig && n.getCustomName() == pig_title_before) {
					var loc = n.getLocation();
					PIG_TITLE = Main.getPlugin().getConfig().getString("settings.pig-name").replace('&', '§');
					n.remove();
					spawnFoodPig(loc, p);
				}
			}
			p.sendMessage("§aConfig neu geladen!");			
	}
	public void setDrop(Player sender) {
		Player player = (Player) sender;
		if(player.hasPermission("foodpig.admin")){						
			ItemType = player.getInventory().getItemInMainHand().getType();
			ItemCount = player.getInventory().getItemInMainHand().getAmount();
			if(ItemType.equals(Material.AIR)) {
				player.sendMessage("§cDu kannst Luft nicht als Drop setzen!");
			}else if(!ItemType.isEdible()){
				player.sendMessage(ChatColor.RED + "Du kannst nur ESSBARE Items als Drop setzten");
			}else if(ItemType.equals(Material.PUFFERFISH)) {
				Main.getPlugin().getConfig().set("settings.itemtype", "COOKED_BEEF");
				Main.getPlugin().getConfig().set("settings.itemcount", 3);
				Main.getPlugin().saveConfig();
				player.sendMessage(ChatColor.RED + "Willst du jemanden vergiften? Der Drop wurde wieder auf Default gesetzt");
			}else{							
				Main.getPlugin().getConfig().set("settings.itemtype", player.getInventory().getItemInMainHand().getType().toString());
				Main.getPlugin().getConfig().set("settings.itemcount", player.getInventory().getItemInMainHand().getAmount());
				Main.getPlugin().saveConfig();
				player.sendMessage("§aDie §6Essenssau §adroppt ab jetzt §6" + String.valueOf(Main.getPlugin().getConfig().getInt("settings.itemcount") + "§a Stück §6" + Material.valueOf(Main.getPlugin().getConfig().getString("settings.itemtype"))));
			}
		}else {
			player.sendMessage(Main.getPlugin().getConfig().getString("messages.noperms").replace('&', '§'));
		}
	}
	
	@EventHandler
	public void handleShopInteract(PlayerInteractEntityEvent event) {
		Player p = (Player) event.getPlayer();
		Pig gui = (Pig) event.getRightClicked();
		if(!(event.getRightClicked() instanceof Pig && p.isSneaking())) 
			{
				if(killmode) {
					gui.remove();
					p.sendMessage(Main.getPlugin().getConfig().getString("messages.removed").replace('&', '§'));
				}else if(gui.hasAI() == false){	
						ItemStack test = new ItemStack(p.getInventory().getItemInMainHand());
						if(p.getInventory().getItemInMainHand().getType() == Material.NAME_TAG) {
							if(test.getItemMeta().getDisplayName() == "") {
								p.sendMessage(ChatColor.LIGHT_PURPLE + "Netter Versuch :) Der Name Tag wurde aus deinem Inventar gedroppt. Wenn du das FoodPig umbennen willst, kannst du es in der Config tun");
							}else {							
								p.sendMessage(ChatColor.DARK_PURPLE + "Netter Versuch :) Der Name Tag wurde aus deinem Inventar gedroppt. Wenn du das FoodPig zu " + ChatColor.LIGHT_PURPLE + test.getItemMeta().getDisplayName() + ChatColor.DARK_PURPLE + " umbennen willst, kannst du es in der Config tun");
							}
						p.getInventory().setItemInMainHand(null);
						p.getWorld().dropItemNaturally(p.getLocation(), test);
					}
					openGui3(p);
				}
				return;
			}
		if(gui.getCustomName().equals(PIG_TITLE)) {
			if(gui.hasAI()) return;
			event.setCancelled(true);
			Player player = event.getPlayer();
			openGui(player);
		}
	}
	
	 private void openGui(Player player){
	        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Drop Selector 1/2");
	        
	        ItemStack BlackGlassPane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
	        ItemMeta BlackGlassPaneMeta = BlackGlassPane.getItemMeta();
	        BlackGlassPaneMeta.setDisplayName(ChatColor.DARK_GRAY + "");
	        BlackGlassPane.setItemMeta(BlackGlassPaneMeta);
	        for(int i=0; i<9; i++) {
	        	inv.setItem(i, BlackGlassPane);
	        }
	        inv.setItem(9, makeItem(Material.COOKED_BEEF, ChatColor.GREEN + "Beef"));
	        inv.setItem(10, makeItem(Material.COOKED_PORKCHOP, ChatColor.GREEN + "Pork"));
	        inv.setItem(11, makeItem(Material.COOKED_CHICKEN, ChatColor.GREEN + "Chicken"));
	        inv.setItem(12, makeItem(Material.COOKED_RABBIT, ChatColor.GREEN + "Rabbit"));
	        inv.setItem(13, makeItem(Material.COOKED_MUTTON, ChatColor.GREEN + "Mutton"));
	        inv.setItem(14, makeItem(Material.COOKED_COD, ChatColor.GREEN + "Cod"));
	        inv.setItem(15, makeItem(Material.COOKED_SALMON, ChatColor.GREEN + "Salmon"));
	        inv.setItem(16, makeItem(Material.APPLE, ChatColor.GREEN + "Apple"));
	        inv.setItem(17, makeItem(Material.CARROT, ChatColor.GREEN + "Carrot"));      
	        player.openInventory(inv);
	        for(int j=18; j<26; j++) {
	        	inv.setItem(j, BlackGlassPane);
	        }
	        ItemStack cancel = new ItemStack(Material.BARRIER);
	        ItemMeta cancelMeta = cancel.getItemMeta();
	        cancelMeta.setDisplayName(ChatColor.RED + "Cancel");
	        cancel.setItemMeta(cancelMeta);
	        inv.setItem(22, cancel);
	        ItemStack next = makeItem(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN + "Next");
	        ItemStack prev = makeItem(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN + "Previous");
	        inv.setItem(18, prev);
	        inv.setItem(26, next);

	    }
	 private void openGuipage2(Player player) {
		 Inventory inv = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Drop Selector 2/2");
		 player.openInventory(inv);
		 
		 ItemStack BlackGlassPane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
	        ItemMeta BlackGlassPaneMeta = BlackGlassPane.getItemMeta();
	        BlackGlassPaneMeta.setDisplayName(ChatColor.DARK_GRAY + "");
	        BlackGlassPane.setItemMeta(BlackGlassPaneMeta);
	        for(int i=0; i<9; i++) {
	        	inv.setItem(i, BlackGlassPane);
	        }
	        inv.setItem(9, makeItem(Material.BREAD, ChatColor.GREEN + "Bread"));
	        inv.setItem(10, makeItem(Material.COOKIE, ChatColor.GREEN + "Cookie"));
	        inv.setItem(11, makeItem(Material.MELON_SLICE, ChatColor.GREEN + "Melon"));
	        inv.setItem(12, makeItem(Material.DRIED_KELP, ChatColor.GREEN + "Kelp"));
	        inv.setItem(13, makeItem(Material.BAKED_POTATO, ChatColor.GREEN + "Potato"));
	        inv.setItem(14, makeItem(Material.BEETROOT, ChatColor.GREEN + "Beetroot"));
	        inv.setItem(15, makeItem(Material.PUMPKIN_PIE, ChatColor.GREEN + "Pie"));
	        inv.setItem(16, makeItem(Material.SWEET_BERRIES, ChatColor.GREEN + "Sweet Berries"));
	        inv.setItem(17, makeItem(Material.GLOW_BERRIES, ChatColor.GREEN + "Glow Berries"));      
	        for(int j=18; j<26; j++) {
	        	inv.setItem(j, BlackGlassPane);
	        }
	        ItemStack cancel = new ItemStack(Material.BARRIER);
	        ItemMeta cancelMeta = cancel.getItemMeta();
	        cancelMeta.setDisplayName(ChatColor.RED + "Cancel");
	        cancel.setItemMeta(cancelMeta);
	        inv.setItem(22, cancel);
	        ItemStack next = makeItem(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN + "Next");
	        ItemStack prev = makeItem(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN + "Previous");
	        inv.setItem(18, prev);
	        inv.setItem(26, next);		 
	 }
	 private ItemStack makeItem(Material material, String name) {
		 ItemStack name1 = new ItemStack(material);
		 ItemMeta name1Meta = name1.getItemMeta();
		 name1Meta.setDisplayName(name);
		 name1.setItemMeta(name1Meta);

		return name1;
	 }
	
	@EventHandler
	public void InventClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Inventory open = event.getClickedInventory();
		ItemStack item = event.getCurrentItem();

		if(open == null) {return;}
		
		if(event.getView().getTitle().equals(ChatColor.GOLD + "Drop Selector 1/2")) {
			event.setCancelled(true);
			if(item.getType().equals(Material.COOKED_BEEF)) {
				setDropAutomatic(player, "Beef", Material.COOKED_BEEF);
			}
			if(item.getType().equals(Material.COOKED_PORKCHOP)) {
				setDropAutomatic(player, "Pork", Material.COOKED_PORKCHOP);
			}
			if(item.getType().equals(Material.COOKED_CHICKEN)) {
				setDropAutomatic(player, "Chicken", Material.COOKED_CHICKEN);
			}
			if(item.getType().equals(Material.COOKED_RABBIT)) {
				setDropAutomatic(player, "Rabbit", Material.COOKED_RABBIT);
			}
			if(item.getType().equals(Material.COOKED_MUTTON)) {
				setDropAutomatic(player, "Mutton", Material.COOKED_MUTTON);
			}
			if(item.getType().equals(Material.COOKED_COD)) {
				setDropAutomatic(player, "Cod", Material.COOKED_COD);
			}
			if(item.getType().equals(Material.COOKED_SALMON)) {
				setDropAutomatic(player, "Salmon", Material.COOKED_SALMON);
			}
			if(item.getType().equals(Material.APPLE)) {
				setDropAutomatic(player, "Apple", Material.APPLE);
			}
			if(item.getType().equals(Material.CARROT)) {
				setDropAutomatic(player, "Carrot", Material.CARROT);
			}
			if(item.getType().equals(Material.BARRIER)) {
				player.closeInventory();
			}
			if(item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Next") && item.getType().equals(Material.LIME_STAINED_GLASS_PANE)) {
				openGuipage2(player);
			}
			if(item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Previous") && item.getType().equals(Material.LIME_STAINED_GLASS_PANE)) {
				player.sendMessage("§cDu kannst nicht auf die vorherige Seite");
			}
		}else if(event.getView().getTitle().equals(ChatColor.GOLD + "Drop Selector 2/2")) {
			if(item.getType().equals(Material.BREAD)){
				setDropAutomatic(player, "Bread", Material.BREAD);
			}
			if(item.getType().equals(Material.MELON)){
				setDropAutomatic(player, "Melon", Material.MELON);
			}
			if(item.getType().equals(Material.DRIED_KELP)){
				setDropAutomatic(player, "Kelp", Material.DRIED_KELP);
			}
			if(item.getType().equals(Material.BAKED_POTATO)){
				setDropAutomatic(player, "Potato", Material.BAKED_POTATO);
			}
			if(item.getType().equals(Material.BEETROOT)){
				setDropAutomatic(player, "Beetroot", Material.BEETROOT);
			}
			if(item.getType().equals(Material.PUMPKIN_PIE)){
				setDropAutomatic(player, "Pumpkin pie", Material.PUMPKIN_PIE);
			}
			if(item.getType().equals(Material.SWEET_BERRIES)){
				setDropAutomatic(player, "Sweet Berries", Material.SWEET_BERRIES);
			}
			if(item.getType().equals(Material.GLOW_BERRIES)){
				setDropAutomatic(player, "Glow Berries", Material.GLOW_BERRIES);
			}		if(item.getType().equals(Material.BARRIER)) {
				player.closeInventory();
			}
			if(item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Next") && item.getType().equals(Material.LIME_STAINED_GLASS_PANE)) {
				player.sendMessage("§cEine 3.Seite ist (noch) nicht verfügbar");
				event.setCancelled(true);
			}
			if(item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Previous") && item.getType().equals(Material.LIME_STAINED_GLASS_PANE)) {
				openGui(player);
			}	
		}
	}
	
	public void setDropAutomatic(Player player, String name, Material material) {
		player.sendMessage("§aDer Drop wurde auf §6" + name + " §agestellt");
		Main.getPlugin().getConfig().set("settings.itemtype", material.toString());
		Main.getPlugin().saveConfig();
		player.closeInventory();
		openGui2(player);
	}
	
	@SuppressWarnings("deprecation")
	private void openGui2(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.GOLD + Main.getPlugin().getConfig().getString("settings.itemtype") + " Count Selector");
        player.openInventory(inv);
        
        ItemStack item = new ItemStack(Material.valueOf(Main.getPlugin().getConfig().getString("settings.itemtype")), Main.getPlugin().getConfig().getInt("settings.itemcount"));
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + Main.getPlugin().getConfig().getString("settings.itemtype"));
        item.setItemMeta(itemMeta);
                
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        headMeta.setOwner("TheNewTsar");
        headMeta.setDisplayName("§4-");
        head.setItemMeta(headMeta);
        
        ItemStack head2 = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta2 = (SkullMeta) head.getItemMeta();
        headMeta2.setOwner("Trajan");
        headMeta2.setDisplayName("§a+");
        head2.setItemMeta(headMeta2);
        
        inv.setItem(2, head2);
        inv.setItem(4, item);
        inv.setItem(6, head);
	}
	@EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
		if(event.getView().getTitle().equals(ChatColor.GOLD + Main.getPlugin().getConfig().getString("settings.itemtype") + " Count Selector")) {			
			Player player = (Player) event.getPlayer();
			player.sendMessage("§aDas Essensschwein droppt jetzt §6" + Main.getPlugin().getConfig().getInt("settings.itemcount") + "§a Stück §6" + Main.getPlugin().getConfig().getString("settings.itemtype"));
		}
	}
	
	@EventHandler
	public void InventClick2(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Inventory open = event.getClickedInventory();
		ItemStack item = event.getCurrentItem();
		
		if(open == null) {return;}
		
		if(event.getView().getTitle().equals(ChatColor.GOLD + Main.getPlugin().getConfig().getString("settings.itemtype") + " Count Selector")) {
			event.setCancelled(true);
			if(item.getItemMeta().getDisplayName().equals("§a+")) {
				if(Main.getPlugin().getConfig().getInt("settings.itemcount") == 64) {
					player.sendMessage("§cEs können nicht mehr als §a64 Stück§c droppen!");;
				}else {					
					Main.getPlugin().getConfig().set("settings.itemcount", Main.getPlugin().getConfig().getInt("settings.itemcount")+1);
					ItemStack item1 = new ItemStack(Material.valueOf(Main.getPlugin().getConfig().getString("settings.itemtype")), Main.getPlugin().getConfig().getInt("settings.itemcount"));
					ItemMeta item1Meta = item1.getItemMeta();
					Main.getPlugin().saveConfig();
					item1Meta.setDisplayName(ChatColor.GREEN + Main.getPlugin().getConfig().getString("settings.itemtype"));
					item1.setItemMeta(item1Meta);
					open.setItem(4, item1);
				}
			}
			if(item.getItemMeta().getDisplayName().equals("§4-")) {
				if(Main.getPlugin().getConfig().getInt("settings.itemcount") == 1) {
					player.sendMessage("§cEs können nicht weniger als §a1 Stück§c droppen!");;
				}else {					
					Main.getPlugin().getConfig().set("settings.itemcount", Main.getPlugin().getConfig().getInt("settings.itemcount")-1);
					ItemStack item1 = new ItemStack(Material.valueOf(Main.getPlugin().getConfig().getString("settings.itemtype")), Main.getPlugin().getConfig().getInt("settings.itemcount"));
					ItemMeta item1Meta = item1.getItemMeta();
					Main.getPlugin().saveConfig();
					item1Meta.setDisplayName(ChatColor.GREEN + Main.getPlugin().getConfig().getString("settings.itemtype"));
					item1.setItemMeta(item1Meta);
					open.setItem(4, item1);
				}
			}else if(item.getItemMeta().getDisplayName().equals("§4-") && event.isShiftClick()) {
				player.sendMessage("Shift");
			}
		}
	}
	private void openGui3(Player player) {
		Inventory inv = Bukkit.createInventory(null, 27, ChatColor.GOLD + "FoodPig GUI");
        player.openInventory(inv);
        
        
        String State = null;
        if(Main.getPlugin().getConfig().getBoolean("settings.dropxp")) {
        	State = ChatColor.RED + "OFF";
        }else if (!Main.getPlugin().getConfig().getBoolean("settings.dropxp")) {
        	State = ChatColor.GREEN + "ON";
        }else {
        	State = ChatColor.GREEN + "ON";
        }
    	String StateRemove = null;
    	if(killmode) {
    		StateRemove = ChatColor.RED + "OFF";
    	}else if(!killmode) {
    		StateRemove = ChatColor.GREEN + "ON";
    	}else {
    		StateRemove = ChatColor.RED + "OFF";
    	}
    	
        ItemStack BlackGlassPane = makeItem(Material.BLACK_STAINED_GLASS_PANE, ChatColor.GRAY + "");
        for(int i=0; i<9; i++) {
        	inv.setItem(i, BlackGlassPane);
        }
        inv.setItem(9, makeItem(Material.PIG_SPAWN_EGG, ChatColor.GOLD + "Spawn"));
        inv.setItem(10, makeItem(Material.EXPERIENCE_BOTTLE, ChatColor.GOLD + "XP " + State));
        inv.setItem(11, makeItem(Material.DIAMOND_SWORD, ChatColor.RED + "Removing Mode " + StateRemove));
        inv.setItem(12, makeItem(Material.COOKED_BEEF, ChatColor.GREEN + "Default Settings"));
        inv.setItem(13, makeItem(Material.DROPPER, ChatColor.GREEN + "Setdrop"));
        inv.setItem(14, makeItem(Material.CHEST_MINECART, ChatColor.GREEN + "Open Drop Selector"));
        inv.setItem(15, makeItem(Material.OAK_BOAT, ChatColor.GREEN + "Boat Stealing"));
        inv.setItem(16, makeItem(Material.COMPASS, ChatColor.GREEN + "Help"));
        inv.setItem(17, makeItem(Material.WRITABLE_BOOK, ChatColor.GOLD + "Reload Config"));
        for(int i=18; i<28; i++) {
        	inv.setItem(i, BlackGlassPane);
        }
	}
	
	
	@EventHandler
	public void InventClick3(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Inventory open = event.getClickedInventory();
		ItemStack item = event.getCurrentItem();
		
		if(open == null) {return;}
		
		if(event.getView().getTitle().equals(ChatColor.GOLD + "FoodPig GUI")) {
			event.setCancelled(true);
			if(item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Spawn")) {
				spawnFoodPig(player.getLocation(), player);
				player.closeInventory();
			}
			if(item.getType().equals(Material.EXPERIENCE_BOTTLE)) {
				setXP(player);
				player.closeInventory();
			}
			if(item.getType().equals(Material.DIAMOND_SWORD)) {
				setKillmode(player);
				player.closeInventory();
			}
			if(item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Default Settings")) {
				setDefault(player);
				player.closeInventory();
			}
			if(item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Setdrop")) {
				setDrop(player);
				player.closeInventory();
			}
			if(item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Open Drop Selector")) {
				openGui(player);
			}
			if(item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Reload Config")) {
				reloadConf(player);
				player.closeInventory();
			}
		}
	}

	
	@EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
		if(Main.getPlugin().getConfig().getBoolean("settings.boat-stealing"))
		{
			
		}else {
			Entity j = event.getEntered();
	        if(j instanceof Pig) {
	            if(j.getCustomName() != null) {
	                if(j.getCustomName().equals(PIG_TITLE)) {
	                    Location loc = event.getEntered().getLocation();
	                    j.teleport(loc);
	                    event.setCancelled(true);
	                }
	            }
	        }
		}
        
    }
    	
	
	@EventHandler
	public void handleFoodPigDeath(EntityDeathEvent event) {
		if(!(event.getEntity() instanceof Pig)) return;
		Pig pig = (Pig) event.getEntity();
		if(!pig.getCustomName().equals(PIG_TITLE)) return;
		if(pig.hasAI()) return;
		Player player = pig.getKiller();
		Material Item1;
		event.getDrops().clear();
		try {
			Material.valueOf(Main.getPlugin().getConfig().getString("settings.itemtype"));
		}catch (Exception ex){
			Main.getPlugin().getConfig().set("settings.itemtype", Material.COOKED_BEEF.toString());
			Main.getPlugin().saveConfig();
			pig.remove();
			Item1 = Material.COOKED_BEEF;
			player.sendMessage(ChatColor.RED + "Das Item in der Config ist ungültig, deshalb wurde der Drop wieder auf Default gesetzt!");

		}
		Item1 = Material.valueOf(Main.getPlugin().getConfig().getString("settings.itemtype"));
		if(Main.getPlugin().getConfig().getInt("settings.itemcount") <= 0 || Main.getPlugin().getConfig().getInt("settings.itemcount") > 64 ) {
			Main.getPlugin().getConfig().set("settings.itemcount", 3);			
			Main.getPlugin().saveConfig();
			player.sendMessage(ChatColor.RED + "Das Item in der Config ist ungültig, deshalb wurde der Drop wieder auf Default gesetzt!");
		}
		if(Item1 == Material.PUFFERFISH) {
			Main.getPlugin().getConfig().set("settings.itemtype", "COOKED_BEEF");
			Main.getPlugin().getConfig().set("settings.itemcount", 3);
			Main.getPlugin().saveConfig();
			Item1 = Material.COOKED_BEEF;
			pig.remove();
			player.sendMessage(ChatColor.RED + "Willst du jemanden vergiften? Der Drop wurde wieder auf Default gesetzt");
		}
		if(!Item1.isEdible()){
			Main.getPlugin().getConfig().set("settings.itemtype", Material.COOKED_BEEF.toString());
			Main.getPlugin().saveConfig();
			pig.remove();
			Item1 = Material.COOKED_BEEF;
			player.sendMessage(ChatColor.RED + "Das Item in der Config ist ungültig, deshalb wurde der Drop wieder auf Default gesetzt!");
		}else {
		}
		int Count = Main.getPlugin().getConfig().getInt("settings.itemcount");

		
		
		ItemStack foodDrop = new ItemStack(Item1, Count);
		pig.getWorld().dropItemNaturally(pig.getLocation(),foodDrop);
		if(Main.getPlugin().getConfig().getBoolean("settings.dropxp") == false) {
			event.setDroppedExp(0);
		}
		
		
		if(!(event.getEntity().getKiller() instanceof Player) || event.getEntity().getKiller() instanceof Player) {
			spawnFoodPig(pig.getLocation(), player);
		}
	}
}