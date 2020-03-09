package me.wand555.KillMineReward;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class EntityDeathListener implements Listener {

	private KillMineReward plugin;
	
	public EntityDeathListener(JavaPlugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = (KillMineReward) plugin;
	}
	
	@EventHandler
	public void onEntityDeathEvent(EntityDeathEvent event) {	
		Player p = event.getEntity().getKiller();
		if(p != null) {
			if(p.hasPermission("killmine.use")) {
				int amount = plugin.getEntityValue().get(event.getEntityType());
				if(amount > 0) {
					KillMineReward.getEcon().depositPlayer(p, amount);
				}
			}
		}	
	}
}
