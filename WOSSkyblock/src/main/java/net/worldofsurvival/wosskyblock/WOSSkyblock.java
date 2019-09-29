package net.worldofsurvival.wosskyblock;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import net.worldofsurvival.wosskyblock.commands.IslandCommand;
import net.worldofsurvival.wosskyblock.items.MainItems;
import net.worldofsurvival.wosskyblock.listeners.InventoryClickListener;
import net.worldofsurvival.wosskyblock.menus.IslandManageMenu;
import net.worldofsurvival.wosskyblock.utils.Common;

public final class WOSSkyblock extends JavaPlugin {
	//Edit something and send me a screenie of your GitHub desktop
	private Common common = new Common();
	private MainItems mainItems = new MainItems();
	private IslandManageMenu mainSelectorMenu = new IslandManageMenu(mainItems);

	@Override
	public void onEnable() {
		this.registerCommands(
				new IslandCommand(common, mainSelectorMenu)

				);

		this.registerEvents(this, 
				new InventoryClickListener(common)

				);
		Command command;

	}


	//Registering events.
	private void registerEvents(WOSSkyblock plugin, Listener... listeners) {
		final PluginManager pm = plugin.getServer().getPluginManager();

		for (final Listener lis : listeners)
			pm.registerEvents(lis, plugin);
	}

	//Registering commands to the command map
	private void registerCommands(Command... commands) {
		for (Command command : commands) {
			try {
				final Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
				commandMapField.setAccessible(true);

				final CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
				commandMap.register(command.getLabel(), command);
			} catch (final Exception e) {
				e.printStackTrace();

				common.log("&4Could not register command: " + command.getName());
			}
		}
	}
}