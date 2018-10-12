package com.piggest.minecraft.bukkit.anti_thunder;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
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
		if (event.isCancelled() == true) {
			return;
		}
		Chunk_location chunk_loc = Chunk_location.new_location(event.getLightning().getLocation().getChunk());
		plugin.getLogger().info("区块" + chunk_loc + "发生雷击");
		Anti_thunder_structure structure = plugin.get_structure_manager().structure_nearby(chunk_loc);
		if (structure != null) {
			plugin.getLogger().info("在雷击周围的3*3区块发现防雷器");
			if (structure.completed() == false) {
				plugin.getLogger().info("区块" + structure.get_chunk_location() + "的防雷器结构不完整，已经移除");
				plugin.get_structure_manager().remove_structure(structure);
				return;
			}
			if (structure.is_active() == true) {
				event.setCancelled(true);
				plugin.getLogger().info("已阻止雷击");
			} else {
				plugin.getLogger().info("防雷器未被激活，因此雷击未被阻止");
			}
		}
	}

	@EventHandler
	public void on_powered(BlockPistonExtendEvent event) {
		if (event.isCancelled() == true) {
			return;
		}
		Chunk_location chunk_loc = Chunk_location.new_location(event.getBlock().getChunk());
		Anti_thunder_structure structure = plugin.get_structure_manager().get_anti_thunder_structure_map()
				.get(chunk_loc);
		if (structure != null) {
			if (structure.get_core_location().equals(event.getBlock().getLocation())) {
				if (structure.activate(true) == true) {
					structure.get_owner().sendMessage("区块" + chunk_loc + "的防雷器已经激活");
				} else {
					structure.get_owner().sendMessage("区块" + chunk_loc + "的防雷器激活失败");
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void on_dispowered(BlockPistonRetractEvent event) {
		if (event.isCancelled() == true) {
			return;
		}
		Chunk_location chunk_loc = Chunk_location.new_location(event.getBlock().getChunk());
		Anti_thunder_structure structure = plugin.get_structure_manager().get_anti_thunder_structure_map()
				.get(chunk_loc);
		if (structure != null) {
			if (structure.get_core_location().equals(event.getBlock().getLocation()) && structure.is_active() == true) {
				if (structure.completed() == true) {
					structure.activate(false);
					structure.get_owner().sendMessage("区块" + chunk_loc + "的防雷器已经暂停");
				} else {
					plugin.get_structure_manager().remove_structure(structure);
					structure.get_owner().sendMessage("区块" + chunk_loc + "的防雷器结构不完整，已经移除");
				}
			}
		}
	}

	@EventHandler
	public void on_put_piston(BlockPlaceEvent event) {
		Block placed_block = event.getBlockPlaced();
		if (placed_block.getType() == Material.PISTON && event.isCancelled() == false) {
			Anti_thunder_structure structure = new Anti_thunder_structure(plugin, placed_block.getWorld().getName(),
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

	@EventHandler
	public void on_break_piston(BlockBreakEvent event) {
		Block break_block = event.getBlock();
		if (break_block.getType() == Material.PISTON && event.isCancelled() == false) {
			Chunk_location chunk_loc = Chunk_location.new_location(break_block.getChunk());
			Anti_thunder_structure structure = plugin.get_structure_manager().get_anti_thunder_structure_map()
					.get(chunk_loc);
			if (structure != null) {
				Player player = event.getPlayer();
				player.sendMessage("区块" + chunk_loc + "的防雷器结构已被破坏");
				plugin.get_structure_manager().remove_structure(structure);
			}
		}
	}
}
