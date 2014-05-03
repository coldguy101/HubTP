package me.coldguy101.HubTP;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Sean on 5/1/2014.
 */
public class ServerTypeManager
{
	private Plugin plugin;
	private HashMap<String, ArrayList<String>> typesToServers = new HashMap<String, ArrayList<String>>();

	public ServerTypeManager(Plugin p)
	{
		this.plugin = p;
		extrapolateServerTypesFromConfigFile();
	}

	public void setType(String type, String name)
	{
		typesToServers.get(type).add(name);
	}

	public ArrayList<String> getAllServersFromType(String type)
	{
		return typesToServers.get(type);
	}

	public void extrapolateServerTypesFromConfigFile()
	{
		Set<String> ss = plugin.getConfig().getConfigurationSection("types").getKeys(false);
		for(String type : ss)
		{
			typesToServers.put(type, (ArrayList<String>) plugin.getConfig().getList("types." + type));
		}
	}
}
