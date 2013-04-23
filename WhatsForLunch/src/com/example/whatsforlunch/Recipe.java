package com.example.whatsforlunch;

import java.util.ArrayList;
import java.util.Arrays;

public class Recipe {
	private String name;
	private String url;
	private ArrayList<String> ingredients;
	
	public Recipe(){
		name = null;
		url = null;
		ingredients = null;
	}
	
	public Recipe(String t, String u, String[] list){
		name = t;
		url = u;
		ingredients = new ArrayList<String>(list.length);
		for(String i : list)
			ingredients.add(i);
	}
	
	public void setName(String t){
		name = t;
	}
	
	public void setLink(String u){
		url = u;
	}
	
	public void setIngredients(String[] list){
		ingredients = new ArrayList<String>(list.length);
		for(String i : list)
			ingredients.add(i);
	}
	
	public String getName(){
		return name;
	}
	
	public String getLink(){
		return url;
	}

}
