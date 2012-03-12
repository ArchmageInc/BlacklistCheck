package com.archmageinc.BlacklistCheck;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;



public class BlacklistLookup {
	
	private String[] DNSBLServers;
	private BlacklistCheck plugin;
	
	public BlacklistLookup(BlacklistCheck plugin){
		this.plugin		=	plugin;
		DNSBLServers	=	plugin.getDNSBLServers();
	}
	
	public boolean isBlacklisted(InetAddress ip){
		if(plugin.getConfig().getBoolean("Debug"))
			plugin.logMessage("Checking address "+ip.toString()+" against blacklist servers");
		
		if(plugin.isWhitelisted(ip))
			return false;
		
		if(!(ip instanceof Inet4Address))
			return false;
		
		String[] parts	=	((Inet4Address) ip).toString().replaceAll("/","") .split("\\.");
		
		if(parts.length!=4)
			return false;
		
		String reversed	=	parts[3]+"."+parts[2]+"."+parts[1]+"."+parts[0];
		for(String DNSBLServer : DNSBLServers){
			try {
				if(plugin.getConfig().getBoolean("Debug"))
					plugin.logMessage("Checking DNSEntry: "+reversed+"."+DNSBLServer);
				
				if(InetAddress.getByName(reversed+"."+DNSBLServer)!=null)
					return true;
				
			} catch (UnknownHostException e) {}
		}
		return false;
		
	}
}
