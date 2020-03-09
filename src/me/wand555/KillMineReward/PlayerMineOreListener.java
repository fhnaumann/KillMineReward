package me.wand555.KillMineReward;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerMineOreListener implements Listener {
	
	private KillMineReward plugin;
	
	public PlayerMineOreListener(JavaPlugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = (KillMineReward) plugin;
	}
	
	@EventHandler
	public void onPlayerMineOreEvent(BlockBreakEvent event) {
		if(event.getPlayer().hasPermission("killmine.use")) {
			if(KillMineReward.ores.contains(event.getBlock().getType())) {
				//wenn es enabled ist
				if(KillMineReward.REGULAR_BREAK) {
					//wenn block auch tatsächlich dropt
					if(event.isDropItems()) {
						
						if(KillMineReward.NO_PLAYER_PLACING_ORE_REWARD) {
							if(!plugin.getPlayerPlacedOres().isEmpty()) {
								if(!plugin.getPlayerPlacedOres().contains(event.getBlock().getLocation())) {
									int amount = plugin.getOreValue().get(event.getBlock().getType());
									if(amount > 0) {
										KillMineReward.getEcon().depositPlayer(event.getPlayer(), amount);
									}			
								}
								else {
									plugin.removeFromPlayerPlacedOres(event.getBlock().getLocation());
								}
							}
							
						}
						
					}
				}
				//wenn es disabled ist
				else {
					
					if(KillMineReward.NO_PLAYER_PLACING_ORE_REWARD) {
						if(!plugin.getPlayerPlacedOres().isEmpty()) {
							if(!plugin.getPlayerPlacedOres().contains(event.getBlock().getLocation())) {
								int amount = plugin.getOreValue().get(event.getBlock().getType());
								if(amount > 0) {
									KillMineReward.getEcon().depositPlayer(event.getPlayer(), amount);
								}
							}
							else {
								plugin.removeFromPlayerPlacedOres(event.getBlock().getLocation());
							}
						}
					}
				}
			}
		}
	}
	
	
	
	@EventHandler
	public void onOrePlaceEvent(BlockPlaceEvent event) {
		if(KillMineReward.NO_PLAYER_PLACING_ORE_REWARD) {
			if(KillMineReward.ores.contains(event.getBlock().getType())) {
				plugin.addToPlayerPlacedOres(event.getBlock().getLocation());
			}
		}
	}
}
