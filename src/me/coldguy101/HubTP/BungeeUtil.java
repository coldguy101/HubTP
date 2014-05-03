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
		plugin = pl;
		pingTask = new PlayerCountCheckTask().runTaskTimerAsynchronously(plugin, 100L, 200L);
		Bukkit.getMessenger().registerOutgoingPluginChannel(pl, pmChannel);
		Bukkit.getMessenger().registerIncomingPluginChannel(pl, pmChannel, this);
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
	public static void getAllOnlineServers()
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
		//Bukkit.broadcastMessage("Message Received:");
		if(!channel.equalsIgnoreCase(pmChannel))
			return;

		DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
		try
		{
			String command = in.readUTF();
			String answer = in.readLine();

			Bukkit.broadcastMessage(command);
			Bukkit.broadcastMessage(answer);
			Bukkit.broadcastMessage("" + in.readInt());

			if(command.equalsIgnoreCase("PlayerCount"))
			{
				sm.setServers(answer.trim().split(","));
			}
			else
			{
				sm.setServerCount(answer.trim(), in.readInt());
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
			if(Bukkit.getOnlinePlayers().length > 0)
			{
				Bukkit.broadcastMessage("hihi");
				BungeeUtil.getAllOnlineServers();
//				sm.setServers(new String[]{"hg1","hg2","hg3","hg4","hg5","hg6","hub1","hub2","hub3"});
				BungeeUtil.checkCurrentPlayersOnServers();
			}
		}
	}
}
