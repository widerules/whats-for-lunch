package com.example.whatsforlunch;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

public class EF_Categories_Frag extends ListFragment {
	
	LayoutInflater inflater;
	String[] countries = new String[] {
	        "Fruit",
	        "Vegetables",
	        "Dairy",
	        "Sri Lanka",
	        "Bangladesh",
	        "Nepal",
	        "Afghanistan",
	        "North Korea",
	        "South Korea",
	        "Japan"
	    };
		
	 
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
	    	//final String[] itemNames = ((Enter_Foods) getActivity()).getTripItemNames();
	    	
	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
	        		inflater.getContext(), 							//Context
	        		android.R.layout.simple_list_item_1,			//List detail
	        		countries);										//List contents
	 
	        /** Setting the list adapter for the ListFragment */
	        setListAdapter(adapter);
	    }
}
