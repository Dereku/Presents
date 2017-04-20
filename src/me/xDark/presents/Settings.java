package me.xDark.presents;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.inventory.ItemStack;

public final class Settings {
	public static int DAYS_PASSED;

	public static String SKULL_OWNER;

	public static String FOUND_ALL;
	public static String ALREADY_FOUND;

	public static String PRESENT_PLACED;
	public static String PRESENT_BROKEN;
	public static String FOUND_PRESENT;

	public static boolean ALLOW_REUSE;
	public static boolean RUN_COMMAND;
	public static boolean RANDOM_PRESENT;
	public static boolean USE_ECONOMY;

	public static ArrayList<String> COMMANDS_TO_RUN;

	public static ItemStack[] ITEMS_TO_GIVE;

	public static int MAX_PRESENTS;
	public static double MONEY_INCREMENT;
	public static double MONEY_TO_GIVE;

	public static boolean USE_DATES;
	
	public static String MUST_WAIT;

	public static void update() {

		SKULL_OWNER = Presents.instance.getConfig().getString("settings.skull-owner");
		FOUND_ALL = Presents.instance.getConfig().getString("messages.found-all").replace('&', '§');
		ALREADY_FOUND = Presents.instance.getConfig().getString("messages.already-found").replace('&', '§');
		PRESENT_PLACED = Presents.instance.getConfig().getString("messages.present-placed").replace('&', '§');
		PRESENT_BROKEN = Presents.instance.getConfig().getString("messages.present-broken").replace('&', '§');
		FOUND_PRESENT = Presents.instance.getConfig().getString("messages.present-found").replace('&', '§');
		MUST_WAIT = Presents.instance.getConfig().getString("messages.days-not-passed").replace('&', '§');
		
		ALLOW_REUSE = Presents.instance.getConfig().getBoolean("settings.allow-reuse");
		USE_DATES = Presents.instance.getConfig().getBoolean("settings.use-dates");
		List<String> commands = Presents.instance.getConfig().getStringList("commands");
		int size = commands.size();
		RUN_COMMAND = (size > 0);
		if (RUN_COMMAND)
			COMMANDS_TO_RUN = (ArrayList<String>) commands;
		RANDOM_PRESENT = Presents.instance.getConfig().getBoolean("settings.random-items");
		MAX_PRESENTS = Presents.instance.getConfig().getInt("settings.max-presents");
		DAYS_PASSED = Presents.instance.getConfig().getInt("settings.days-passed");
		USE_ECONOMY = Presents.instance.getConfig().getBoolean("settings.use-economy");
		MONEY_TO_GIVE = Presents.instance.getConfig().getDouble("settings.money-reward");
		MONEY_INCREMENT = Presents.instance.getConfig().getDouble("settings.money-increment");
		List<String> items = Presents.instance.getConfig().getStringList("items");
		size = items.size();
		if (size > 0) {
			ITEMS_TO_GIVE = new ItemStack[size];
			for (int i = 0; i < size; i++) {
				String s = items.get(i);
				try {
					String[] split = s.split(",");
					int id = Integer.parseInt(split[0]);
					int amount = Integer.parseInt(split[1]);
					ITEMS_TO_GIVE[i] = new ItemStack(id, amount);
				} catch (Exception ex) {
					Presents.instance.getLogger().log(Level.SEVERE, "Unable to parse itemstack at: \"" + s + "\"", ex);
				}
			}
		} else
			ITEMS_TO_GIVE = null;

	}
}
