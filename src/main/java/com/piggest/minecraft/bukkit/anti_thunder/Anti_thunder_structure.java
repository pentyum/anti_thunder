package com.piggest.minecraft.bukkit.anti_thunder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Anti_thunder_structure extends Structure {
	private String owner;
	private boolean active = false;

	public Anti_thunder_structure(String world_name, int x, int y, int z) {
		super(world_name, x, y, z);
	}

	@Override
	public boolean completed() {
		return true;
	}

	public void set_owner(String owner) {
		this.owner = owner;
	}

	public Player get_owner() {
		return Bukkit.getPlayer(owner);
	}

	public boolean is_active() {
		return this.active;
	}

	public void activate(boolean active) {
		this.active = active;
	}
}
