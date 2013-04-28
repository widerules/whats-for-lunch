package com.example.whatsforlunch;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class EF_Categories_Frag extends ListFragment{
	
	LayoutInflater inflater;

	    @Override
	    public View onCreateView(LayoutInflater inflate, ViewGroup container,Bundle savedInstanceState) {
	    	inflater = inflate;
	    	
	    	setCategories();

	        return super.onCreateView(inflater, container, savedInstanceState);
	    }
	    
	    /**
	     * Refreshes categories in the listview. The category set is repopulated and the
	     * 	list adapter is reset. Reseting the list adapter refreshes the list.
	     */
	    public void setCategories(){
	    	/** Setting the list adapter for the ListFragment */
	    	String[] cats = ((Enter_Foods) getActivity()).getCategories(); 
	        setListAdapter(new CategoriesAdapter(getActivity(), cats, EF_Categories_Frag.this));
	    }
	    /**
	     * Enters the selected category. The listview is repopulated with items
	     * from the selected category and the adapter is reset.
	     * 
	     * If the selection is not a known category, it is assumed to be a food
	     * selection. The selection is then entered into the appropriate
	     * edit text field.
	     * @param category
	     */
	    public void enterCategory(String category){
	    	String[] food = ((Enter_Foods) getActivity()).getFoodsInCategory(category);
	    	//A category was selected
	    	if(food!=null && food.length > 0){
	    		setListAdapter(new CategoriesAdapter(getActivity(), food, EF_Categories_Frag.this));
	    	}
	    	//A food item was selected within a category
	    	else{
	    		((Enter_Foods) getActivity()).setFoodPicked(category); 
	    	}
	    }
}
