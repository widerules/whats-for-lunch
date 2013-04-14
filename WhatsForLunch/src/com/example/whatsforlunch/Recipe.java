package com.example.whatsforlunch;

import java.util.ArrayList;
import java.util.Arrays;

public class Recipe {
	private String title;
	private String url;
	private ArrayList<String> ingredients;
	
	public Recipe(){
		title = null;
		url = null;
		ingredients = null;
	}
	
	public Recipe(String t, String u, String[] list){
		title = t;
		url = u;
		ingredients = new ArrayList<String>(list.length);
		for(String i : list)
			ingredients.add(i);
	}
	
	public void setTitle(String t){
		title = t;
	}
	
	public void setLink(String u){
		url = u;
	}
	
	public void setIngredients(String[] list){
		ingredients = new ArrayList<String>(list.length);
		for(String i : list)
			ingredients.add(i);
	}
	
	public void setIngredients(String list){
		ingredients = (ArrayList<String>) Arrays.asList(list.split("\\s*,\\s*"));
	}

}
