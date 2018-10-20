package com.piggest.minecraft.bukkit.anti_thunder;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Recycle_listener implements Listener {
	private Anti_thunder plugin = null;

	public Recycle_listener(Anti_thunder anti_thunder_plugin) {
		this.plugin = anti_thunder_plugin;
	}

	@EventHandler
	public void on_recycle_entity(EntityDamageEvent event) {
		Structure structure = plugin.get_structure_manager().in_structure(event.getEntity().getLocation(),
				Recycle_bin.class);
		plugin.getLogger().info("有实体被破坏了");
		if (structure != null) {
			plugin.getLogger().info("实体在有回收器被破坏了");
			Recycle_bin recycle_bin = (Recycle_bin) structure;
			if (recycle_bin.completed() > 0) {
				plugin.getLogger().info("实体被回收了");
				recycle_bin.recycle_entity(event.getEntity());
			} else {
				plugin.get_structure_manager().remove_structure(recycle_bin);
			}
		}
	}
	
	@EventHandler
	public void on_discharge(PlayerInteractEvent event) {
		if (event.isCancelled() == false
				&& (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK)
				&& event.getItem() == null) {
			Structure structure = plugin.get_structure_manager().in_structure(event.getClickedBlock().getLocation(),
					Recycle_bin.class);
			if (structure != null) {
				Recycle_bin recycle_bin = (Recycle_bin) structure;
				Player player = event.getPlayer();
				if (recycle_bin.completed() > 0) {
					if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
						player.sendMessage("已回收" + recycle_bin.get_stored_money());
						recycle_bin.discharge_all(player);
					} else {
						player.sendMessage("当前已储存" + recycle_bin.get_stored_money());
					}
				} else {
					player.sendMessage("区块" + recycle_bin.get_chunk_location() + "的回收器结构已被破坏");
					plugin.get_structure_manager().remove_structure(recycle_bin);
				}
			}
		}
	}

	@EventHandler
	public void on_build(PlayerInteractEvent event) {
		if (event.isCancelled() == false && event.getAction() == Action.LEFT_CLICK_BLOCK && event.getItem() == null) {
			Block clicked_block = event.getClickedBlock();
			if (event.getClickedBlock().getType() == Material.DIAMOND_BLOCK) {
				Recycle_bin recycle_bin = new Recycle_bin(plugin, clicked_block.getWorld().getName(),
						clicked_block.getX(), clicked_block.getY(), clicked_block.getZ());
				if (recycle_bin.completed() > 0) {
					if (plugin.get_structure_manager().add_new_structure(recycle_bin) == true) {
						event.getPlayer().sendMessage("区块" + recycle_bin.get_chunk_location() + "的回收器已经建成");
					}
				}
			}
		}
	}
}
