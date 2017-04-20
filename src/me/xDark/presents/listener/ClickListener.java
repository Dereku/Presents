package me.xDark.presents.listener;

import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.xDark.presents.Presents;
import me.xDark.presents.Settings;
import me.xDark.presents.Statistic;

public class ClickListener implements Listener {
	@EventHandler
	public void onPlace(PlayerInteractEvent e) {
		if (e.getAction() != Action.LEFT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		Block b = e.getClickedBlock();
		if (b.getType() != Material.SKULL)
			return;
		if (!((Skull) b.getState()).getOwner().equalsIgnoreCase(Settings.SKULL_OWNER))
			return;
		Player p = e.getPlayer();
		Statistic statistic = Presents.instance.getStatistics().get(p.getName().toLowerCase());
		if (statistic.editMode())
			return;
		if (statistic.foundAll())
			return;
		if (Settings.USE_DATES && !statistic.hasPassed(new Date(), Settings.DAYS_PASSED)) {
			p.sendMessage(Settings.MUST_WAIT.replace("%number", String.valueOf(Settings.DAYS_PASSED)));
			return;
		}
		if (statistic.getFoundPresents().contains(b.getLocation())) {
			p.sendMessage(Settings.ALREADY_FOUND);
			return;
		}
		statistic.getFoundPresents().add(b.getLocation());
		if (statistic.getFoundPresents().size() != Settings.MAX_PRESENTS)
			p.sendMessage(
					Settings.FOUND_PRESENT.replace("%current", String.valueOf(statistic.getFoundPresents().size()))
							.replace("%max", String.valueOf(Settings.MAX_PRESENTS)));
		else {
			if (Settings.USE_DATES)
				statistic.findDate = new Date();
			if (Settings.RUN_COMMAND)
				for (String s : Settings.COMMANDS_TO_RUN)
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
							s.replace('&', '§').replace("%name", p.getName()));
			statistic.timesFound++;
			double money = -1;
			if (Settings.USE_ECONOMY) {
				money = !Settings.ALLOW_REUSE ? Settings.MONEY_TO_GIVE
						: Settings.MONEY_TO_GIVE + (statistic.timesFound * Settings.MONEY_INCREMENT);
				Presents.economy.depositPlayer(p.getName(), money);
			}
			p.sendMessage(Settings.FOUND_ALL.replace("%money", String.valueOf(money)));
			if (Settings.ITEMS_TO_GIVE != null) {
				p.getInventory()
						.addItem(Settings.RANDOM_PRESENT
								? new ItemStack[] {
										Settings.ITEMS_TO_GIVE[Presents.RANDOM.nextInt(Settings.ITEMS_TO_GIVE.length)] }
								: Settings.ITEMS_TO_GIVE);
				statistic.getFoundPresents().clear();
				if (!Settings.ALLOW_REUSE)
					statistic.setFoundAll(true);
			}
		}
	}
}