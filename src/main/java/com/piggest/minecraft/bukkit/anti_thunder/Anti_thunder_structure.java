package com.piggest.minecraft.bukkit.anti_thunder;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Piston;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class Anti_thunder_structure extends Structure {
	private String owner;
	private boolean active = false;
	private Anti_thunder_runner runner = new Anti_thunder_runner(this);

	public Anti_thunder_structure(JavaPlugin plugin, String world_name, int x, int y, int z) {
		super(plugin, world_name, x, y, z);
	}

	public Anti_thunder_structure() {
		super();
	}

	public Anti_thunder get_plugin() {
		return (Anti_thunder) plugin;
	}

	@Override
	public int completed() {
		Location core_loc = this.get_core_location();
		int i = 0;
		int j = 0;
		Piston core_piston_data = this.get_core_piston();
		if (core_piston_data == null) {
			return 0;
		}
		if (core_piston_data.getFacing().getModY() != 1) {
			return 0;
		}
		Location check_loc = core_loc.clone();
		check_loc.setY(core_loc.getY() + 1);
		for (i = 1; i <= 5; i++) {
			Block pole_block = check_loc.add(0, 1, 0).getBlock();
			if (pole_block.getType() != Material.END_ROD) {
				return 0;
			}
		}
		check_loc.setY(core_loc.getY() - 1);
		for (i = -1; i <= 1; i++) {
			check_loc.setZ(core_loc.getZ() + i);
			for (j = -1; j <= 1; j++) {
				check_loc.setX(core_loc.getX() + j);
				Block pole_block = check_loc.getBlock();
				if (pole_block.getType() != Material.IRON_BLOCK) {
					return 0;
				}
			}
		}
		return 1;
	}

	public void set_owner(String owner) {
		this.owner = owner;
	}

	@SuppressWarnings("deprecation")
	public OfflinePlayer get_owner() {
		return Bukkit.getOfflinePlayer(owner);
	}

	public String get_owner_name() {
		return this.owner;
	}

	public boolean is_active() {
		return this.active;
	}

	public boolean activate(boolean active) {
		if (active == true) {
			if (this.completed() > 0) {
				if (runner.started() == false) {
					runner.start();
					plugin.getLogger().info("10秒后启动扣钱线程");
					runner.runTaskTimerAsynchronously(plugin, 10 * 20, get_plugin().get_cycle() * 20);
				} else {
					OfflinePlayer owner = this.get_owner();
					Economy economy = this.get_plugin().get_economy();
					int price = this.get_plugin().get_price();
					if (!economy.has(owner, price)) {
						this.send_msg_to_owner("你的钱不够，不能启动防雷器");
						active = false;
						return false;
					}
				}
			} else {
				this.get_plugin().get_structure_manager().remove_structure(this);
				this.send_msg_to_owner("区块" + get_chunk_location() + "的防雷器结构不完整，已经移除");
				return false;
			}
		}
		this.active = active;
		return true;
	}

	public void send_msg_to_owner(String msg) {
		if (this.get_owner().isOnline()) {
			this.get_owner().getPlayer().sendMessage(msg);
		} else {
			this.plugin.getLogger().info("[" + this.owner + "]" + msg);
		}
	}

	public Piston get_core_piston() {
		BlockData core_data = this.get_core_block().getBlockData();
		if (!(core_data instanceof Piston)) {
			return null;
		}
		return (Piston) core_data;
	}

	public void close() {
		if (this.runner.started() == true) {
			if (this.runner.isCancelled() == false) {
				plugin.getLogger().info("停止扣钱线程");
				this.runner.cancel();
			}
		}
	}

	public Chunk_location get_chunk_location() {
		return Chunk_location.new_location(this.get_core_location().getChunk());
	}

	@Override
	public HashMap<String, Object> get_save() {
		HashMap<String, Object> one_structure = new HashMap<String, Object>();
		one_structure.put("world", this.get_world_name());
		one_structure.put("owner", this.get_owner_name());
		one_structure.put("active", this.is_active());
		one_structure.put("chunk-x", this.get_core_location().getChunk().getX());
		one_structure.put("chunk-z", this.get_core_location().getChunk().getZ());
		one_structure.put("x", this.x);
		one_structure.put("y", this.y);
		one_structure.put("z", this.z);
		return one_structure;
	}

	@Override
	public void load_save(Map<?, ?> one_structure) {
		String world_name = (String) one_structure.get("world");
		String owner = (String) one_structure.get("owner");
		Boolean active = (Boolean) one_structure.get("active");
		this.world_name = world_name;
		this.x = (Integer) one_structure.get("x");
		this.y = (Integer) one_structure.get("y");
		this.z = (Integer) one_structure.get("z");
		this.set_owner(owner);
		this.activate(active);
	}

	@Override
	public Structure include_location(Location loc) {
		return this;
	}

}
