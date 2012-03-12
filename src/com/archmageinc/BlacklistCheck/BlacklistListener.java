package com.archmageinc.BlacklistCheck;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent.Result;

public class BlacklistListener implements Listener {
	
	private BlacklistLookup BLL;
	private BlacklistCheck plugin;
	
	public BlacklistListener(BlacklistCheck plugin){
		this.plugin	=	plugin;
		BLL			=	new BlacklistLookup(plugin);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPreLoginEvent(PlayerPreLoginEvent event){
		if(BLL.isBlacklisted(event.getAddress())){
			event.setKickMessage("Connected from a publicly blacklisted server!");
			if(plugin.getConfig().getBoolean("LogDisconnects"))
				plugin.logMessage(event.getAddress().toString()+" has been blocked from connecting (DNSBL address)");
			event.setResult(Result.KICK_OTHER);
		}
	}

}
