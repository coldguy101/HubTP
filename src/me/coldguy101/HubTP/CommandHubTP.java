package me.coldguy101.HubTP;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

				if(serv.equalsIgnoreCase("dumpservers"))
				{
					for(String s : sm.getServers())
					{
						player.sendMessage(ChatColor.RED + s + ChatColor.LIGHT_PURPLE + " > " + ChatColor.GREEN + sm.getServerCount(s));
					}
					return true;
				}
				if(!sm.getServers().contains(serv))
				{
					player.sendMessage(ChatColor.RED + "Sorry, that server does not exist!");
					return false;
				}
				//put working code here.

				return true;
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

				// Since this will be performed by a human, no need to use UUIDs (that would be counterproductive)
				// noinspection deprecation
				if(Bukkit.getPlayerExact(playerName) == null)
				{
					sender.sendMessage("Sorry! The server does not have a player of that name.");
					return false;
				}
				if(!sm.getServers().contains(serv))
				{
					sender.sendMessage("Sorry, that server does not exist!");
					return false;
				}
				//put working code here.
				return true;
			}
			else
				return false;
		}
	}
}
