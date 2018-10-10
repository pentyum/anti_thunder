package com.piggest.minecraft.bukkit.anti_thunder;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.weather.LightningStrikeEvent;

public class Anti_thunder_listener implements Listener {
	private Anti_thunder plugin = null;

	public Anti_thunder_listener(Anti_thunder anti_thunder_plugin) {
		this.plugin = anti_thunder_plugin;
	}

	@EventHandler
	public void on_thunder(LightningStrikeEvent event) {
		Chunk_location chunk_loc = Chunk_location.new_location(event.getLightning().getLocation().getChunk());
		plugin.getLogger().info("区块" + chunk_loc + "发生雷击");
		Anti_thunder_structure structure = plugin.get_structure_manager().get_anti_thunder_structure_map()
				.get(chunk_loc);
		if (structure != null) {
			plugin.getLogger().info("在当前区块发现防雷器");
			if (structure.is_active() == true) {
				event.setCancelled(true);
				plugin.getLogger().info("已阻止雷击");
			}
		}
	}

	@EventHandler
	public void on_powered(BlockPistonExtendEvent event) {
		Chunk_location chunk_loc = Chunk_location.new_location(event.getBlock().getChunk());
		Anti_thunder_structure structure = plugin.get_structure_manager().get_anti_thunder_structure_map()
				.get(chunk_loc);
		if (structure != null) {
			if (structure.get_core_location().equals(event.getBlock().getLocation())) {
				if (structure.completed() == true) {
					structure.activate(true);
					structure.get_owner().sendMessage("区块" + chunk_loc + "的防雷器已经激活");
				} else {
					plugin.get_structure_manager().remove_structure(structure);
				}
			}
		}
	}
	
	@EventHandler
	public void on_dispowered(BlockPistonRetractEvent event) {
		Chunk_location chunk_loc = Chunk_location.new_location(event.getBlock().getChunk());
		Anti_thunder_structure structure = plugin.get_structure_manager().get_anti_thunder_structure_map()
				.get(chunk_loc);
		if (structure != null) {
			if (structure.get_core_location().equals(event.getBlock().getLocation())) {
				if (structure.completed() == true) {
					structure.activate(false);
					structure.get_owner().sendMessage("区块" + chunk_loc + "的防雷器已经暂停");
				} else {
					plugin.get_structure_manager().remove_structure(structure);
				}
			}
		}
	}
}
