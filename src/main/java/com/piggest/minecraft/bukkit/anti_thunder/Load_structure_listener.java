package com.piggest.minecraft.bukkit.anti_thunder;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class Load_structure_listener implements Listener {
	private Anti_thunder plugin = null;

	public Load_structure_listener(Anti_thunder anti_thunder_plugin) {
		this.plugin = anti_thunder_plugin;
	}

	@EventHandler
	public void on_chunk_load(ChunkLoadEvent event) {
		Chunk_location chunk_loc = Chunk_location.new_location(event.getChunk());
		Structure structure = plugin.get_structure_manager().get_anti_thunder_structure_map().get(chunk_loc);
		if (structure != null) {
			structure.set_loaded(true);
			plugin.getLogger().info("区块" + chunk_loc + "的防雷器已经加载");
		}
	}

	@EventHandler
	public void on_chunk_unload(ChunkUnloadEvent event) {
		Chunk_location chunk_loc = Chunk_location.new_location(event.getChunk());
		Structure structure = plugin.get_structure_manager().get_anti_thunder_structure_map().get(chunk_loc);
		if (structure != null) {
			structure.set_loaded(false);
			plugin.getLogger().info("区块" + chunk_loc + "的防雷器已经卸载");
		}
	}
}
