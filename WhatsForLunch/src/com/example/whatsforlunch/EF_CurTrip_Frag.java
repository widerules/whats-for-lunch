package com.example.whatsforlunch;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class EF_CurTrip_Frag extends ListFragment {
	
	LayoutInflater inflater;
	String[] countries = new String[] {
	        "India",
	        "Pakistan",
	        "Sri Lanka",
	        "China",
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
	    	final FoodItem[] foods = ((Enter_Foods) getActivity()).getTripItems(); 
	        /** Setting the list adapter for the ListFragment */
	        setListAdapter(new FoodlistAdapter(getActivity(), foods));
	    }
}
