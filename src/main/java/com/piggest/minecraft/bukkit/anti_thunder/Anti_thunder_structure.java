package com.piggest.minecraft.bukkit.anti_thunder;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Anti_thunder_structure extends Structure {
	private String owner;
	private boolean active = false;

	public Anti_thunder_structure(String world_name, int x, int y, int z) {
		super(world_name, x, y, z);
	}

	@Override
	public boolean completed() {
		Location core_loc = this.get_core_location();
		int i = 0;
		int j = 0;
		if (core_loc.getBlock().getType() != Material.PISTON) {
			return false;
		}
		Location check_loc = core_loc.clone();
		check_loc.setY(core_loc.getY() + 1);
		for (i = 1; i <= 5; i++) {
			Block pole_block = check_loc.add(0, 1, 0).getBlock();
			if (pole_block.getType() != Material.END_ROD) {
				return false;
			}
		}
		check_loc.setY(core_loc.getY() - 1);
		for (i = -1; i <= 1; i++) {
			check_loc.setZ(core_loc.getZ() + i);
			for (j = -1; j <= 1; j++) {
				check_loc.setX(core_loc.getX() + j);
				Block pole_block = check_loc.getBlock();
				if (pole_block.getType() != Material.IRON_BLOCK) {
					return false;
				}
			}
		}
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
