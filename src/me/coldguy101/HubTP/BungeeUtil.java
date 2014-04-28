package me.coldguy101.HubTP;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.*;
import java.util.logging.Level;

/**
 * Created by Sean on 4/27/2014.
 */
public class BungeeUtil implements PluginMessageListener
{
	private static ServerManager sm = new ServerManager();
	private static String pmChannel = "BungeeCord";
	private static Plugin plugin;
	private static BukkitTask pingTask;

	/**
	 * Constructor. Pass it a JavaPlugin and it will be happy.
	 * @param pl
	 */
	public BungeeUtil(Plugin pl)
	{
		pingTask = new PlayerCountCheckTask().runTaskTimerAsynchronously(plugin, 100L, 100L);
		plugin = pl;
		Bukkit.getMessenger().registerOutgoingPluginChannel(pl, pmChannel);
		Bukkit.getMessenger().registerIncomingPluginChannel(pl, pmChannel, this);
		getAllServers();
	}

	/**
	 * Moves the specified player to the specified server.
	 * @param p
	 * @param serv
	 */
	public static void movePlayerToServer(Player p, String serv)
	{
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try
		{
			out.writeUTF("Connect");
			out.writeUTF(serv);
		}
		catch (IOException ex)
		{
			// Impossibru
		}
		p.sendPluginMessage(plugin, pmChannel, b.toByteArray());
	}

	/**
	 * Should only run once at startup.
	 */
	public static void getAllServers()
	{
		if(Bukkit.getOnlinePlayers()[0] != null)
		{
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			try
			{
				out.writeUTF("GetServers");
			}
			catch (IOException ex)
			{
				// Impossibru
			}
			Bukkit.getOnlinePlayers()[0].sendPluginMessage(plugin, pmChannel, b.toByteArray());
		}
	}

	/**
	 * Checks all servers and gets player count
	 */
	public static void checkCurrentPlayersOnServers()
	{
		for(String s : sm.getServers())
		{
			checkCurrentPlayersOnServer(s);
		}
	}

	/**
	 * Gets Player Count for the specified server
	 * @param serv
	 */
	public static void checkCurrentPlayersOnServer(String serv)
	{
		if(Bukkit.getOnlinePlayers()[0] != null)
		{
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			try
			{
				out.writeUTF("PlayerCount");
				out.writeUTF(serv);
			}
			catch (IOException ex)
			{
				// Impossibru
			}
			Bukkit.getOnlinePlayers()[0].sendPluginMessage(plugin, pmChannel, b.toByteArray());
		}
	}

	/**
	 * Parses Incoming plugin messages and uses ServerManager to
	 * @param channel
	 * @param player
	 * @param message
	 */
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message)
	{
		if(!channel.equalsIgnoreCase(pmChannel))
			return;

		DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
		try
		{
			String s = in.readUTF();

			if(s.contains(","))
			{
				sm.setServers(s.split(","));
			}
			else
			{
				sm.setServerCount(in.readUTF(), in.readInt());
			}
		}
		catch (IOException e)
		{
			Bukkit.getLogger().log(Level.SEVERE, "Whoopsies... Apparently there was an IOException within BungeeUtil. You should probably look at that.");
			e.printStackTrace();
		}
	}

	private class PlayerCountCheckTask extends BukkitRunnable
	{
		@Override
		public void run()
		{
			BungeeUtil.checkCurrentPlayersOnServers();
		}
	}
}
