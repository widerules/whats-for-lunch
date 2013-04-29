package com.example.whatsforlunch;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
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
import android.widget.AbsListView; 		
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class WhatsForLunch extends ListActivity {
	ArrayList<Recipe> wfl = new ArrayList<Recipe>();
	ArrayList<String> rList = new ArrayList<String>();
	ArrayAdapter<String> rAd;
	RecipeList rec;
	boolean all_foods;
	boolean search_only; 		
	boolean ready = false;
	TextView recipe_type;
	Database_Manager myDb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.whats_for_lunch);
		myDb = new Database_Manager(this);
		rec = new RecipeList();
		all_foods = true; 		
		search_only = false;
		recipe_type = (TextView)this.findViewById(R.id.recipeType);
		final ListView listView = getListView();
		listView.setItemsCanFocus(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		listView.setTextFilterEnabled(true);
		listView.setOnItemClickListener(new OnItemClickListener(){

		// go to recipe's url 
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			Recipe r = wfl.get(position);
			Intent browse = new Intent(Intent.ACTION_VIEW , Uri.parse(r.getLink()));
			startActivity(browse);
		}
		});
		
		// get more recipes when scrolling down 		
		listView.setOnScrollListener(new OnScrollListener(){ 		

			@Override 		
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) { 		
				if(ready){ 		
					int lastItem = firstVisibleItem + visibleItemCount; 		
					if(lastItem == totalItemCount) { 		
						EditText e = (EditText) findViewById(R.id.query); 		
						String s = e.getText().toString(); 		
						if(!search_only || !s.equals("")) 		
							rec.getRecipes(); 		
					} 		
				} 		
			} 		

			@Override 		
			public void onScrollStateChanged(AbsListView view, int scrollState) { 		
				//nothing 		
			} 		

		});
		
		rAd = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rList);
		setListAdapter(rAd);
		foodChange();
	}
	
	public void onResume(){
		super.onResume();
		printUserRecipes();
	}
	
	private void printUserRecipes() {
		String file = "userRecipes";
		//hard to tell how to divide sections, users can add new lines, commas, and several
		//other things you would normally check for, so need a unique identifier for sections 
		//these are purposely made long so that there isn't an accidental match to user entered text
		//probably not the best way to do it but it works, also makes it easier to split as desired when reading
		String newField = "uniqueStartofFieldMarkerabcxyz ";
		String endRecipe = "uniqueIdentifierMarksEndOfOneRecipeabcxyz ";
		String [] out;
		
		
		
		TextView viewOut =(TextView) this.findViewById(R.id.displayUserRecipes);
		  String eol = System.getProperty("line.separator");
		  BufferedReader input = null;
		  String line;
		  StringBuffer buffer = new StringBuffer();
		  
		  try {
		    input = new BufferedReader(new InputStreamReader(openFileInput(file)));
		    
		    while ((line = input.readLine()) != null) {
		    	buffer.append(line);
		    }
		  } catch (Exception e) {
		     e.printStackTrace();
		  } finally {
		  if (input != null) {
		    try {
		    input.close();
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
		    }
		  }
		 line= buffer.toString();
		 out=line.split(newField);
		 for(int i=0; i<out.length;i++){
			 //replace endRecipe string with a newline
			 if(out[i].contains(endRecipe))
			 	 viewOut.append(out[i].replace(endRecipe, eol));
			 else
				 viewOut.append(out[i]);
			 
		 }
		 
		 
		 
		 
	}

	public void launchUserRecipes(View view){
		Intent intent = new Intent(this, UserRecipes.class);
		startActivity(intent);
	}
	
	
	private void foodChange(){
		ready = false;
		//Database_Manager myDb = new Database_Manager(this);
	    Cursor myCur = myDb.getCursor();
	    ArrayList<String> i = new ArrayList<String>();
	    myCur.moveToFirst();
	    if(!search_only){
		    if(!myCur.isAfterLast()){
				if(all_foods){
					while(!myCur.isAfterLast()){
			        	i.add(myCur.getString(1));
			        	myCur.moveToNext();
			        }
				}else{
					int DAYS_BEFORE_EXPIRATION = -3;
					String[] date = new String[3];
					DateTime now = new DateTime();
					DateTime exp;
					while(!myCur.isAfterLast()){
					    date = myCur.getString(5).split("/");
					    exp = new DateTime(Integer.parseInt(date[2]), Integer.parseInt(date[0]), Integer.parseInt(date[1]), 12, 0);
					    exp = exp.plusDays(DAYS_BEFORE_EXPIRATION);
					    if(now.isAfter(exp)){
					    	i.add(myCur.getString(1));
					    }
			        	myCur.moveToNext();
			        }
				}
		    }
	    }
		rec = new RecipeList();
		wfl.clear();
		if(i.isEmpty() && !search_only){
			rList.clear();
			if(all_foods)
				rList.add("\nNo food in the system.\n");
			else
				rList.add("\nNo expiring foods.\n");
			rAd.notifyDataSetChanged();
		}else{
			EditText e = (EditText) findViewById(R.id.query);
    		String s = e.getText().toString();
    		if(!search_only || !s.equals("")){
				rec.addIngredients(i);
				rec.getRecipes();
				ready = true;
    		} else{
    			rList.clear();
    			rList.add("\nEnter search terms seperated by spaces and press \"Search\".\n");
    			rAd.notifyDataSetChanged();
    		}
		}
	}
	
	public void foodButton(View view){
		if(search_only){
			search_only = false;
			all_foods = true;
		}else if(all_foods){
			all_foods = false;
		}else{
			search_only = true;
		}
		Button b = (Button)view.findViewById(R.id.foodChange);
		if(search_only){
			b.setText("All Foods");
			recipe_type.setText("Recipes(Search Only)");
		}else if(all_foods){
			b.setText("Expiring Foods");
			recipe_type.setText("Recipes(All Foods)");
		}else{
			b.setText("Search Only");
			recipe_type.setText("Recipes(Expiring)");
		}
		foodChange();
	}
	
	public void search(View view){
		wfl.clear();
		EditText e = (EditText) findViewById(R.id.query);
    	String s = e.getText().toString();
    	if(!search_only || !s.equals(""))
    		rec.getRecipes();
    	else{
    		wfl.clear();
    		updateList();
    	}
	}

	private void updateList() {
		ready = false;
		ArrayList<String> rec_strings = new ArrayList<String>(wfl.size());
		for(int n = 10; n < wfl.size(); n += 10){
			while(n < wfl.size())
				if(wfl.get(n-10).getName().equals(wfl.get(n).getName()))
					wfl.remove(wfl.get(n-10));
		}
		for(Recipe r : wfl){
			rec_strings.add(r.getName());
		}
		rList.clear();
		rList.addAll(rec_strings);
		if(rList.isEmpty()){
			if(rec.getIngredients().isEmpty()){
				if(all_foods)
					rList.add("\nNo food in the system.\n");
				else if(!search_only)
					rList.add("\nNo expiring foods.\n");
				else{
					EditText e = (EditText) findViewById(R.id.query);
			    	String s = e.getText().toString();
					if(s.equals("")){
						rList.add("\nEnter search terms seperated by spaces and press \"Search\".\n");
					}else{
						rList.add("\nNo recipes found.\n");
					}
				}
			}else
				rList.add("\nNo recipes found.\nYou may have spelled your ingredients or query in a way we don't recognize.\n");
		}else{
			ready = true;
		}
		rAd.notifyDataSetChanged();
	}

	@SuppressWarnings("serial")
	class RecipeList implements Serializable {
		
		//private ArrayList<Recipe> rList;
		private ArrayList<String> ingredients;
		private ArrayList<String> unused;
		//public String test;
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
			for(String i : food){
				if(i.indexOf(",") != -1)
					i = i.substring(0, i.indexOf(","));
				if(i.indexOf("\"") != -1)
					i = i.substring(0, i.indexOf("\""));
				if(i.indexOf("(") != -1)
					i = i.substring(0, i.indexOf("("));
				i = i.replaceAll(" ", "%20");
				unused.add(i);
			}
			concatIngredients();
		}
		
		//initialize with an ArrayList<String> of ingredients
		public RecipeList(ArrayList<String> food){
			ingredients = new ArrayList<String>(food.size());
			unused = new ArrayList<String>(food.size());
			for(String i : food){
				if(i.indexOf(",") != -1)
					i = i.substring(0, i.indexOf(","));
				if(i.indexOf("\"") != -1)
					i = i.substring(0, i.indexOf("\""));
				if(i.indexOf("(") != -1)
					i = i.substring(0, i.indexOf("("));
				i = i.replaceAll(" ", "%20");
				unused.add(i);
			}
			concatIngredients();
		}
		
		//add ArrayList<String> of ingredients
		public void addIngredients(ArrayList<String> list) {
			unused = new ArrayList<String>(list.size());
			for(String i : list){
				if(i.indexOf(",") != -1)
					i = i.substring(0, i.indexOf(","));
				if(i.indexOf("\"") != -1)
					i = i.substring(0, i.indexOf("\""));
				if(i.indexOf("(") != -1)
					i = i.substring(0, i.indexOf("("));
				i = i.replaceAll(" ", "%20");
				unused.add(i);
			}
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
	    /*
	     * Not used for now
	     * 
	    //returns specified number of recipes
	    private void getRecipes(int min){
	    	if(wfl.size() >= min)
	    		return;
	    	moreRecipes(min);
	    }
	    */
	    
	    //returns 10 recipes by default
	    public void getRecipes(){
	    	//getRecipes(10);
	    	if(ingredients.isEmpty() && !search_only){ 		
	    		updateList(); 		
	    		return; 		
	    	}
	    	try {
				puppy();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    
	    /*
	     * not used for now
	     * 
	    private void moreRecipes(int min){
	    	// int oSize = wfl.size();
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
	    */
	    
	    // gets 10 more recipes
	    private void puppy() throws IOException{
	    	String u = url;
	    	EditText q = (EditText) findViewById(R.id.query);
	    	String query = q.getText().toString();
	    	u = u.concat("&q=");
	    	u = u.concat(query);
	    	int page = (wfl.size() / 10) + 1;
	    	u = u.concat("&p=");
	    	u = u.concat(Integer.toString(page));
	    	System.out.println(u);
	    	//test = u;
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
	    		synchronized(this){
		    		PuppyHandler pup = new PuppyHandler();
		    		//ArrayList<Recipe> result = new ArrayList<Recipe>();
		        	try {
		        		for(String u : urls)
		        			pup.parse(downloadUrl(u));
		    		} catch (IOException e) {
		    			e.printStackTrace();
		    		}
					return null;
	    		}
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
	    				body = body.replaceAll("\\n","");
	    				body = body.replaceAll("&amp;","&");
	    				body = body.replaceAll("&quot;","&");
	    				body = body.replaceAll("\\t","");
	    				body = body.replaceAll("\\f","");
	    				body = body.replaceAll("\\r","");
	    				recipe.setName(body);
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
	    /*
	    class OneAsyncThreadPoolExecutor extends ThreadPoolExecutor{
			
			public OneAsyncThreadPoolExecutor() { 
				super(1, 1, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
			}
	    	
	    }
	    */
	    
	}
}