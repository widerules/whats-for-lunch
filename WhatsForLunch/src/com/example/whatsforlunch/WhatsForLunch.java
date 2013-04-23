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

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Xml;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class WhatsForLunch extends ListActivity {
	ArrayList<Recipe> wfl = new ArrayList<Recipe>();
	ArrayList<String> rList = new ArrayList<String>();
	ArrayAdapter<String> rAd;
	RecipeList rec;
	boolean all_foods;
	TextView recipe_type;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.whats_for_lunch);
		all_foods = true;
		recipe_type = (TextView)this.findViewById(R.id.recipeType);
		final ListView listView = getListView();
		listView.setItemsCanFocus(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		listView.setTextFilterEnabled(true);
		listView.setOnItemClickListener(new OnItemClickListener(){
		
		 @Override
		 public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			 Recipe r = wfl.get(position);
			 Intent browse = new Intent(Intent.ACTION_VIEW , Uri.parse(r.getLink()));
			 startActivity(browse);
		 }
		
		});
		rAd = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rList);
		setListAdapter(rAd);
		foodChange();
	}
	
	private void foodChange(){
		Database_Manager myDb = new Database_Manager(this);
	    Cursor myCur = myDb.getCursor();
	    ArrayList<String> i = new ArrayList<String>();
	    myCur.moveToFirst();
	    if(!myCur.isAfterLast()){
			if(all_foods){
				while(true){
		        	i.add(myCur.getString(1));
		        	myCur.moveToNext();
		        	if(myCur.isAfterLast())
		        		break;
		        }
			}else{
				while(true){
					if(myCur.getString(2).equals("Aged"))
		        		i.add(myCur.getString(1));
		        	myCur.moveToNext();
		        	if(myCur.isAfterLast())
		        		break;
		        }
			}
	    }
		rec = new RecipeList();
		rec.addIngredients(i);
		wfl.clear();
		rec.getRecipes();
	}
	
	public void foodButton(View view){
		if(all_foods)
			all_foods = false;
		else
			all_foods = true;
		Button b = (Button)view.findViewById(R.id.foodChange);
		if(all_foods){
			b.setText("Expiring Foods");
			recipe_type.setText("Generated Recipes(All Foods)");
		}else{
			b.setText("All Foods");
			recipe_type.setText("Generated Recipes(Expiring)");
		}
		foodChange();
	}
	
	public void moreRecipes(View view){
		rec.getRecipes();
	}

	private void updateList() {
		ArrayList<String> rec_strings = new ArrayList<String>(wfl.size());
		String s;
		for(Recipe r : wfl){
			s = r.getName();
			s = s.replaceAll("\\n","");
			s = s.replaceAll("&amp;","&");
			rec_strings.add(s);
		}
		rList.clear();
		rList.addAll(rec_strings);
		if(rList.isEmpty())
			rList.add("\nNo recipes found.\nYou may have spelled your ingredients in a way we don't recognize or you are out of food at the moment.\n");
		rAd.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.whats_for_lunch, menu);
		return true;
	}

	@SuppressWarnings("serial")
	class RecipeList implements Serializable {
		
		//private ArrayList<Recipe> rList;
		private ArrayList<String> ingredients;
		private ArrayList<String> unused;
		public String test;
		private String url = "http://www.recipepuppy.com/api/?format=xml&i=";
		//public AsyncTask<String,Void,Void> task;
		
		public RecipeList(){
			ingredients = new ArrayList<String>();
			unused = new ArrayList<String>();
			concatIngredients();
		}
		
		//initialize with a String[] of ingredients
		public RecipeList(String...food){
			ingredients = new ArrayList<String>(food.length);
			unused = new ArrayList<String>(food.length);
			for(String i : food)
				unused.add(i);
			concatIngredients();
		}
		
		//initialize with an ArrayList<String> of ingredients
		public RecipeList(ArrayList<String> food){
			ingredients = new ArrayList<String>();
			unused = food;			
			concatIngredients();
		}
		
		//add ArrayList<String> of ingredients
		public void addIngredients(ArrayList<String> list) {
			unused = list;
			concatIngredients();
	    }
		
		//add String[] of ingredients
		public void addIngredients(String...list) {
			for(String i : list)
				unused.add(i);
			concatIngredients();
	    }
	
	    public ArrayList<String> getIngredients() {
	        return ingredients;
	    }
	    
	    private void concatIngredients(){
	    	if(!unused.isEmpty())
	    		url = url.concat(unused.get(0));
			
			for(int i = 1; i < unused.size(); i++){
				url = url.concat(",");
				url = url.concat(unused.get(i));
			}
			ingredients.addAll(unused);
			unused.clear();
	    }
	    
	    //returns specified number of recipes
	    private void getRecipes(int min){
	    	if(wfl.size() >= min)
	    		return;
	    	moreRecipes(min);
	    }
	    
	    //returns 10 recipes by default
	    public void getRecipes(){
	    	//getRecipes(10);
	    	try {
				puppy();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    
	    private void moreRecipes(int min){
	    	//int oSize = wfl.size();
	    	if(wfl.size() < min){
				try {
					puppy();
				} catch (IOException e) {
					e.printStackTrace();
				}
				//if(!(oSize < wfl.size()))
				//	break;
	    	}
	    }
	    
	    // gets 10 more recipes
	    private void puppy() throws IOException{
	    	int page = (wfl.size() / 10) + 1;
	    	String u = url;
	    	u = u.concat("&p=");
	    	u = u.concat(Integer.toString(page));
	    	test = u;
	    	new AsyncPuppy().execute(u);
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
	 
	    
	    class AsyncPuppy extends AsyncTask<String, Void, Void> {
	    	protected Void doInBackground(String... urls) {
	    		PuppyHandler pup = new PuppyHandler();
	    		//ArrayList<Recipe> result = new ArrayList<Recipe>();
	        	try {
	    			pup.parse(downloadUrl(urls[0]));
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
				return null;
	    	}

	    	@Override
			protected void onPostExecute(Void result) {
	    		runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                    	updateList();
                    }
                });
	    	    
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
	    
	    
	    class PuppyHandler extends DefaultHandler {
	        //private Items items;
	        private Recipe recipe;
	
	        public PuppyHandler() {
	            super();
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
	    				wfl.add(recipe);
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
}