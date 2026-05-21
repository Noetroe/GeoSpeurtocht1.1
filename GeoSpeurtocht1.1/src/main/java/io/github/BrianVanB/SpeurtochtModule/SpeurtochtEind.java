package io.github.BrianVanB.SpeurtochtModule;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.BrianVanB.GeoSpeurtocht.GeoSpeurtocht;

public class SpeurtochtEind extends BukkitRunnable
{
	private GeoSpeurtocht Master;
	private Location dest;
	
	/**
	 * <h1>SpeurtochtEind</h1>
	 * De commandos die na een gegeven tijd alle spelers weer naar de start teleporteren en bevriezen 
	 * <p>
	 * @param master De hoofdplugin
	 * @param destination De locatie om iedereen heen te brengen
	 */
	public SpeurtochtEind(GeoSpeurtocht master, Location destination)
	{
		Master = master;
		dest = destination;
	}
	
	@Override
	public void run()
	{
		for(Player p : Master.getServer().getOnlinePlayers())
		{
			if(p.isOp())
				continue;
			
			p.teleport(dest);
			Master.FreezeManager.Freeze(p);
		}
		Master.FreezeManager.GlobalFreeze = true;
		this.cancel();
	}
}
