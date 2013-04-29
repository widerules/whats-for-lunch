package com.example.whatsforlunch;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class UserRecipes extends Activity {
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_recipes);
	}
	

	
	public void createRecipe(View view) throws IOException{
		
		String file = "userRecipes";
		ArrayList<String> userRes = new ArrayList<String>();
		String eol = System.getProperty("line.separator");
		//hard to tell how to divide sections, users can add new lines, commas, and several
		//other things you would normally check for, so need a unique identifier for sections 
		//these are purposely made long so that there isn't an accidental match to user entered text
		//probably not the best way to do it but it works, also makes it easier to split as desired when reading
		String newField = "uniqueStartofFieldMarkerabcxyz ";
		String endRecipe = "uniqueIdentifierMarksEndOfOneRecipeabcxyz ";
		
		//get text fields
		EditText nameView = (EditText) findViewById(R.id.user_recipes_name);
		String nameText = nameView.getText().toString();
		EditText ingredientView = (EditText) findViewById(R.id.user_recipes_ingredients);
		String ingredientText = ingredientView.getText().toString();
		EditText recipeView = (EditText) findViewById(R.id.user_recipe_recipe);
		String recipeText = recipeView.getText().toString();
		
		//Format text before writing to file
		if(nameText!=""){
			nameText = newField + nameText + " ";
			userRes.add(nameText);
		}
		if(ingredientText!=""){
			ingredientText =newField + ingredientText + " ";
			userRes.add(ingredientText);
		}
		if(recipeText!=""){
			recipeText = newField + recipeText + endRecipe;
			userRes.add(recipeText);
		}
	    BufferedWriter writer = null;
		  try {
		    writer = new BufferedWriter(new OutputStreamWriter(openFileOutput(file, MODE_APPEND)));
	        while(!userRes.isEmpty()){
		       writer.write(userRes.remove(0));
	        }
		  } catch (Exception e) {
		      e.printStackTrace();
		  } finally {
		    if (writer != null) {
		    try {
		      writer.close();
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
		    }
		  }
	finish();	  
	} 	
	
	
}



