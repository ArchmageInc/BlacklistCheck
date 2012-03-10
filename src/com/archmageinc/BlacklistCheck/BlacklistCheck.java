package com.archmageinc.BlacklistCheck;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class BlacklistCheck extends JavaPlugin {

	private Logger log				=	Logger.getLogger("Minecraft");
	
	@Override
	public void onEnable(){
		initialConfigCheck();
		getDNSBLServers();
		getServer().getPluginManager().registerEvents(new BlacklistListener(this), this);
		logMessage("Enabled");
	}
	
	@Override
	public void onDisable(){
		logMessage("Disabled");
	}
	
	public String[] getDNSBLServers(){
		if(!getConfig().isList("DNSBLServers"))
			return null;
		
		return	getConfig().getStringList("DNSBLServers").toArray(new String[0]);
	}
	
	private void initialConfigCheck(){
		if(!(new File(this.getDataFolder(),"config.yml").exists())){
			this.logMessage("Saving default configuration file.");
			this.saveDefaultConfig();
		}
	}
	
	public void logMessage(String msg){
		PluginDescriptionFile pdFile	=	this.getDescription();
		log.info("["+pdFile.getName()+" "+pdFile.getVersion()+"]: "+msg);
	}
	
	public boolean isWhitelisted(InetAddress ip){
		reloadConfig();
		List<String> whitelist	=	getConfig().getStringList("Whitelist");
		if((ip instanceof Inet4Address)){
			String ips	=	((Inet4Address) ip).toString().replaceAll("/","");
			if(whitelist.contains(ips)){
				logMessage("Address "+ip.toString()+" is whitelisted.");
				return true;
			}
		}
		
		return false;
	}
	
}
