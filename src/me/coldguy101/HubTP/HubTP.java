package me.coldguy101.HubTP;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Sean on 4/27/2014.
 */
public class HubTP extends JavaPlugin
{
	BungeeUtil bUtil;

	@Override
	public void onEnable()
	{
		bUtil = new BungeeUtil(this);
		getCommand("hubtp").setExecutor(new CommandHubTP());
	}

	@Override
	public void onDisable()
	{

	}
}
