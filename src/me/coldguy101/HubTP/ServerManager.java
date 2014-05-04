package me.coldguy101.HubTP;

import java.util.*;

/**
 * Created by Sean on 4/27/2014.
 */
public class ServerManager
{
	private Collection<String> servers = Collections.emptyList();
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
		servers = new HashSet<String>(Arrays.asList(allServersInConfig));
	}

	public Collection<String> getServers()
	{
		return servers;
	}
}
