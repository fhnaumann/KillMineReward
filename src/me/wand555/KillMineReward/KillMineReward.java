package me.wand555.KillMineReward;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class KillMineReward extends JavaPlugin {

	private KillMineReward plugin;
	public static boolean REGULAR_BREAK;
	public static boolean NO_PLAYER_PLACING_ORE_REWARD;
	public static final ArrayList<Material> ores = new ArrayList<Material>(
			Arrays.asList(
					Material.COAL_ORE, Material.IRON_ORE, Material.LAPIS_ORE, Material.GOLD_ORE, 
					Material.REDSTONE_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.NETHER_QUARTZ_ORE));
	private HashMap<Material, Integer> oreValue;
	private HashMap<EntityType, Integer> entityValue;
	private ArrayList<Location> playerPlacedOres;
	
	private static Economy econ = null;
	private static final Logger log = Logger.getLogger("Minecraft");
	
	public void onEnable() {
		plugin = this;
		loadDefaultConfig();
		REGULAR_BREAK = this.getConfig().getBoolean("Ore.Settings.Regular Break");
		NO_PLAYER_PLACING_ORE_REWARD = this.getConfig().getBoolean("Ore.Settings.No Player placing Ore Reward");
		this.loadFromConfig();
		oreValue = new HashMap<Material, Integer>(ores.stream()
				.collect(Collectors
						.toMap(Function.identity(), i -> this.getConfig().getInt("Ore." + i.toString()))));
		entityValue = new HashMap<EntityType, Integer>(Stream.of(EntityType.values())
				.filter(et -> et.isAlive() && et != EntityType.ARMOR_STAND)
				.collect(Collectors
						.toMap(Function.identity(), i -> this.getConfig().getInt("Kill." + i.toString()))));
		new EntityDeathListener(this);
		new PlayerMineOreListener(this);
		
		if(!setupEconomy()) {
			log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
	}
	
	public void onDisable() {
		this.storeToConfig();
	}
	
	private void loadDefaultConfig() {
		this.getConfig().options().copyDefaults(true);
		this.getConfig().addDefault("Ore.Settings.Regular Break", true);
		this.getConfig().addDefault("Ore.Settings.No Player placing Ore Reward", true);
		this.getConfig().addDefaults(
				ores.stream()
				.collect(Collectors
						.toMap(o -> "Ore." + o.toString(), o -> 0, (o1, o2) -> o1, TreeMap::new)));	
		this.getConfig().addDefaults(
				Stream.of(EntityType.values())
				//(et1, et2) -> et1.toString().compareToIgnoreCase(et2.toString())
				.sorted(Comparator.comparing(EntityType::toString))
				.filter(et -> et.isAlive() && et != EntityType.ARMOR_STAND)
				.collect(Collectors
						.toMap(et -> "Kill." + et.toString(), i -> 0, (v1, v2) -> v1, TreeMap::new)));
		this.saveConfig();
	}
	
	public void saveCustomYml(FileConfiguration ymlConfig, File ymlFile) {
	   try {
		   ymlConfig.save(ymlFile);
	   } catch (IOException e) {
		   e.printStackTrace();
	   }
	}
	   
	private void storeToConfig() {
	   this.checkOrdner();
	   File file = new File(plugin.getDataFolder()+"", "placedOres.yml");
	   FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
	   cfg.set("Locations", this.getPlayerPlacedOres());	   
	   this.saveCustomYml(cfg, file);
	}
	   
	   @SuppressWarnings("unchecked")
	private void loadFromConfig() {
	   //only load this into memory when necessary
	   if(NO_PLAYER_PLACING_ORE_REWARD) {
		   this.checkOrdner();
		   File file = new File(plugin.getDataFolder()+"", "placedOres.yml");
		   FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		   this.playerPlacedOres = (cfg.getList("Locations") == null ? new ArrayList<Location>() : (ArrayList<Location>) cfg.getList("Locations"));
	   }
	}
	   
	public void checkOrdner() {
	   File file = new File(this.getDataFolder()+"");
	   if(!file.exists()) {
		   file.mkdir();
	   }
	}

	public HashMap<Material, Integer> getOreValue() {
		return oreValue;
	}

	public HashMap<EntityType, Integer> getEntityValue() {
		return entityValue;
	}

	public ArrayList<Location> getPlayerPlacedOres() {
		return playerPlacedOres;
	}
	
	public void addToPlayerPlacedOres(Location loc) {
		this.playerPlacedOres.add(loc);
	}
	
	public void removeFromPlayerPlacedOres(Location loc) {
		this.playerPlacedOres.remove(loc);
	}
	
	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
	
	public static Economy getEcon() {
		return econ;
	}
}
