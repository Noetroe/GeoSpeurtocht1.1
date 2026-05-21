package io.github.BrianVanB.SpeurtochtModule;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import io.github.BrianVanB.GeoSpeurtocht.GeoSpeurtocht;
import io.github.BrianVanB.Utilities.ScheduledBroadcast;

public class SpeurCommands implements CommandExecutor {

	private GeoSpeurtocht Master;
	private SpeurtochtManager Manager;

	/**
	 * <h1>SpeurCommands</h1>
	 * Class die alle commando's gerelateerd aan de speurtocht uitvoert.
	 * <p>
	 * @param master De hoofdplugin
	 * @param manager De class die de speurtocht beheert
	 */
	public SpeurCommands(GeoSpeurtocht master, SpeurtochtManager manager) 
	{
		Master = master;
		Manager = manager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
	{
		
		switch(command.getName().toLowerCase())
		{
		
		/*  Commando voor het plaatsen van een startpunt.
		 *  Startpunt wordt geplaatst op de positie van de speler die het commando uitvoert.
		 *  Daarom kan het ook alleen door een speler gedaan worden
		 */		
		case "setstart":
			
			if(sender instanceof Player)
			{
				Manager.Startpunt = ((Player) sender).getLocation();
				sender.sendMessage(ChatColor.GREEN + "Startpunt geupdate.");
				Master.getLogger().info("Startpunt geupdate");
				return true;
			}
			else return Master.CommandError(sender, ChatColor.RED + "Player-only command");
			
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			
			/*
			 * Simpel commando om terug te teleporteren naar het startpunt (als er een is)
			 */
		case "startpunt":
			
			if(sender instanceof Player)
			{
				if(Manager.Startpunt == null)
					return Master.CommandError(sender, "Geen startpunt gevonden. Plaats een startpunt met /setstart", true);
				
				((Player) sender).teleport(Manager.Startpunt);
				return true;
			}
			else return Master.CommandError(sender, "That command is player only");

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			
			/*
			 *  Start timers voor de speurtocht voor alle spelers op de server
			 *  Heeft de beschikbare tijd in minuten als argument nodig
			 *  Spelers krijgen te zien dat ze mogen beginnen, hoe lang ze hebben, 
			 *  een melding zodra de helft van de tijd op is, zodra er nog 1 minuut is en als het klaar is.
			 *  Als de speurtocht klaar is worden de spelers automatisch bevroren en teruggezet op het startpunt.
			 */			
		case "startall":
			
			if(Manager.Startpunt == null)
				return Master.CommandError(sender, "Geen startpunt gevonden. Plaats een startpunt met /setstart");
			
			if(args.length < 1)
				return Master.CommandError(sender, "Geen tijd gegeven: " + Master.getCommand("startall").getUsage());
			
			if(Manager.Running)
				return Master.CommandError(sender, "Er is al een speurtocht bezig! Gebruik /stopall om deze eerst te stoppen.");
			
			float minutes = 0;
			try 
			{
				int tijd = Integer.parseInt(args[0]);
				minutes = (float)tijd;
			} 
			catch (NumberFormatException e) 
			{
				Master.getLogger().warning(e.getMessage());
				return Master.CommandError(sender, "Ongeldige waarde in tijd."); 
			} 
			
			Master.getLogger().info("Speurtocht wordt gestart...");
			
			//Maak timers voor een bericht op de helft van de tijd, laatste minuut en einde.
			Manager.Timers = new BukkitTask[] {					
					new ScheduledBroadcast(Master, ChatColor.GOLD + 
							"Tijd is op.", 5).runTaskTimer(Master, (long)((minutes)*60*20), 20),
					
					new SpeurtochtEind(Master, Manager.Startpunt).runTaskLater(Master, (long)(minutes*60*20 + 100))
			};

			Master.FreezeManager.Unfreezeall();
			Master.ExtraStuff.TPall(Manager.Startpunt);
			Master.getServer().broadcastMessage(ChatColor.GREEN + "Je mag nu beginnen. Succes!");
			Master.getServer().broadcastMessage(ChatColor.GOLD + "Je hebt " + (int)minutes + " minuten.");
			Manager.TimerBar.Create((int)minutes);
			Manager.Running = true;
			return true;
			
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			
			/*
			 * Commando om de speurtocht vroegtijdig te stoppen. 
			 * Timers worden geannuleerd en de spelers bevroren + geteleporteerd naar de start
			 */
			
		case "stopall":
			
			if(Manager.Startpunt == null)
				return Master.CommandError(sender, "Geen startpunt gevonden. Plaats een startpunt met /setstart");
			
			Master.getLogger().info("Speurtocht wordt gestopt...");
			
			if(Manager.Running)
				for(BukkitTask t : Manager.Timers)
					t.cancel();

			for(BukkitTask t : Manager.PlayerTimers.values())
				t.cancel();
			Manager.PlayerTimers.clear();

			for(BossBarTimer bar : Manager.PlayerBars.values())
				bar.Cancel();
			Manager.PlayerBars.clear();
			
			Master.ExtraStuff.TPall(Manager.Startpunt);
			Master.FreezeManager.Freezeall();
			
			if(Manager.Running)
				Manager.TimerBar.Cancel();
			
			Manager.Running = false;
			
			new ScheduledBroadcast(Master, ChatColor.GOLD + 
					"Tijd is op.", 5).runTaskTimer(Master, 0, 20);
			
			return true;
			
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			
			/*
			 * Commando om de timers die op de achtergrond draaien te annuleren zodat spelers
			 * eventueel nog ietsje langer door kunnen spelen.
			 * Je moet daarna stopall of tpall+freezeall doen om iedereen te resetten
			 */
			
		/*
		 * Start de speurtocht voor één specifieke speler.
		 * Optioneel kan een tijd in minuten meegegeven worden.
		 * De speler wordt geteleporteerd naar het startpunt, ontbevroren en ontvangt een bericht.
		 * Als er een tijd is opgegeven wordt de speler na die tijd automatisch bevroren en teruggezet.
		 */
		case "startplayer":

			if (Manager.Startpunt == null)
				return Master.CommandError(sender, "Geen startpunt gevonden. Plaats een startpunt met /setstart");

			if (args.length < 1)
				return Master.CommandError(sender, "Geen speler gegeven: " + Master.getCommand("startplayer").getUsage());

			Player startTarget = Master.getServer().getPlayerExact(args[0]);
			if (startTarget == null)
				return Master.CommandError(sender, "Speler '" + args[0] + "' niet gevonden of niet online.");

			Master.FreezeManager.UnFreeze(startTarget);
			startTarget.teleport(Manager.Startpunt);
			startTarget.sendMessage(ChatColor.GREEN + "Je mag nu beginnen. Succes!");
			sender.sendMessage(ChatColor.GREEN + startTarget.getName() + " is gestart.");

			if (args.length >= 2)
			{
				int playerMinutes = 0;
				try
				{
					playerMinutes = Integer.parseInt(args[1]);
				}
				catch (NumberFormatException e)
				{
					return Master.CommandError(sender, "Ongeldige waarde in tijd.");
				}

					startTarget.sendMessage(ChatColor.GOLD + "Je hebt " + playerMinutes + " minuten.");

				final Player finalTarget = startTarget;
				final Location playerStart = Manager.Startpunt;
				final long ticks = (long)(playerMinutes * 60 * 20);
				final UUID playerId = startTarget.getUniqueId();

				BossBarTimer playerBar = new BossBarTimer(Master);
				playerBar.CreateForPlayer(playerMinutes, startTarget);
				Manager.PlayerBars.put(playerId, playerBar);

				BukkitTask playerTask = new BukkitRunnable()
				{
					@Override
					public void run()
					{
						if (finalTarget.isOnline())
						{
							finalTarget.teleport(playerStart);
							Master.FreezeManager.Freeze(finalTarget);
							finalTarget.sendMessage(ChatColor.GOLD + "Tijd is op!");
						}
						Manager.PlayerTimers.remove(playerId);
						Manager.PlayerBars.remove(playerId);
					}
				}.runTaskLater(Master, ticks);

				Manager.PlayerTimers.put(playerId, playerTask);
			}

			return true;

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		case "stoptimers":
			
			if(!Manager.Running)
				return Master.CommandError(sender, "Er is geen actieve speurtocht");
			
			Master.getLogger().info("Speurtocht timers gestopt.");
			
			for(BukkitTask t : Manager.Timers)
				t.cancel();
			
			Manager.TimerBar.Cancel();
			Manager.Running = false;
			sender.sendMessage("Timers gestopt. Gebruik /stopall om alle spelers te resetten");
			
			return true;
		}
		
		return false;
	}
}
