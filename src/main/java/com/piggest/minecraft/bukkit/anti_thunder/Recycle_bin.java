package com.piggest.minecraft.bukkit.anti_thunder;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public class Recycle_bin extends Structure {
	private double stored_money = 0;
	private double limited_money = 10000;
	private double price = 0.2;

	public Recycle_bin() {
		super();
	}

	public Recycle_bin(Anti_thunder plugin, String world_name, int x, int y, int z) {
		super(plugin, world_name, x, y, z);
	}

	public double get_price() {
		return this.price;
	}

	public double get_stored_money() {
		return this.stored_money;
	}

	public double get_limited_money() {
		return this.limited_money;
	}

	@Override
	public int completed() {
		Block check_block = null;
		int x = -1;
		int y = 0;
		int z = -1;
		for (x = -1; x <= 1; x++) {
			for (z = -1; z <= 1; z++) {
				check_block = this.get_block(x, y, z);
				if (check_block.getType() != Material.DIAMOND_BLOCK) {
					return 0;
				}
			}
		}
		return 1;
	}

	@Override
	public void close() {
		// TODO 自动生成的方法存根

	}

	@Override
	public HashMap<String, Object> get_save() {
		HashMap<String, Object> one_structure = new HashMap<String, Object>();
		one_structure.put("world", this.get_world_name());
		one_structure.put("stored_money", this.stored_money);
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
		double stored_money = (Double) one_structure.get("stored_money");
		this.world_name = world_name;
		this.x = (Integer) one_structure.get("x");
		this.y = (Integer) one_structure.get("y");
		this.z = (Integer) one_structure.get("z");
		this.stored_money = stored_money;
	}

	@Override
	public Structure include_location(Location loc) {
		int relative_x = loc.getBlockX() - this.x;
		int relative_y = loc.getBlockY() - this.y;
		int relative_z = loc.getBlockZ() - this.z;
		//plugin.getLogger().info("rx="+relative_x+"ry="+relative_y+"rz="+relative_z);
		if (relative_y <= 2) {
			if (Math.abs(relative_x) <= 2 && Math.abs(relative_z) <= 2) {
				if (Math.abs(relative_x) == 2 && Math.abs(relative_z) == 2) {
					return null;
				} else {
					return this;
				}
			}
		}
		return null;
	}

	public double add_money(double money) {
		this.stored_money += money;
		if (this.stored_money > this.limited_money) {
			this.stored_money = this.limited_money;
		}
		return this.stored_money;
	}

	public Anti_thunder get_plugin() {
		return (Anti_thunder) plugin;
	}

	public double discharge_money(double money) {
		this.stored_money -= money;
		if (this.stored_money < 0) {
			this.stored_money = 0;
		}
		return this.stored_money;
	}

	public void recycle_entity(Entity entity) {
		int num = 1;
		if (entity instanceof Item) {
			Item item = (Item) entity;
			num = item.getItemStack().getAmount();
		}
		this.add_money(num * this.price);
	}

	public void discharge_all(Player player) {
		this.get_plugin().get_economy().depositPlayer(player, this.stored_money);
		this.discharge_money(this.stored_money);
		this.plugin.getLogger()
				.info(player.getName() + "从" + this.get_chunk_location() + "的回收器中回收了" + this.stored_money);
	}
}
