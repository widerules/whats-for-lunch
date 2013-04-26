package com.example.whatsforlunch;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CategoriesAdapter extends BaseAdapter {

	//the system service that read the resource and instantiate the View from it.
	private LayoutInflater _inflater;
	//Save the calling fragment to call back to it
	private EF_Categories_Frag _frag;
	//My collection to display, can be any type of custom object
	String[] _cats;
	
	public CategoriesAdapter(Context context, String[] cats, EF_Categories_Frag fragment) {
        _inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        _cats = cats;
        _frag = fragment;
    }
	
	//The number of items in the collection
	@Override
	public int getCount() {
		return _cats.length;
	}

	//Allow getting the item at the current location
	@Override
	public String getItem(int position) {
		return _cats[position];
	}

	//allow to get the custom identifier of the item. 
	//This could be, but not need, a position. 
	//In this example we change the 0 based index to start form 1 to n.
	@Override
	public long getItemId(int position) {
		return position + 1;
	}

	static class ViewHolder {
        TextView categoryItem;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final int pos = position;

        if (convertView == null) {
            // 1. Create the item view based on resource layout.
            convertView = _inflater.inflate(R.layout.ef_catdetail, null);

            // 2. Find all customized elements.
            holder = new ViewHolder();
            holder.categoryItem = (TextView) convertView.findViewById(R.id.category);
            
            
            convertView.setTag(holder);
        } else {
            // Optimization - don't recreate view if one is available
            holder = (ViewHolder) convertView.getTag();
        }

        // 3. Customize the item view based on position.
        holder.categoryItem.setText(getItem(position));
        holder.categoryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg) { 
            	//Log.d("Categories", "category: "+getItem(pos)+" selected");
            	_frag.updateCategories(getItem(pos));
            }
        });
        
        
        return convertView;
    }  

}
