package com.piggest.minecraft.bukkit.anti_thunder;

import org.bukkit.Chunk;

public class Chunk_location {
	public String world;
	public int x;
	public int z;

	public Chunk_location(String world, int x, int z) {
		this.world = world;
		this.x = x;
		this.z = z;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Chunk_location) {
			Chunk_location loc2 = (Chunk_location) obj;
			if (this.world.equals(loc2.world) && this.x == loc2.x && this.z == loc2.z) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.x * 10 + this.z;
	}

	@Override
	public String toString() {
		return "(" + this.world + "," + this.x + "," + this.z + ")";
	}
	
	public static Chunk_location new_location(Chunk chunk) {
		return new Chunk_location(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
	}
}
