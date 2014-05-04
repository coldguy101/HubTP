package me.coldguy101.HubTP;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by Sean on 4/27/2014.
 */
public class BungeeUtil implements PluginMessageListener
{
	private ServerManager sm = new ServerManager();
	private String pmChannel = "BungeeCord";
	private Plugin plugin;
	private BukkitTask pingTask;
	private Map<String, List<PlayerCountCallback>> playerCountCallbacks = new HashMap<String, List<PlayerCountCallback>>();
	private List<GetServersCallback> getServersCallbacks = new ArrayList<GetServersCallback>();

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
	public void movePlayerToServer(Player p, String serv)
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
	public synchronized void getAllOnlineServers(GetServersCallback callback)
	{
		if (Bukkit.getOnlinePlayers()[0] == null)
		{
			return;
		}

		if (callback != null)
		{
			getServersCallbacks.add(callback);
		}
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

	/**
	 * Gets Player Count for the specified server
	 * @param serv
	 */
	public synchronized void checkCurrentPlayersOnServer(String serv, PlayerCountCallback callback)
	{
		if (Bukkit.getOnlinePlayers()[0] == null)
		{
			return;
		}

		if (callback != null)
		{
			addPlayerCountCallback(serv, callback);
		}
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

	private synchronized void addPlayerCountCallback(String serv, PlayerCountCallback callback)
	{
		List<PlayerCountCallback> callbacks = playerCountCallbacks.get(serv);
		if (callbacks == null)
		{
			callbacks = new ArrayList<PlayerCountCallback>();
			playerCountCallbacks.put(serv, callbacks);
		}
		callbacks.add(callback);
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
			Bukkit.broadcastMessage(command);

			if (command.equals("PlayerCount"))
			{
				String server = in.readUTF();
				int playerCount = in.readInt();
				for (PlayerCountCallback callback: takePlayerCountCallbacks(server))
				{
					callback.onPlayerCountReceived(server, playerCount);
				}
			}
			else if (command.equals("GetServers"))
			{
				String[] serverList = in.readUTF().split(", ");
				for (GetServersCallback callback: takeGetServersCallbacks())
				{
					callback.onServersReceived(serverList);
				}
			}
			else
			{
				plugin.getLogger().warning("Unexpected bungee message: " + command);
			}
		}
		catch (IOException e)
		{
			Bukkit.getLogger().log(Level.SEVERE, "Whoopsies... Apparently there was an IOException within BungeeUtil. You should probably look at that.");
			e.printStackTrace();
		}
	}

	private synchronized List<GetServersCallback> takeGetServersCallbacks()
	{
		List<GetServersCallback> callbacks = getServersCallbacks;
		getServersCallbacks = new ArrayList<GetServersCallback>();
		return callbacks;
	}

	private synchronized List<PlayerCountCallback> takePlayerCountCallbacks(String server)
	{
		List<PlayerCountCallback> callbacks = playerCountCallbacks.remove(server);
		return callbacks == null ? Collections.<PlayerCountCallback>emptyList() : callbacks;
	}

	private class PlayerCountCheckTask extends BukkitRunnable
	{
		@Override
		public void run()
		{
			if (Bukkit.getOnlinePlayers().length <= 0)
			{
				return;
			}
			Bukkit.broadcastMessage("hihi");
			getAllOnlineServers(new GetServersCallback()
			{
				@Override
				public void onServersReceived(String[] serverList)
				{
					for (String server: serverList)
					{
						checkCurrentPlayersOnServer(server, new PlayerCountCallback()
						{
							@Override
							public void onPlayerCountReceived(String serverName, int playerCount)
							{
								sm.setServerCount(serverName, playerCount);
							}
						});
					}
				}
			});
		}
	}

	public interface PlayerCountCallback {
		void onPlayerCountReceived(String serverName, int playerCount);
	}

	public interface GetServersCallback {
		void onServersReceived(String[] serverList);
	}
}
