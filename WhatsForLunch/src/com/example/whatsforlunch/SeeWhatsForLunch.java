package com.example.whatsforlunch;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class SeeWhatsForLunch extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_foods_table);
		RecipeList rec = new RecipeList();
		ArrayList<Recipe> rList = rec.getRecipes();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.see_whats_for_lunch, menu);
		return true;
	}

}
