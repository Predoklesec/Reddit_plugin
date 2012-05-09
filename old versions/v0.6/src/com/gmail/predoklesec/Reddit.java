package com.gmail.predoklesec;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Reddit extends JavaPlugin {
	
	Logger log;
	String link = "http://www.reddit.com/r/";
	String topic;
	News[] data;
	int stevec = 0;
	int interval = 0;
	Thread getData;
	String INFO = ChatColor.RED + "[Reddit] ";
	ChatColor message_c = ChatColor.YELLOW;
	ChatColor link_c = ChatColor.BLUE;
	ChatColor news_c = ChatColor.RED;
	
	public void onEnable(){ 
		log = this.getLogger();
		getData = new Thread(gd);
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		log.info("Plugin has been enabled!");
		interval = getConfig().getInt("Interval")*60*20;
		topic = getConfig().getString("Topic");
			
		getData.run();
		
		if(getConfig().getString("MessageColor") != null) message_c = ChatColor.getByChar(getConfig().getString("MessageColor"));
		if(getConfig().getString("LinkColor") != null) link_c = ChatColor.getByChar(getConfig().getString("LinkColor"));
		if(getConfig().getString("NewsColor") != null) news_c = ChatColor.getByChar(getConfig().getString("NewsColor"));
		
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				nextData(null);
			}
		}, 0L, interval);
	}
	
	public void onDisable(){ 
		log.info("Plugin has been disabled!");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if (cmd.getName().equalsIgnoreCase("reddit") && sender.hasPermission("reddit.*")){
			if(args.length==0){
				sender.sendMessage(ChatColor.GOLD + "Try /help reddit ;)");
                return true;
            }
			if(args[0].equalsIgnoreCase("set")){
				if(args[1] != null){
					if(args[1].equalsIgnoreCase("interval")){
						if(args[2] != null){
							getConfig().set("Interval", Integer.parseInt(args[2]));
							sender.sendMessage(INFO + ChatColor.YELLOW + "Interval set to: " + args[2] + " minutes.");
							saveConfig();
							onEnable();
							return true;
						}
					}
					if(args[1].equalsIgnoreCase("topic")){
						if(args[2] != null){
							getConfig().set("Topic", args[2]);
							sender.sendMessage(INFO + ChatColor.YELLOW + "Topic set to: " + args[2] + ".");
							saveConfig();
							topic = getConfig().getString("Topic");
							getData.start();
							return true;
						}
					}
				}
            }
            if(args[0].equalsIgnoreCase("get")){
            	getData.start();
    			return true;
            }
            if(args[0].equalsIgnoreCase("next")){
            	nextData(sender);
            	return true;
            }
            else if(args[0].equalsIgnoreCase("reload")){
            	reloadConfig();
    			onEnable();
    			sender.sendMessage(INFO + ChatColor.YELLOW + "Reloaded!");
                return true;
            }
		}
		return false;
	}
	
	Runnable gd = new Runnable() {
		public void run() {
			data = getData();
		 }
	};
	
	public void nextData(CommandSender sender){
		if(data != null){
    		getServer().broadcastMessage(news_c + "[/r/" + topic + "] " + message_c + data[stevec].getNews());
    		getServer().broadcastMessage(link_c + data[stevec].getTiny());
    		stevec++;
    		if(stevec == data.length){
    			try { 
    				//data = getData();
    				data = null;
    				getData.start();
    				stevec = 0;
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		}
    	}
    	else{
    		sender.sendMessage(INFO + ChatColor.YELLOW + "News data is empty or is reloading!");
    	}
	}
	
	public News[] getData() {
		data = null;
		stevec = 0;
		try {
			URL url = new URL(link+topic+"/.rss");
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(url.openStream());
			NodeList nodes = doc.getElementsByTagName("item");
			
			News[] tableofnews = new News[nodes.getLength()];
			
			log.info("Fatched " + nodes.getLength() + " news :D");
			
			for(int i=0;i<nodes.getLength();i++) {
				Element element = (Element)nodes.item(i);
				String title = getElementValue(element,"title");
				String link = getElementValue(element,"link");
				String description = getElementValue(element,"description");
				tableofnews[i] = new News(title, link, description);
			}
			
			return tableofnews;
		} catch (ParserConfigurationException e) {
			log.info("error with parsing data!");
			return null;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			log.info("error with parsing data!");
			e.printStackTrace();
			return null;
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			log.info("error with parsing data!");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.info("error with parsing data!");
			e.printStackTrace();
			return null;
		}
	}
	
	private String getCharacterDataFromElement(Element e) {
		try {
			Node child = e.getFirstChild();
			if(child instanceof CharacterData) {
					CharacterData cd = (CharacterData) child;
					return cd.getData();
			}
		}
		catch(Exception ex) {}
		return "";
	}

	protected float getFloat(String value) {
		if(value != null && !value.equals("")) return Float.parseFloat(value);
		return 0;
	}
	
	protected String getElementValue(Element parent,String label) {
		return getCharacterDataFromElement((Element)parent.getElementsByTagName(label).item(0));
	}
}
