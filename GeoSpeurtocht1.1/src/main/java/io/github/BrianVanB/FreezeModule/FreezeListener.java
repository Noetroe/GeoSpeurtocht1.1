package io.github.BrianVanB.FreezeModule;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;


public class FreezeListener implements Listener 
{
	private FreezeManager Manager;
	
	/**
	 * <h1>FreezeListener</h1>
	 * De FreezeListener kijkt naar spelers die proberen te bewegen
	 * en stopt ze als ze bevroren zijn.
	 * <p>
	 * 
	 * @param fm De class die het freezen beheert
	 */
	public FreezeListener(FreezeManager fm)
	{
		Manager = fm;
	}
	
	@EventHandler
	public void onLogin(PlayerLoginEvent e)
	{
		//als freezeall gedaan is, worden nieuwe/herloggende spelers bevroren
		if(Manager.GlobalFreeze)
			Manager.Freeze(e.getPlayer());
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e)
	{
		//voorkom beweging als de speler bevroren is door ze terug te zetten op hun originele positie
		if(e.getPlayer().hasMetadata("Frozen"))
		{
			e.getPlayer().sendMessage(ChatColor.RED + "Wacht tot de begeleider de speurtocht start!");
			e.getPlayer().teleport(e.getFrom());
		}
	}

}
