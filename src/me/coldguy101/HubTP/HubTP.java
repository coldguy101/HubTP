package me.coldguy101.HubTP;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Sean on 4/27/2014.
 */
public class HubTP extends JavaPlugin
{
	private ServerTypeManager stm;
	private BungeeUtil bUtil;

	@Override
	public void onEnable()
	{
		saveDefaultConfig();
		bUtil = new BungeeUtil(this);
		stm = new ServerTypeManager(this);

		getCommand("hubtp").setExecutor(new CommandHubTP());
	}

	@Override
	public void onDisable()
	{

	}
}
