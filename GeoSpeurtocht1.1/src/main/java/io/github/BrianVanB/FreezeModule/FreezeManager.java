package io.github.BrianVanB.FreezeModule;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import io.github.BrianVanB.GeoSpeurtocht.GeoSpeurtocht;

public class FreezeManager {

	private GeoSpeurtocht Master;
	private FreezeCommands cmdExecutor;
	private FreezeListener listener;
	
	public boolean GlobalFreeze = false;
	
	/**
	 * <h1>FreezeManager</h1>
	 * De FreezeManager beheert de CommandExecutor en Listener die gebruikt worden
	 * voor alle freeze gerelateerde zaken.
	 * <p>
	 * @param plugin De hoofdplugin
	 */
	public FreezeManager(GeoSpeurtocht plugin)
	{
		Master = plugin;	
		
		//commands
		cmdExecutor = new FreezeCommands(Master, this);
		Master.getCommand("freeze").setExecutor(cmdExecutor);
		Master.getCommand("unfreeze").setExecutor(cmdExecutor);
		Master.getCommand("freezeall").setExecutor(cmdExecutor);
		Master.getCommand("unfreezeall").setExecutor(cmdExecutor);

		//listener
		listener = new FreezeListener(this);
		Master.getServer().getPluginManager().registerEvents(listener, Master);
		
	}
	
	/**
	 * <h1>Freeze</h1>
	 * Bevriest de gegeven speler.
	 * <p>
	 * @param p De speler om te bevriezen
	 */
	public void Freeze(Player p)
	{
		if(p.isOp()) //Operators worden niet bevroren
			return;
		
		p.setMetadata("Frozen", new FixedMetadataValue(Master, true));
	}
	
	/**
	 * <h1>Unfreeze</h1>
	 * Bevrijd de gegeven speler.
	 * <p>
	 * @param p De speler om vrij te laten
	 */
	public void UnFreeze(Player p)
	{
		if(p.hasMetadata("Frozen"))
			p.removeMetadata("Frozen", Master);
	}
	
	/**
	 * <h1>Freezeall</h1>
	 * Bevriest alle spelers in de server die niet operator zijn.
	 */
	public void Freezeall()
	{
		GlobalFreeze = true;
		for(Player p : Master.getServer().getOnlinePlayers())
		{
			Freeze(p);
		}
	}
	
	/**
	 * <h1>Unfreezeall</h1>
	 * Bevrijd alle spelers in de server.
	 */
	public void Unfreezeall()
	{
		GlobalFreeze = false;
		for(Player p : Master.getServer().getOnlinePlayers())
		{
			UnFreeze(p);
		}
	}
}
