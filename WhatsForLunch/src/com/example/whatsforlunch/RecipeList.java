package com.example.whatsforlunch;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.text.format.DateFormat;
import android.util.Xml;
import android.webkit.WebView;

public class RecipeList implements Serializable {
	
	private ArrayList<Recipe> rList;
	private ArrayList<String> ingredients;
	private boolean ready;
	public String test;
	private String url = "http://www.recipepuppy.com/api/?format=xml&i=";
	public AsyncTask<String,Void,Void> task;
	
	public RecipeList(){
		ingredients = new ArrayList<String>();
		rList = new ArrayList<Recipe>();
		ready = false;
	}
	
	//initialize with a String[] of ingredients
	public RecipeList(String...food){
		ingredients = new ArrayList<String>(food.length);
		rList = new ArrayList<Recipe>();
		for(String i : food)
			ingredients.add(i);
		
		try {
			url = url.concat(food[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(int i = 1; i < food.length; i++){
			url = url.concat(",");
			url = url.concat(food[i]);
		}
		getRecipes();
		ready = true;
	}
	
	//initialize with an ArrayList<String> of ingredients
	public RecipeList(ArrayList<String> food){
		ingredients = food;
		
		try {
			url = url.concat(food.get(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(int i = 1; i < food.size(); i++){
			url = url.concat(",");
			url = url.concat(food.get(i));
		}
		
		rList = new ArrayList<Recipe>();
		getRecipes();
		ready = true;
	}
	
	//add ArrayList<String> of ingredients
	// TODO: doesn't concat ingredients
	public void addIngredients(ArrayList<String> list) {
		ingredients.addAll(list);
		ready = false;
    }
	
	//add String[] of ingredients
	public void addIngredients(String...list) {
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
    	int oSize = rList.size();
    	while(rList.size() < min){
			try {
				puppy();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(!(oSize < rList.size()))
				break;
    	}
    }
    
    // gets 10 more recipes
    private void puppy() throws IOException{
    	int page = (rList.size() / 10) + 1;
    	String u = url;
    	u = u.concat("&p=");
    	u = u.concat(Integer.toString(page));
    	test = u;
    	task = new AsyncPuppy().execute(u);
    }
    
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
 
    
    private class AsyncPuppy extends AsyncTask<String, Void, Void> {
    	protected Void doInBackground(String... urls) {
    		PuppyHandler pup = new PuppyHandler();
    		//ArrayList<Recipe> result = new ArrayList<Recipe>();
        	try {
    			pup.parse(downloadUrl(urls[0]));
    		} catch (IOException e) {
    			e.printStackTrace();
    		}	
        	//return result;
			return null;
    	}
    	
    	protected void onPostExecute() {
    	    
    	}
    	/*
    	// Given a string representation of a URL, sets up a connection and gets
        // an input stream.
        // from http://developer.android.com/training/basics/network-ops/xml.html   
    	private InputStream downloadUrl(String urlString) throws IOException {
    		URL url = new URL(urlString);
    	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    	    conn.setReadTimeout(10000 /* milliseconds * /);
    	    conn.setConnectTimeout(15000 /* milliseconds * /);
    	    conn.setRequestMethod("GET");
    	    conn.setDoInput(true);
    	    // Starts the query
    	    conn.connect();
    	    return conn.getInputStream();
    	}
    	*/
    }
    
    
    private class PuppyHandler extends DefaultHandler {
        //private Items items;
        private Recipe recipe;

        public PuppyHandler() {
            rList = new ArrayList<Recipe>();
        }
    	
    	
    	//returns ArrayList<Recipe> of the 10 recipes on the page
    	public void parse(InputStream is) {
    		RootElement root = new RootElement("recipes");
    		Element recElement = root.getChild("recipe");
    		Element recTitle = recElement.getChild("title");
    		Element recLink = recElement.getChild("href");
    		Element recIngs = recElement.getChild("ingredients");
    		 
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
    				recipe.setName(body);
    				// TODO: work = body;
    			}
    		});
    		
    		// Listen for the end of an href element and set the text as our recipe's
    		// link
    		recLink.setEndTextElementListener(new EndTextElementListener() {
    			public void end(String body) {
    				recipe.setLink(body);
    			}
    		});
    		
    		// Listen for the end of an ingredients element and set the text as our recipe's
    		// ingredients list
    		recIngs.setEndTextElementListener(new EndTextElementListener() {
    			public void end(String body) {
    				recipe.setIngredients(body.split("\\s*,\\s*"));
    			}
    		});
    		
    		// Parse InputStream and return the resulting recipe list
    		try {
    			Xml.parse(is, Xml.Encoding.UTF_8, root.getContentHandler());
    			//return rList;
    		} catch (SAXException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		 
    	}
    }
}