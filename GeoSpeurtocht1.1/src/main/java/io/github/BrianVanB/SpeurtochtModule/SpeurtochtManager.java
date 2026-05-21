package io.github.BrianVanB.SpeurtochtModule;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import io.github.BrianVanB.GeoSpeurtocht.GeoSpeurtocht;

public class SpeurtochtManager {

	private GeoSpeurtocht Master;
	private SpeurCommands cmdExecutor;
	
	public boolean Running;
	public Location Startpunt;
	public BukkitTask[] Timers;	
	public BossBarTimer TimerBar;
	public Map<UUID, BukkitTask> PlayerTimers = new HashMap<>();
	public Map<UUID, BossBarTimer> PlayerBars = new HashMap<>();
	
	/**
	 * <h1>SpeurtochtManager</h1>
	 * Beheert alle speurtocht gerelateerde waardes en classes.
	 * <p>
	 * @param plugin De hoofdplugin
	 */
	public SpeurtochtManager(GeoSpeurtocht plugin)
	{
		Master = plugin;
		
		Startpunt = LoadStartpunt();
		Running = false;
		Timers = null;
		TimerBar = new BossBarTimer(Master);
		
		cmdExecutor = new SpeurCommands(Master, this);
		Master.getCommand("setstart").setExecutor(cmdExecutor);
		Master.getCommand("startpunt").setExecutor(cmdExecutor);
		Master.getCommand("startall").setExecutor(cmdExecutor);
		Master.getCommand("stopall").setExecutor(cmdExecutor);
		Master.getCommand("stoptimers").setExecutor(cmdExecutor);
		Master.getCommand("startplayer").setExecutor(cmdExecutor);
		
		Master.getServer().getPluginManager().registerEvents(TimerBar, Master);
	}	
	
	/**
	 * <h1>LoadStartpunt</h1>
	 * Porbeert het startpunt te lezen uit het configuratie bestand.
	 * <p>
	 * @return <ul><li><b>Locatie</b> als er een startpunt is</li><li><b>Null</b> als er geen start is</li></ul>
	 */
	public Location LoadStartpunt()
	{		
	    try
	    {
	      return new Location(
	    		  Bukkit.getWorld(Master.getConfig().getString("Startpunt.World")), 
	    		  Master.getConfig().getInt("Startpunt.X"), 
	    		  Master.getConfig().getInt("Startpunt.Y"), 
	    		  Master.getConfig().getInt("Startpunt.Z"), 
	    		  Master.getConfig().getInt("Startpunt.Yaw"), 
	    		  Master.getConfig().getInt("Startpunt.Pitch"));
	    }
	    catch (Exception e) { Master.getLogger().warning("Fout bij laden van startpunt: " + e.getMessage()); }
	    return null;
	}
	
	/**
	 * <h1>SaveStartpunt</h1>
	 * Slaat het startpunt op in het configuratie bestand
	 */
	public void SaveStartpunt()
	{
		if(Startpunt == null)
		{
			Master.getLogger().warning("Kan geen startpunt opslaan want er is geen startpunt");
			return;
		}
			
	    Master.getConfig().set("Startpunt.X", Integer.valueOf(Startpunt.getBlockX()));
	    Master.getConfig().set("Startpunt.Y", Integer.valueOf(Startpunt.getBlockY()));
	    Master.getConfig().set("Startpunt.Z", Integer.valueOf(Startpunt.getBlockZ()));
	    Master.getConfig().set("Startpunt.Yaw", Float.valueOf(Startpunt.getYaw()));
	    Master.getConfig().set("Startpunt.Pitch", Float.valueOf(Startpunt.getPitch()));
	    Master.getConfig().set("Startpunt.World", Startpunt.getWorld().getName());
	    Master.saveConfig();
	}
		
}
