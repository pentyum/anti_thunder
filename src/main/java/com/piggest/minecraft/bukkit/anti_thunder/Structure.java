package com.piggest.minecraft.bukkit.anti_thunder;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Structure {
	protected JavaPlugin plugin;
	protected String world_name;
	protected int x;
	protected int y;
	protected int z;
	protected boolean loaded = false;
	protected String name = "结构";

	public Structure() {

	}

	public Structure(JavaPlugin plugin, String world_name, int x, int y, int z) {
		this.set_info(plugin, world_name, x, y, z);
	}

	public void set_info(JavaPlugin plugin, String world_name, int x, int y, int z) {
		this.plugin = plugin;
		this.world_name = world_name;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public String get_name() {
		if (this.name == "" || this.name == null) {
			return this.getClass().getName();
		} else {
			return this.name;
		}
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

	public String get_world_name() {
		return this.world_name;
	}

	public Location get_core_location() {
		return new Location(this.get_world(), this.x, this.y, this.z);
	}

	public Block get_core_block() {
		return get_core_location().getBlock();
	}

	public abstract boolean completed();

	public abstract void close();

	public Chunk_location get_chunk_location() {
		return Chunk_location.new_location(this.get_core_block().getChunk());
	}

	public HashMap<Chunk_location, Structure> get_map() {
		return ((Anti_thunder) this.plugin).get_structure_manager().get_structure_map(this.getClass().getName());
	}

	public abstract HashMap<String, Object> get_save();

	public abstract void load_save(Map<?, ?> one_structure);
}
