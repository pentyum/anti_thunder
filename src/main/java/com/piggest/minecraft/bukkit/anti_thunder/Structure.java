package com.piggest.minecraft.bukkit.anti_thunder;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public abstract class Structure {
	private String world_name;
	private int x;
	private int y;
	private int z;
	private boolean loaded = false;

	public Structure(String world_name, int x, int y, int z) {
		this.world_name = world_name;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public boolean is_loaded() {
		return this.loaded;
	}

	public void set_loaded(boolean load) {
		this.loaded = load;
	}

	public World get_world() {
		return Bukkit.getWorld(world_name);
	}

	public Location get_core_location() {
		return new Location(this.get_world(), this.x, this.y, this.z);
	}

	public Block get_core_block() {
		return get_core_location().getBlock();
	}

	public abstract boolean completed();
}