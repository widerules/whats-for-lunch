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
	    	
	    	updateList();

	        return super.onCreateView(inflater, container, savedInstanceState);
	    }
	    
	    /**
	     * Refreshes items in the listview. The item set is repopulated and the
	     * 	list adapter is reset. Reseting the list adapter refreshes the list.
	     */
	    public void updateList(){
	    	/** Creating an array adapter to store the list **/
	    	String[] cats = ((Enter_Foods) getActivity()).getCategories(); 
	        /** Setting the list adapter for the ListFragment */
	        setListAdapter(new CategoriesAdapter(getActivity(), cats, EF_Categories_Frag.this));
	    }
	    public void updateCategories(String category){
	    	String[] food = ((Enter_Foods) getActivity()).getFoodsInCategory(category);
	    	if(food!=null && food.length > 0){
	    		//A category was selected
	    		setListAdapter(new CategoriesAdapter(getActivity(), food, EF_Categories_Frag.this));
	    	}else{
	    		//A food item was selected within a category
	    		((Enter_Foods) getActivity()).setFoodPicked(category); 
	    	}
	    }
}
