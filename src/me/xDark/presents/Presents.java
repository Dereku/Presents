package me.xDark.presents;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.xDark.presents.listener.BlockListener;
import me.xDark.presents.listener.ClickListener;
import net.milkbowl.vault.economy.Economy;

public class Presents extends JavaPlugin implements Listener {

	public Presents() {
		instance = this;
	}

	private static final BlockListener BLOCK_LISTENER = new BlockListener();
	private static final ClickListener CLICK_LISTENER = new ClickListener();
	public static Presents instance;

	public static Economy economy;

	private HashMap<String, Statistic> statistics = new HashMap<>();

	private final File data = new File(getDataFolder(), "data.yml");
	private static YamlConfiguration dataYML;

	public static final Random RANDOM = new Random();
	
	private static final StringBuilder BUILDER = new StringBuilder();
	
	private static String locationToString(Location loc) {
		BUILDER.delete(0, BUILDER.length());
		BUILDER.append(loc.getWorld().getName()).append(";").append(loc.getBlockX()).append(";").append(loc.getBlockY()).append(";").append(loc.getBlockZ());
		return BUILDER.toString();
	}
	
	private static Location fromString(String loc) {
		String[] split = loc.split(";");
		String world = split[0];
		int x = Integer.parseInt(split[1]);
		int y = Integer.parseInt(split[2]);
		int z = Integer.parseInt(split[3]);
		return new Location(Bukkit.getWorld(world), x, y, z);
	}

	@Override
	public void onEnable() {
		saveDefaultConfig();
		Settings.update();
		if (Settings.USE_ECONOMY && !setupEconomy()) {
			getLogger().log(Level.SEVERE,
					"Unable to startup. Economy plugin was not found or Vault startup was not successful");
			setEnabled(false);
			return;
		}
		getDataFolder().mkdir();
		if (!data.isFile())
			try {
				data.createNewFile();
			} catch (IOException e) {
				getLogger().log(Level.SEVERE, "Error creating data file", e);
				setEnabled(false);
				return;
			}
		dataYML = YamlConfiguration.loadConfiguration(data);
		readData();
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(BLOCK_LISTENER, this);
		getServer().getPluginManager().registerEvents(CLICK_LISTENER, this);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		String str = e.getPlayer().getName().toLowerCase();
		if (!statistics.containsKey(str))
			statistics.put(str, new Statistic(false, new HashSet<Location>(), 0, null));
	}

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String str, String[] args) {
		if (!s.hasPermission("present.editmode"))
			return true;
		if (s.equals(Bukkit.getConsoleSender()))
			return true;
		Statistic statistic = statistics.get(s.getName().toLowerCase());
		statistic.setEditMode(!statistic.editMode());
		s.sendMessage(String.valueOf(statistic.editMode()));
		return true;
	}

	@Override
	public void onDisable() {
		if (economy != null)
			writeData();
		statistics.clear();
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null)
			return false;
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null)
			return false;
		return ((economy = rsp.getProvider()) != null);
	}

	private static Statistic deserializeStatistic(HashMap<Object, Object> map) {
		boolean foundAll = (boolean) map.get("foundAll");
		HashSet<Location> foundLocations = (HashSet<Location>) map.get("fountPresents");
		int found = (int) map.get("timesFound");
		Date date = null;
		try {
			date = SIMPLE_DATE_FORMAT.parse(String.valueOf(map.get("findDate")));
		} catch (ParseException e) {
		}
		map.clear();
		return new Statistic(foundAll, foundLocations, found, date);
	}

	private void readData() {
		for (String obj : dataYML.getKeys(false)) {
			HashMap<Object, Object> map = new HashMap<>();
			map.put("foundAll", dataYML.getBoolean(obj + ".foundAll"));
			map.put("timesFound", dataYML.getInt(obj + ".timesFound"));
			map.put("fountPresents", new HashSet<Location>());
			for (String s : dataYML.getConfigurationSection(obj).getKeys(false)) {
				if (s.startsWith("foundPresent"))
					((HashSet<Location>) map.get("fountPresents")).add(fromString(dataYML.getString(obj + "." + s)));
			}
			statistics.put(obj, deserializeStatistic(map));
		}
	}

	private void writeData() {
		data.delete();
		try {
			data.createNewFile();
		} catch (IOException e) {
		}
		dataYML = YamlConfiguration.loadConfiguration(data);
		for (Map.Entry<String, Statistic> entry : statistics.entrySet()) {
			String player = entry.getKey();
			Statistic statistic = entry.getValue();
			dataYML.set(player, serialize(statistic));
			saveData();
		}
	}

	private void saveData() {
		try {
			dataYML.save(data);
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "Error while saving data", e);
			setEnabled(false);
		}
	}

	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	private static HashMap<Object, Object> serialize(Statistic statistic) {
		HashMap<Object, Object> map = new HashMap<>();
		map.put("foundAll", statistic.foundAll());
		map.put("timesFound", statistic.timesFound);
		map.put("findDate", statistic.findDate == null ? "null" : SIMPLE_DATE_FORMAT.format(statistic.findDate));
		for (Location location : statistic.getFoundPresents())
			map.put("foundPresents-" + RANDOM.nextInt(Integer.MAX_VALUE), locationToString(location));
		return map;
	}

	public HashMap<String, Statistic> getStatistics() {
		return statistics;
	}
}
