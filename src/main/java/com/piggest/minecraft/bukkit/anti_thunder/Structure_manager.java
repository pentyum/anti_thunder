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
		this.register_structure_class(Anti_thunder_structure.class);
	}

	public boolean add_new_structure(Structure structure) {
		Chunk_location chunk_loc = structure.get_chunk_location();
		HashMap<Chunk_location, Structure> structures_map = structure.get_map();
		if (structures_map == null) {
			structures_map = register_structure_class(structure.getClass());
			structures_map.put(chunk_loc, structure);
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
	
	public HashMap<Chunk_location, Structure> register_structure_class(Class<? extends Structure> structure_class) {
		HashMap<Chunk_location, Structure> structures_map = new HashMap<Chunk_location, Structure>();
		this.structure_type_map.put(structure_class.getName(), structures_map);
		plugin.getLogger().info("已注册"+structure_class.getName()+"结构");
		return structures_map;
	}
	
	public void load_structure_map() {
		for (Entry<String, HashMap<Chunk_location, Structure>> entry : this.structure_type_map.entrySet()) {
			String structure_class_name = entry.getKey();
			Structure structure_to_load = null;
			try {
				@SuppressWarnings("unchecked")
				Class<? extends Structure> structure_class = (Class<? extends Structure>) Class.forName(structure_class_name);
				plugin.getLogger().info("已获取"+structure_class_name+"的类");
				structure_to_load = structure_class.newInstance();
				plugin.getLogger().info("已创建"+structure_class_name+"的实例");
				structure_to_load.set_info(plugin, "world", 0, 0, 0);
				plugin.getLogger().info("正在从"+structure_class_name+"的配置文件中加载数据");
				List<Map<?, ?>> structure_list = plugin.get_structure_config().getMapList(structure_class_name);
				for (Map<?, ?> one_structure : structure_list) {
					structure_to_load.load_save(one_structure);
					plugin.getLogger().info("区块" + structure_to_load.get_chunk_location() + "的" + structure_to_load.get_name() + "已经从配置中加载");
					this.add_new_structure(structure_to_load);
				}
			} catch (ClassNotFoundException e) {
				plugin.getLogger().severe(structure_class_name+"必须继承Structure类");
				break;
			} catch (InstantiationException e) {
				plugin.getLogger().severe(structure_class_name+"实例生成错误");
				break;
			} catch (IllegalAccessException e) {
				plugin.getLogger().severe(structure_class_name+"实例生成器无法访问");
				break;
			}
			
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
