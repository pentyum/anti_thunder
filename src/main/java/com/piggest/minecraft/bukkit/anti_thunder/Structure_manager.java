package com.piggest.minecraft.bukkit.anti_thunder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;

public class Structure_manager {
	private Anti_thunder plugin = null;
	private HashMap<Chunk_location, Anti_thunder_structure> anti_thunder_structure_map = new HashMap<Chunk_location, Anti_thunder_structure>();

	public Structure_manager(Anti_thunder plugin) {
		this.plugin = plugin;
	}

	public boolean add_new_structure(Anti_thunder_structure structure) {
		Chunk_location chunk_loc = Chunk_location.new_location(structure.get_core_location().getChunk());
		if (this.anti_thunder_structure_map.get(chunk_loc) == null) {
			this.anti_thunder_structure_map.put(chunk_loc, structure);
			plugin.getLogger().info("区块" + chunk_loc + "的防雷器已经加载进内存");
			return true;
		} else {
			return false;
		}
	}

	public void load_anti_thunder_structure_map() {
		List<Map<?, ?>> structure_list = plugin.get_structure_config().getMapList("anti_thunder");
		for (Map<?, ?> one_structure : structure_list) {
			String world_name = (String) one_structure.get("world");
			String owner = (String) one_structure.get("owner");
			Boolean active = (Boolean) one_structure.get("active");
			Chunk_location chunk_loc = new Chunk_location(world_name, (Integer) one_structure.get("chunk-x"),
					(Integer) one_structure.get("chunk-z"));
			Anti_thunder_structure structure_to_load = new Anti_thunder_structure(world_name,
					(Integer) one_structure.get("x"), (Integer) one_structure.get("y"),
					(Integer) one_structure.get("z"));
			structure_to_load.set_owner(owner);
			structure_to_load.activate(active);
			plugin.getLogger().info("区块" + chunk_loc + "的雷电保护器已经从配置中加载");
			this.anti_thunder_structure_map.put(chunk_loc, structure_to_load);
		}
	}

	public void save_anti_thunder_structure_map() {
		ArrayList<HashMap<String, ?>> structure_list = new ArrayList<HashMap<String, ?>>();
		for (Entry<Chunk_location, Anti_thunder_structure> entry : anti_thunder_structure_map.entrySet()) {
			Chunk_location chunk_loc = entry.getKey();
			Anti_thunder_structure structure_to_save = entry.getValue();
			HashMap<String, Object> one_structure = new HashMap<String, Object>();
			one_structure.put("world", structure_to_save.get_world_name());
			one_structure.put("owner", structure_to_save.get_owner_name());
			one_structure.put("active", structure_to_save.is_active());
			one_structure.put("chunk-x", chunk_loc.x);
			one_structure.put("chunk-z", chunk_loc.z);
			one_structure.put("x", structure_to_save.get_core_location().getBlockX());
			one_structure.put("y", structure_to_save.get_core_location().getBlockY());
			one_structure.put("z", structure_to_save.get_core_location().getBlockZ());
			structure_list.add(one_structure);
			Bukkit.getLogger().info("区块" + chunk_loc + "的雷电保护器已经保存至配置中");
		}
		plugin.get_structure_config().set("anti_thunder", structure_list);
	}

	public void remove_structure(Anti_thunder_structure structure) {
		Chunk_location chunk_loc = Chunk_location.new_location(structure.get_core_block().getChunk());
		plugin.getLogger().info("内存中的区块" + chunk_loc + "的防雷器已经移除");
		this.anti_thunder_structure_map.remove(chunk_loc);
	}

	public HashMap<Chunk_location, Anti_thunder_structure> get_anti_thunder_structure_map() {
		return this.anti_thunder_structure_map;
	}

	public Anti_thunder_structure structure_nearby(Chunk_location chunk_loc) {
		int i;
		int j;
		Chunk_location check_loc = new Chunk_location(chunk_loc.world, chunk_loc.x, chunk_loc.z);
		for (i = -1; i <= 1; i++) {
			check_loc.x = chunk_loc.x + i;
			for (j = -1; j <= 1; j++) {
				check_loc.z = chunk_loc.z + j;
				Anti_thunder_structure structure = this.get_anti_thunder_structure_map().get(check_loc);
				if (this.get_anti_thunder_structure_map().get(check_loc) != null) {
					return structure;
				}
			}
		}
		return null;
	}
}
