package com.piggest.minecraft.bukkit.anti_thunder;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.milkbowl.vault.economy.Economy;

public class Anti_thunder_runner extends BukkitRunnable {
	private Anti_thunder_structure structure;
	private boolean started = false;

	public Anti_thunder_runner(Anti_thunder_structure structure) {
		this.structure = structure;
	}

	public void start() {
		this.started = true;
	}

	public void run() {
		Player owner = structure.get_owner();
		if (structure.completed() == true) {
			if (structure.is_active() == true) {
				Economy economy = structure.get_plugin().get_economy();
				int price = structure.get_plugin().get_price();
				if (economy.has(owner, price)) {
					economy.withdrawPlayer(owner, price);
					owner.sendMessage("已扣除" + price);
				} else {
					owner.sendMessage("金钱不够，防雷器已经暂停");
					structure.activate(false);
				}
			}
		} else {
			structure.get_plugin().get_structure_manager().remove_structure(structure);
			owner.sendMessage("区块" + structure.get_chunk_location() + "的防雷器结构不完整，已经移除");
		}
	}

	public boolean started() {
		return this.started;
	}
}
