package com.gmail.predoklesec;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;

public class News {
	String title;
	String link;
	String link2;
	String tinylink;
	
	News(String t, String l, String d){
		this.title = t;
		this.link2 = l;
		ArrayList<String> links = pullLinks(d);
		if(links.size() > 3) this.link = links.get(3).toString();
		else this.link = links.get(1).toString();
		try {
			//this.tinylink = getTinyUrl(this.link);
	        String regex = 	"(?<=/comments/)\\w*";
	   	    Pattern p = Pattern.compile(regex);
	        Matcher m = p.matcher(l);
	        while (m.find()) {
	           this.tinylink = "http://redd.it/" + m.group();
	        }
		} catch (Exception e) {
			this.tinylink = "error!";
		}
	}
	
	public String toString() {
		return ChatColor.RED + "[News] " + ChatColor.YELLOW + title;
	}
	
	public String getTiny(){
		return tinylink;
	}
	
	public String getNews(){
		return title;
	}
	
	private ArrayList<String> pullLinks(String text) {
		ArrayList<String> links = new ArrayList<String>();
		String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(text);
		while(m.find()) {
			String urlStr = m.group();
			if (urlStr.startsWith("(") && urlStr.endsWith(")")){
				urlStr = urlStr.substring(1, urlStr.length() - 1);
			}
			links.add(urlStr);
		}
		return links;
	}
	
	/*private String getTinyUrl(String link) throws Exception{
		String tiny = "http://tinyurl.com/api-create.php?url=";
		
		URL url = new URL(tiny+link);
		
		InputStream in = url.openStream();
	    StringBuffer sb = new StringBuffer();

	    byte [] buffer = new byte[256];

	    while(true){
	        int byteRead = in.read(buffer);
	        if(byteRead == -1)
	            break;
	        for(int i = 0; i < byteRead; i++){
	            sb.append((char)buffer[i]);
	        }
	    }
	    return sb.toString();
	}*/
}
