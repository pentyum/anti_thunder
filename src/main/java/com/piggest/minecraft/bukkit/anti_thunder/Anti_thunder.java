package com.piggest.minecraft.bukkit.anti_thunder;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class Anti_thunder extends JavaPlugin {
	private boolean use_vault = true;
	private Economy economy = null;
	private int price = 0;
	private FileConfiguration config = null;
	private FileConfiguration structure_config = null;
	private File structure_file = null;
	private final Load_structure_listener load_structure_listener = new Load_structure_listener(this);
	private final Anti_thunder_listener anti_structure_listener = new Anti_thunder_listener(this);
	private final Recycle_listener recycle_listener = new Recycle_listener(this);
	private Structure_manager structure_manager = null;
	private int cycle = 3600;

	public FileConfiguration get_structure_config() {
		return this.structure_config;
	}

	public Economy get_economy() {
		return this.economy;
	}

	private boolean initVault() {
		boolean hasNull = false;
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			if ((economy = economyProvider.getProvider()) == null) {
				hasNull = true;
			}
		}
		return !hasNull;
	}

	@Override
	public void onEnable() {
		saveDefaultConfig();
		saveResource("structure.yml", false);
		this.config = getConfig();
		this.use_vault = config.getBoolean("use-vault");
		this.price = config.getInt("price");
		this.cycle = config.getInt("cycle");
		this.structure_file = new File(this.getDataFolder(), "structure.yml");
		this.structure_config = YamlConfiguration.loadConfiguration(structure_file);
		this.structure_manager = new Structure_manager(this);

		if (use_vault == true) {
			getLogger().info("使用Vault");
			if (!initVault()) {
				getLogger().severe("初始化Vault失败,请检测是否已经安装Vault插件和经济插件");
				return;
			}
		} else {
			getLogger().info("不使用Vault");
		}
		structure_manager.load_structure_map();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(load_structure_listener, this);
		pm.registerEvents(anti_structure_listener, this);
		pm.registerEvents(recycle_listener, this);
	}

	@Override
	public void onDisable() {
		structure_manager.save_structure_map();
		try {
			structure_config.save(this.structure_file);
		} catch (IOException e) {
			getLogger().severe("结构文件保存错误!");
		}
	}
	
	public Structure_manager get_structure_manager() {
		return this.structure_manager;
	}

	public int get_price() {
		return this.price;
	}

	public int get_cycle() {
		return this.cycle;
	}
}
