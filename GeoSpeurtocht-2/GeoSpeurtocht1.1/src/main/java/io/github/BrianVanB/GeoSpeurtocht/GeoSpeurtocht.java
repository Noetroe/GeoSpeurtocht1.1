package io.github.BrianVanB.GeoSpeurtocht;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.BrianVanB.FreezeModule.FreezeManager;
import io.github.BrianVanB.SpeurtochtModule.SpeurtochtManager;
import io.github.BrianVanB.Utilities.ExtraCommands;

public class GeoSpeurtocht extends JavaPlugin {
	
	public SpeurtochtManager SpeurManager; //alles speurtocht gerelateerd
	public FreezeManager FreezeManager;    //alles freeze gerelateerd
	public ExtraCommands ExtraStuff; 	   //extra dingetjes en goeie meems
	
	@Override
	public void onEnable()
	{
		//startup
		
		//maak de managers
		FreezeManager = new FreezeManager(this);
		SpeurManager = new SpeurtochtManager(this);
		ExtraStuff = new ExtraCommands(this);		
		
		getLogger().info("Finished loading");
	}
	
	@Override
	public void onDisable()
	{
		//shutdown logic
		getLogger().info("Storing data...");		
		SpeurManager.SaveStartpunt(); //sla het startpunt op
	}
	
	/**
	 * <h1>CommandError</h1>
	 * Een functie die door de hele plugin gebruikt kan worden om een bericht
	 * te sturen naar iemand die een fout maakt met een commando.
	 * <p>
	 * @param sender Degene die het commando heeft uitgevoerd
	 * @param msg Het bericht om naar de speler te sturen
	 * @return Een boolean die altijd false is omdat er iets fout is gegaan met een commando
	 */
	public boolean CommandError(CommandSender sender, String msg)
	{
		sender.sendMessage(msg);
		return false;
	}
	public boolean CommandError(CommandSender sender, String msg, boolean returnval)
	{
		sender.sendMessage(msg);
		return returnval;
	}
}
