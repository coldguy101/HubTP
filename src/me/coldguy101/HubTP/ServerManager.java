package me.coldguy101.HubTP;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sean on 4/27/2014.
 */
public class ServerManager
{
	private ArrayList<String> servers = new ArrayList<String>();
	private HashMap<String, Integer> counts = new HashMap<String, Integer>();

	public void setServerCount(String serv, int numP)
	{
		counts.put(serv, numP);
	}

	public int getServerCount(String serv)
	{
		return counts.get(serv);
	}

	public void setServers(String[] allServersInConfig)
	{
		for(String s : allServersInConfig)
		{
			servers.add(s);
		}
	}

	public ArrayList<String> getServers()
	{
		return servers;
	}
}
