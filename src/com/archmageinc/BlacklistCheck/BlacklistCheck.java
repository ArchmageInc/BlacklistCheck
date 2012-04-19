package com.archmageinc.BlacklistCheck;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.net.util.SubnetUtils;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class BlacklistCheck extends JavaPlugin {

	private Logger log;
	private BlacklistLookup BLL;
	
	@Override
	public void onEnable(){
		log				=	getLogger();
		initialConfigCheck();
		getDNSBLServers();
		BLL	=	new BlacklistLookup(this);
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
		getConfig().options().copyDefaults(true);
		if(!(new File(this.getDataFolder(),"config.yml").exists())){
			this.logMessage("Saving default configuration file.");
			this.saveDefaultConfig();
		}else{
			try {
				getConfig().save(new File(getDataFolder(),"config.yml"));
			} catch (IOException e) {
				logWarning("Unable to save configuration file!");
			}
		}
	}
	
	public void logMessage(String msg){
		PluginDescriptionFile pdFile	=	this.getDescription();
		log.info("["+pdFile.getName()+" "+pdFile.getVersion()+"]: "+msg);
	}
	
	public void logWarning(String msg){
		PluginDescriptionFile pdFile	=	this.getDescription();
		log.warning("["+pdFile.getName()+" "+pdFile.getVersion()+"]: "+msg);
	}
	
	public void logToFile(String msg){
		try {
			BufferedWriter writer		=	new BufferedWriter(new FileWriter(new File(getDataFolder(),"BlacklistCheck.log"),true));
			writer.write(msg);
			writer.newLine();
			writer.close();
		}catch (java.io.IOException e) {
			logMessage("Unable to write to BlacklistCheck.log: "+e.getMessage());
		}
	}
	
	public boolean isWhitelisted(InetAddress ip){
		reloadConfig();
		List<String> whitelist	=	getConfig().getStringList("Whitelist");
		if((ip instanceof Inet4Address)){
			String ips	=	((Inet4Address) ip).toString().replaceAll("/","");
			if(getConfig().getBoolean("Debug")){
				if(getConfig().getBoolean("LogToFile"))
					logToFile("Checking "+ips+" against the whitelist.");
				else
					logMessage("Checking "+ips+" against the whitelist.");
			}
			
			Iterator<String> itr	=	whitelist.iterator();
			while(itr.hasNext()){
				String subnet	=	itr.next();
				try{
					if(getConfig().getBoolean("Debug")){
						if(getConfig().getBoolean("LogToFile"))
							logToFile("Checking "+ips+" against whitelist entry "+subnet);
						else
							logMessage("Checking "+ips+" against whitelist entry "+subnet);
					}
					
					SubnetUtils util	=	new SubnetUtils(subnet);
					if(util.getInfo().getAddressCount()==0 && util.getInfo().getNetworkAddress().equals(ips)){
						if(getConfig().getBoolean("Debug")){
							if(getConfig().getBoolean("LogToFile"))
								logToFile("Address "+ips+" is whitelisted.");
							else
								logMessage("Address "+ips+" is whitelisted.");
						}
						
						return true;
					}
					if(util.getInfo().isInRange(ips)){
						if(getConfig().getBoolean("Debug")){
							if(getConfig().getBoolean("LogToFile"))
								logToFile("Address "+ips+" is in a whitelisted subnet: "+subnet);
							else
								logMessage("Address "+ips+" is in a whitelisted subnet: "+subnet);
						}
						
						return true;
					}
				}catch(IllegalArgumentException e){
					logWarning("Misconfiguration for whitelisted subnet "+subnet+". Invalid CIDR Notation");
				}
			}
			if(getConfig().getBoolean("Debug")){
				if(getConfig().getBoolean("LogToFile"))
					logToFile(ips+" is not whitelisted.");
				else
					logMessage(ips+" is not whitelisted.");
			}
			
		}
		
		return false;
	}
	
	public boolean isBlacklisted(InetAddress ip){
		return BLL.isBlacklisted(ip);
	}
	
}
