package com.piggest.minecraft.bukkit.anti_thunder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;

public class Structure_manager {
	private Anti_thunder plugin = null;
	private HashMap<String, HashMap<Chunk_location, Structure>> structure_type_map = new HashMap<String, HashMap<Chunk_location, Structure>>();

	public Structure_manager(Anti_thunder plugin) {
		this.plugin = plugin;
	}

	public boolean add_new_structure(Structure structure) {
		Chunk_location chunk_loc = structure.get_chunk_location();
		String structure_class_name = structure.getClass().getName();
		HashMap<Chunk_location, Structure> structures_map = structure.get_map();
		if (structures_map == null) {
			structures_map = new HashMap<Chunk_location, Structure>();
			structures_map.put(chunk_loc, structure);
			this.structure_type_map.put(structure_class_name, structures_map);
			plugin.getLogger().info("区块" + chunk_loc + "的" + structure.get_name() + "已经加载进内存");
			return true;
		} else {
			if (structures_map.get(chunk_loc) == null) {
				structures_map.put(chunk_loc, structure);
				return true;
			} else {
				return false;
			}
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
			Anti_thunder_structure structure_to_load = new Anti_thunder_structure(plugin, world_name,
					(Integer) one_structure.get("x"), (Integer) one_structure.get("y"),
					(Integer) one_structure.get("z"));
			structure_to_load.set_owner(owner);
			structure_to_load.activate(active);
			plugin.getLogger().info("区块" + chunk_loc + "的" + structure_to_load.get_name() + "已经从配置中加载");
			this.add_new_structure(structure_to_load);
		}
	}

	public void save_structure_map() {
		ArrayList<HashMap<String, ?>> structure_list = new ArrayList<HashMap<String, ?>>();
		for (Entry<String, HashMap<Chunk_location, Structure>> entry : this.structure_type_map.entrySet()) {
			String structure_class_name = entry.getKey();
			HashMap<Chunk_location, Structure> structures_map = entry.getValue();
			for (Entry<Chunk_location, Structure> entry2 : structures_map.entrySet()) {
				Chunk_location chunk_loc = entry2.getKey();
				Structure structure = entry2.getValue();
				HashMap<String, Object> one_structure = structure.get_save();
				structure.close();
				structure_list.add(one_structure);
				Bukkit.getLogger().info("区块" + chunk_loc + "的" + structure.get_name() + "已经保存至配置中");
			}
			plugin.get_structure_config().set(structure_class_name, structure_list);
		}
	}

	public void remove_structure(Structure structure) {
		Chunk_location chunk_loc = structure.get_chunk_location();
		HashMap<Chunk_location, Structure> structure_map = structure.get_map();
		structure.close();
		structure_map.remove(chunk_loc);
		plugin.getLogger().info("内存中的区块" + chunk_loc + "的" + structure.get_name() + "已经移除");
	}

	public HashMap<String, HashMap<Chunk_location, Structure>> get_structure_type_map() {
		return this.structure_type_map;
	}

	public Structure structure_nearby(Chunk_location chunk_loc,String structure_class_name) {
		int i;
		int j;
		Chunk_location check_loc = new Chunk_location(chunk_loc.world, chunk_loc.x, chunk_loc.z);
		HashMap<Chunk_location, Structure> structure_map = this.structure_type_map.get(structure_class_name);
		for (i = -1; i <= 1; i++) {
			check_loc.x = chunk_loc.x + i;
			for (j = -1; j <= 1; j++) {
				check_loc.z = chunk_loc.z + j;
				Structure structure = structure_map.get(check_loc);
				if (structure_map.get(check_loc) != null) {
					return structure;
				}
			}
		}
		return null;
	}

	public HashMap<Chunk_location, Structure> get_structure_map(String name) {
		return this.structure_type_map.get(name);
	}
}
