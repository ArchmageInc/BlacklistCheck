package com.archmageinc.BlacklistCheck;

import java.net.InetAddress;
import java.util.TimerTask;

import org.bukkit.entity.Player;

public class BlacklistTask extends TimerTask  {

	private Player player;
	private BlacklistCheck plugin;
	private InetAddress IP;
	
	public BlacklistTask(BlacklistCheck instance,Player p){
		plugin		=	instance;
		player		=	p;
		IP			=	player.getAddress().getAddress();
	}
	
	@Override
	public void run() {
		if(player!=null && plugin.isBlacklisted(IP)){
			if(plugin.getConfig().getBoolean("LogDisconnects"))
				plugin.logMessage(IP.toString()+" has been kicked (DNSBL address)");
			
			player.kickPlayer(plugin.getConfig().getString("DisconnectMessage"));
		}		
	}

}
