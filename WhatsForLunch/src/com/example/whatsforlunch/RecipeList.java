package com.example.whatsforlunch;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Xml;

public class RecipeList implements Serializable {
	
	private ArrayList<Recipe> rList;
	//private ArrayList<FoodItem> fIngredients;
	private ArrayList<String> ingredients;
	private boolean ready;
	private String url = "http://www.recipepuppy.com/api/?format=xml&i=";
	
	public RecipeList(){
		ingredients = new ArrayList<String>();
		rList = new ArrayList<Recipe>();
		ready = false;
	}
	
	//initialize with a String[] of ingredients
	public RecipeList(String[] food){
		ingredients = new ArrayList<String>(food.length);
		rList = new ArrayList<Recipe>();
		for(String i : food)
			ingredients.add(i);
		
		try {
			url.concat(food[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(int i = 1; i < food.length; i++){
			url.concat(",");
			url.concat(food[i]);
		}
		
		try {
			puppy();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ready = true;
	}
	
	//initialize with an ArrayList<String> of ingredients
	public RecipeList(ArrayList<String> food){
		ingredients = food;
		
		try {
			url.concat(food.get(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(int i = 1; i < food.size(); i++){
			url.concat(",");
			url.concat(food.get(i));
		}
		
		rList = new ArrayList<Recipe>();
		try {
			puppy();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ready = true;
	}
	
	//initialize with a single ingredient
	public RecipeList(String ingredient){
		ingredients = new ArrayList<String>();
		
		try {
			ingredients.add(ingredient);
			url.concat(ingredients.get(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		rList = new ArrayList<Recipe>();
		try {
			puppy();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ready = true;
	}
	
	//add ArrayList<String> of ingredients
	public void addIngredients(ArrayList<String> list) {
		ingredients.addAll(list);
		ready = false;
    }
	
	//add one ingredient
	public void addIngredients(String ingredient) {
		ingredients.add(ingredient);
		ready = false;
    }
	
	//add String[] of ingredients
	public void addIngredients(String[] list) {
		for(String i : list)
			ingredients.add(i);
		ready = false;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }
    
    //returns specified number of recipes
    public ArrayList<Recipe> getRecipes(int min){
    	if(rList.size() >= min)
    		return rList;
    	moreRecipes(min);
    	return rList;
    }
    
    //returns 10 recipes by default
    public ArrayList<Recipe> getRecipes(){
    	return getRecipes(10);
    }
    
    private void moreRecipes(int min){
    	while(rList.size() < min){
			try {
				puppy();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
    
    //gets 10 more recipes
    private void puppy() throws IOException{
    	int page = ingredients.size() + 1;
    	String u = url;
    	u.concat("&p=");
    	u.concat(Integer.toString(page));
    	try {
			InputStream is = downloadUrl(u);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	PuppyHandler pup = new PuppyHandler();
    	rList.addAll(pup.parse(downloadUrl(u)));
    }
    
    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    // from http://developer.android.com/training/basics/network-ops/xml.html   
	private InputStream downloadUrl(String urlString) throws IOException {
		URL url = new URL(urlString);
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setReadTimeout(10000 /* milliseconds */);
	    conn.setConnectTimeout(15000 /* milliseconds */);
	    conn.setRequestMethod("GET");
	    conn.setDoInput(true);
	    // Starts the query
	    conn.connect();
	    return conn.getInputStream();
	}
}

class PuppyHandler extends DefaultHandler {
	private ArrayList<Recipe> rList;
    //private Items items;
    private Recipe recipe;

    public PuppyHandler() {
        rList = new ArrayList<Recipe>();
    }
	
	
	//returns ArrayList<Recipe> of the 10 recipes on the page
	public ArrayList<Recipe> parse(InputStream is) {
		RootElement root = new RootElement("recipes");
		Element recElement = root.getChild("recipe");
		Element recTitle = recElement.getChild("title");
		Element recLink = recElement.getChild("href");
		Element recIngs = recElement.getChild("ingredients");

		// Listen for the end of a text element and set the text as our recipe's
		// title
		recTitle.setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				recipe.setTitle(body);
			}
		});
		 
		// On every <recipe> tag occurrence we create a new Recipe object
		recElement.setStartElementListener(new StartElementListener() {
			public void start(Attributes attributes) {
				recipe = new Recipe();
			}
		});

		// On every </recipe> tag occurrence we add the current Recipe object
		// to rList
		recElement.setEndElementListener(new EndElementListener() {
			public void end() {
				rList.add(recipe);
			}
		});
		
		// Listen for the end of a text element and set the text as our recipe's
		// title
		recTitle.setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				recipe.setTitle(body);
			}
		});
		
		// Listen for the end of an href element and set the text as our recipe's
		// link
		recTitle.setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				recipe.setLink(body);
			}
		});
		
		// here we actually parse the InputStream and return the resulting
		// recipe list
		try {
			Xml.parse(is, Xml.Encoding.UTF_8, root.getContentHandler());
			return rList;
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
		return null;
	}

}