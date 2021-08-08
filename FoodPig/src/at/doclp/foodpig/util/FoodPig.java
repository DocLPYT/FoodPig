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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;


import at.doclp.foodpig.main.Main;

public class FoodPig implements CommandExecutor, Listener{
	
	FileConfiguration config = Main.getPlugin().getConfig();
	private String PIG_TITLE = config.getString("settings.pig-name").replace('&', '§');
	public Material ItemType = Material.COOKED_BEEF;
	public int ItemCount = 3;
	public boolean killmode = false;
	public void spawnFoodPig(Location location, Player player) {
		Pig pig = (Pig) location.getWorld().spawnEntity(location, EntityType.PIG);
		pig.setAI(false);
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
					setStandart(p);
				}else if(args[0].equalsIgnoreCase("killmode")) {
					setKillmode(p);
				}else if(args[0].equalsIgnoreCase("xp")) {
					setXP(p);
				}else if(args[0].equalsIgnoreCase("reload")){
					reloadConf(p);
				}
			}
			else {
				p.sendMessage(ChatColor.GREEN + "Du hast das " + ChatColor.GOLD + "FoodPig GUI " + ChatColor.GREEN + "geöffnet");
				openGui3(p);
//				p.sendMessage("§cDer Command muss ein Argument haben");
//				p.sendMessage("§a/foodpig §6[spawn] §a| §6[setdrop] §a| §6[killmode]");
			}
		}
		return false;
	}
	
	public void setXP(Player sender) {
		Player p = (Player) sender;
		FileConfiguration config = Main.getPlugin().getConfig();
		if(config.getBoolean("settings.dropxp") == true) {
			config.set("settings.dropxp", false);
			Main.getPlugin().saveConfig();
			p.sendMessage("§aDer §6XP Drop §avom FoodPig wurde §cdeaktiviert");
		}else if(config.getBoolean("settings.dropxp") == false) {
			config.set("settings.dropxp",true);
			Main.getPlugin().saveConfig();
			p.sendMessage("§aDer §6XP Drop §avom FoodPig wurde §6aktiviert");
		}
	}
	public void setKillmode(Player sender) {
		Player player = (Player) sender;
		if(player.hasPermission("foodpig.admin")) {						
			if(!killmode) {
				killmode = true;
				player.sendMessage("§aDu befindest dich jetzt im §6Killmode §anun kannst du das FoodPig entfernen.");
				player.sendMessage("§aUm den §6Killmode §azu deaktivieren: §6/foodpig killmode");
			}
			else if(killmode) {
				killmode=false;
				player.sendMessage("§aDu befindest dich jetzt §cNICHT MEHR §aim §6Killmode §anun kannst du das FoodPig normal benutzten ;)");
				player.sendMessage("§aUm den §6Killmode §azu aktivieren: §6/foodpig killmode");
			}
		}else if(!player.hasPermission("foodpig.admin")) {
			player.sendMessage(config.getString("messages.noperms").replace('&', '§'));
		}
	}
	public void setStandart(Player sender) {
		FileConfiguration config = Main.getPlugin().getConfig();
		Player player = (Player) sender;
		config.set("settings.itemtype", (Material.COOKED_BEEF).toString());
		config.set("settings.itemcount", 3);
		config.set("settings.dropxp", true);
		Main.getPlugin().saveConfig();
		player.sendMessage("§aDer Drop vom §6FoodPig §awurde wieder auf die Standarteinstellungen gesetzt");
	}
	public void setFoodPig(Player sender) {
		Player player = (Player) sender;
		FileConfiguration config = Main.getPlugin().getConfig();

		if(player.hasPermission("foodpig.admin")) {
		spawnFoodPig(player.getLocation(), player);
		player.sendMessage(config.getString("messages.created").replace('&', '§'));

		player.sendMessage("§aDie §6Essenssau §a droppt zurzeit §6" + String.valueOf(config.getInt("settings.itemcount") + "§a Stück §6" + Material.valueOf(config.getString("settings.itemtype"))));
		}else if(!(player.hasPermission("foodpig.admin"))){
		player.sendMessage(config.getString("messages.noperms").replace('&', '§'));
		}
	}
	public void reloadConf(Player sender) {
		Main.getPlugin().reloadConfig();
		Main.getPlugin().saveConfig();					
		String pig_title_before = PIG_TITLE;
		Player p = (Player) sender;
		for(Entity n : p.getWorld().getEntities()) {
			if(n instanceof Pig && n.getCustomName().equals(pig_title_before)) {
				p.sendMessage("Schwein");
					Main.getPlugin().reloadConfig();
					var loc = n.getLocation();
					PIG_TITLE = Main.getPlugin().getConfig().getString("settings.pig-name").replace('&', '§');
					n.remove();
					spawnFoodPig(loc, p);
					p.sendMessage("§aConfig neu geladen!");
			}
			else {
				p.sendMessage("§aConfig neu geladen!");	
				return;
			}
		}
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
			}else {							
				FileConfiguration config = Main.getPlugin().getConfig();
				config.set("settings.itemtype", player.getInventory().getItemInMainHand().getType().toString());
				config.set("settings.itemcount", player.getInventory().getItemInMainHand().getAmount());
				Main.getPlugin().saveConfig();
				player.sendMessage("§aDie §6Essenssau §adroppt ab jetzt §6" + String.valueOf(config.getInt("settings.itemcount") + "§a Stück §6" + Material.valueOf(config.getString("settings.itemtype"))));
			}
		}else {
			player.sendMessage(config.getString("messages.noperms").replace('&', '§'));
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
					p.sendMessage("§aDu hast das §6Essensschwein §aentfernt!");
				}else {					
					openGui3(p);
				}
				return;
			}
			if(gui.getCustomName().equals(PIG_TITLE)) {
			event.setCancelled(true);
			Player player = event.getPlayer();
			openGui(player);
		}
	}
	
	 private void openGui(Player player){
	        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Drop Selector");
	        
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
		
		if(event.getView().getTitle().equals(ChatColor.GOLD + "Drop Selector")) {
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
				player.sendMessage("§cDie 2. Seite ist derzeit noch nicht verfügbar");
			}
			if(item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Previous") && item.getType().equals(Material.LIME_STAINED_GLASS_PANE)) {
				player.sendMessage("§cDu kannst nicht auf die vorherige Seite");
			}
		}
	}
	
	public void setDropAutomatic(Player player, String name, Material material) {
		FileConfiguration config = Main.getPlugin().getConfig();
		player.sendMessage("§aDer Drop wurde auf §6" + name + " §agestellt");
		config.set("settings.itemtype", material.toString());
		Main.getPlugin().saveConfig();
		player.closeInventory();
		openGui2(player);
	}
	
	@SuppressWarnings("deprecation")
	private void openGui2(Player player) {
		FileConfiguration config = Main.getPlugin().getConfig();

        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.GOLD + config.getString("settings.itemtype") + " Count Selector");
        player.openInventory(inv);
        
        ItemStack item = new ItemStack(Material.valueOf(config.getString("settings.itemtype")), config.getInt("settings.itemcount"));
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + config.getString("settings.itemtype"));
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
		FileConfiguration config = Main.getPlugin().getConfig();
		if(event.getView().getTitle().equals(ChatColor.GOLD + config.getString("settings.itemtype") + " Count Selector")) {			
			Player player = (Player) event.getPlayer();
			player.sendMessage("§aDas Essensschwein droppt jetzt §6" + config.getInt("settings.itemcount") + "§a Stück §6" + config.getString("settings.itemtype"));
		}
	}
	
	@EventHandler
	public void InventClick2(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Inventory open = event.getClickedInventory();
		ItemStack item = event.getCurrentItem();
		FileConfiguration config = Main.getPlugin().getConfig();
		
		if(open == null) {return;}
		
		if(event.getView().getTitle().equals(ChatColor.GOLD + config.getString("settings.itemtype") + " Count Selector")) {
			event.setCancelled(true);
			if(item.getItemMeta().getDisplayName().equals("§a+")) {
				if(config.getInt("settings.itemcount") == 64) {
					player.sendMessage("§cEs können nicht mehr als §a64 Stück§c droppen!");;
				}else {					
					config.set("settings.itemcount", config.getInt("settings.itemcount")+1);
					ItemStack item1 = new ItemStack(Material.valueOf(config.getString("settings.itemtype")), config.getInt("settings.itemcount"));
					ItemMeta item1Meta = item1.getItemMeta();
					Main.getPlugin().saveConfig();
					item1Meta.setDisplayName(ChatColor.GREEN + config.getString("settings.itemtype"));
					item1.setItemMeta(item1Meta);
					open.setItem(4, item1);
				}
			}
			if(item.getItemMeta().getDisplayName().equals("§4-")) {
				if(config.getInt("settings.itemcount") == 1) {
					player.sendMessage("§cEs können nicht weniger als §a1 Stück§c droppen!");;
				}else {					
					config.set("settings.itemcount", config.getInt("settings.itemcount")-1);
					ItemStack item1 = new ItemStack(Material.valueOf(config.getString("settings.itemtype")), config.getInt("settings.itemcount"));
					ItemMeta item1Meta = item1.getItemMeta();
					Main.getPlugin().saveConfig();
					item1Meta.setDisplayName(ChatColor.GREEN + config.getString("settings.itemtype"));
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
		FileConfiguration config = Main.getPlugin().getConfig();
        player.openInventory(inv);
        
        String State = null;
        if(config.getBoolean("settings.dropxp")) {
        	State = ChatColor.RED + "OFF";
        }else if (!config.getBoolean("settings.dropxp")) {
        	State = ChatColor.GREEN + "ON";
        }
    	String StateRemove = null;
    	if(killmode) {
    		StateRemove = ChatColor.RED + "OFF";
    	}else if(!killmode) {
    		StateRemove = ChatColor.GREEN + "ON";
    	}
    	
        ItemStack BlackGlassPane = makeItem(Material.BLACK_STAINED_GLASS_PANE, ChatColor.GRAY + "");
        for(int i=0; i<9; i++) {
        	inv.setItem(i, BlackGlassPane);
        }
        inv.setItem(10, makeItem(Material.PIG_SPAWN_EGG, ChatColor.GOLD + "Spawn"));
        inv.setItem(11, makeItem(Material.EXPERIENCE_BOTTLE, ChatColor.GOLD + "XP " + State));
        inv.setItem(12, makeItem(Material.DIAMOND_SWORD, ChatColor.RED + "Removing Mode " + StateRemove));
        inv.setItem(13, makeItem(Material.COOKED_BEEF, ChatColor.GREEN + "Standart Drop"));
        inv.setItem(14, makeItem(Material.DROPPER, ChatColor.GREEN + "Setdrop"));
        inv.setItem(15, makeItem(Material.CHEST_MINECART, ChatColor.GREEN + "Open Drop Selector"));
        inv.setItem(16, makeItem(Material.WRITABLE_BOOK, ChatColor.GOLD + "Reload Config"));
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
			if(item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Standart Drop")) {
				setStandart(player);
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
	public void handleFoodPigDeath(EntityDeathEvent event) {
		if(!(event.getEntity() instanceof Pig)) return;
		Pig pig = (Pig) event.getEntity();
		if(!pig.getCustomName().equals(PIG_TITLE)) return;
		Player player = pig.getKiller();
		event.getDrops().clear();
		FileConfiguration config = Main.getPlugin().getConfig();
//		Player p = (Player) pig.getKiller();
		Material Item1 = Material.valueOf(config.getString("settings.itemtype"));
		int Count = config.getInt("settings.itemcount");
		if(Item1 == null || Count == 0 || !Item1.isItem()) {
			Item1 = Material.COOKED_BEEF;
			player.sendMessage(ChatColor.RED + "Das Item in der Config ist ungültig!");
		}
		ItemStack foodDrop = new ItemStack(Item1, Count);
		pig.getWorld().dropItemNaturally(pig.getLocation(),foodDrop);
		if(config.getBoolean("settings.dropxp") == false) {
			event.setDroppedExp(0);
		}
		
		
		if(!(event.getEntity().getKiller() instanceof Player) || event.getEntity().getKiller() instanceof Player) {
			spawnFoodPig(pig.getLocation(), player);
		}
	}
}