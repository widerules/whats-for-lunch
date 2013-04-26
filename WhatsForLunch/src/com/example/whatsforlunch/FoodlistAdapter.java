package com.example.whatsforlunch;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class FoodlistAdapter extends BaseAdapter {

	//the system service that read the resource and instantiate the View from it.
	private LayoutInflater _inflater;
	//Save the calling fragment to call back to it
	private EF_CurTrip_Frag _frag;
	//My collection to display, can be any type of custom object
	FoodItem[] _items;
	
	public FoodlistAdapter(Context context, FoodItem[] items, EF_CurTrip_Frag fragment) {
        _inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        _items = items;
        _frag = fragment;
    }
	
	//The number of items in the collection
	@Override
	public int getCount() {
		return _items.length;
	}

	//Allow getting the item at the current location
	@Override
	public Object getItem(int position) {
		return _items[position];
	}
	public String getItemName(int position){
		return ((FoodItem)_items[position]).getItemName();
	}
	public String getItemDate(int position){
		return ((FoodItem) _items[position]).getExpiration();
	}

	//allow to get the custom identifier of the item. 
	//This could be, but not need, a position. 
	//In this example we change the 0 based index to start form 1 to n.
	@Override
	public long getItemId(int position) {
		return position + 1;
	}

	static class ViewHolder {
        TextView nameText;
        TextView dateText;
        ImageButton delete;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final int pos = position;

        if (convertView == null) {
            // 1. Create the item view based on resource layout.
            convertView = _inflater.inflate(R.layout.ef_itemdetail, null);

            // 2. Find all customized elements.
            holder = new ViewHolder();
            holder.delete = (ImageButton) convertView.findViewById(R.id.deleteItem);
            holder.nameText = (TextView) convertView
                    .findViewById(R.id.enter_name);
            holder.dateText = (TextView) convertView.findViewById(R.id.enter_xdate);
            
            
            convertView.setTag(holder);
        } else {
            // Optimization - don't recreate view if one is available
            holder = (ViewHolder) convertView.getTag();
        }

        // 3. Customize the item view based on position.
        holder.nameText.setText(getItemName(position));
        holder.dateText.setText(getItemDate(position));
        //holder.delete.setFocusable(false);
        holder.delete.setId(position);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg) {
                //TODO delete row 
            	Log.d("Current Trip", "row "+pos+" deleted");
            	_frag.removeItem(pos);
            }
        });

        return convertView;
    }  

}
