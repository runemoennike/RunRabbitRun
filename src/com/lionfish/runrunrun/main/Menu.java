package com.lionfish.runrunrun.main;

import java.util.ArrayList;

public class Menu {
	
	private ArrayList<MenuItem> items = new ArrayList<Menu.MenuItem>();
	private String topText;
	private String botText;
	
	private int sel = 0;
	
	public void addItem(String ident, String text) {
		MenuItem item = new MenuItem(ident, text);
		items.add(item);
		if(items.size() == 1) {
			item.isSelected = true;
		}
	}
	
	public void nav(int delta) {
		items.get(sel).isSelected = false;
		
		sel += delta;
		if(sel < 0) {
			sel = 0;
		} 
		if(sel >= items.size()) {
			sel = items.size() - 1;
		}
		System.out.println("lol");
		items.get(sel).isSelected = true;
	}
	
	public ArrayList<MenuItem> getItems() {
		return items;
	}	
	
	public String getSel() {
		return items.get(sel).ident;
	}
	
	public class MenuItem {
		public String ident;
		public String text;
		public boolean isSelected = false;
		
		public MenuItem(String ident, String text) {
			this.ident = ident;
			this.text = text;
		}
	}

	public String getTopText() {
		return topText;
	}

	public void setTopText(String topText) {
		this.topText = topText;
	}

	public void setBotText(String botText) {
		this.botText = botText;
	}

	public String getBotText() {
		return botText+"| | ";
	}
}
