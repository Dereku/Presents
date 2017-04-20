package me.xDark.presents.listener;

import org.bukkit.Material;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import me.xDark.presents.Presents;
import me.xDark.presents.Settings;
import me.xDark.presents.Statistic;

public class BlockListener implements Listener {
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		if (e.getBlock().getType() != Material.SKULL)
			return;
		Player p = e.getPlayer();
		Statistic statistic = Presents.instance.getStatistics().get(p.getName().toLowerCase());
		if (!statistic.editMode())
			return;
		Skull skull = (Skull) e.getBlock().getState();
		skull.setOwner(Settings.SKULL_OWNER);
		skull.update();
		p.sendMessage(Settings.PRESENT_PLACED);
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if (e.getBlock().getType() != Material.SKULL)
			return;
		Player p = e.getPlayer();
		Statistic statistic = Presents.instance.getStatistics().get(p.getName().toLowerCase());
		if (statistic.editMode()) {
			p.sendMessage(Settings.PRESENT_BROKEN);
		} else
			e.setCancelled(true);
	}
}
