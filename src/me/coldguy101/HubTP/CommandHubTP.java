package me.coldguy101.HubTP;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Sean on 4/27/2014.
 */
public class CommandHubTP implements CommandExecutor
{
	ServerManager sm = new ServerManager();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if(sender instanceof Player)
		{
			Player player = (Player) sender;

			if(args.length == 0)
				return false;
			else if(args.length == 1)
			{
				String serv = args[0];

				if(!sm.getServers().contains(serv))
				{
					player.sendMessage("Sorry, that server does not exist!");
					return false;
				}


			}
			else
				return false;

		}
		else
		{
			if(args.length == 0)
			{
				sender.sendMessage("Need more args than that...");
				return false;
			}
			else if(args.length == 1)
			{
				sender.sendMessage("Please specify the player you wish to send!");
				return false;
			}
			else if(args.length == 2)
			{
				String playerName = args[0];
				String serv = args[1];

				if(serverContainsPlayer(playerName))
				{

				}
				if(!sm.getServers().contains(serv))
				{
					sender.sendMessage("Sorry, that server does not exist!");
				}
			}
			else
				return false;
		}
		return false;
	}

	/**
	 * Checks if the server the plugin is installed on contains a player with the given player name.
	 * @param pName
	 * @return
	 */
	private boolean serverContainsPlayer(String pName)
	{
		for(Player p : Bukkit.getOnlinePlayers())
		{
			if(p.getName().equals(pName))
			{
				return true;
			}
		}
		return false;
	}
}
