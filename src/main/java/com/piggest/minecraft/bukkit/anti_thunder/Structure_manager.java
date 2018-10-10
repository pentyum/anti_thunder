package com.piggest.minecraft.bukkit.anti_thunder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Structure_manager {
	private Anti_thunder plugin = null;
	private HashMap<Chunk_location, Anti_thunder_structure> anti_thunder_structure_map = new HashMap<Chunk_location, Anti_thunder_structure>();

	public Structure_manager(Anti_thunder plugin) {
		this.plugin = plugin;
	}

	public void load_anti_thunder_structure_map() {
		List<Map<?, ?>> structure_list = plugin.get_structure_config().getMapList("anti_thunder");
		for (Map<?, ?> one_structure : structure_list) {
			String world_name = (String) one_structure.get("world");
			String owner = (String) one_structure.get("owner");
			Boolean active = (Boolean) one_structure.get("active");
			HashMap<String, Integer> chunk_info = (HashMap<String, Integer>) one_structure.get("chunk");
			HashMap<String, Integer> location_info = (HashMap<String, Integer>) one_structure.get("location");
			Chunk_location chunk_loc = new Chunk_location(world_name, chunk_info.get("x"), chunk_info.get("z"));
			Anti_thunder_structure structure_to_load = new Anti_thunder_structure(world_name, location_info.get("x"),
					location_info.get("y"), location_info.get("z"));
			structure_to_load.set_owner(owner);
			structure_to_load.activate(active);
			plugin.getLogger().info("区块" + chunk_loc + "的雷电保护器已经加载");
			this.anti_thunder_structure_map.put(chunk_loc, structure_to_load);
		}
	}
	
	public void remove_structure(Anti_thunder_structure structure) {
		Chunk_location chunk_loc = Chunk_location.new_location(structure.get_core_block().getChunk());
		this.anti_thunder_structure_map.remove(chunk_loc);
	}
	
	public void save_structure() {
		
	}
	
	public HashMap<Chunk_location, Anti_thunder_structure> get_anti_thunder_structure_map() {
		return this.anti_thunder_structure_map;
	}
}
