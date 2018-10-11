package com.piggest.minecraft.bukkit.anti_thunder;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
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

	@EventHandler
	public void on_put_piston(BlockPlaceEvent event) {
		Block placed_block = event.getBlockPlaced();
		if (placed_block.getType() == Material.PISTON && event.isCancelled() == false) {
			Anti_thunder_structure structure = new Anti_thunder_structure(placed_block.getWorld().getName(),
					placed_block.getX(), placed_block.getY(), placed_block.getZ());
			if (structure.completed() == true) {
				Player player = event.getPlayer();
				structure.set_owner(player.getName());
				structure.set_loaded(true);
				Chunk_location chunk_loc = Chunk_location.new_location(placed_block.getChunk());
				if (plugin.get_structure_manager().add_new_structure(structure) == true) {
					player.sendMessage("区块" + chunk_loc + "防雷器搭建成功，激活活塞即可启动");
				} else {
					player.sendMessage("区块" + chunk_loc + "已经有防雷器了，因此这个防雷器不会生效");
				}
			}
		}
	}
}
