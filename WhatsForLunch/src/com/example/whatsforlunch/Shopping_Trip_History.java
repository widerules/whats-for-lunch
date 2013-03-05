package com.example.whatsforlunch;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Carlos
 * Each user will have a single, unique Shopping_Trip_History.
 * A user's history contains previous active shopping trips.
 * Once all food has been used in a trip, the trip should be removed.
 *
 */
public class Shopping_Trip_History {
	
	private List<ShoppingTrip> trips = new ArrayList<ShoppingTrip>();
	
	public Shopping_Trip_History(){}
	
	public void addTrip(){
		ShoppingTrip st = new ShoppingTrip();
		trips.add(st);
	}
	public void addTrip(ShoppingTrip st){
		trips.add(st);
	}
	public void removeTrip(int tripIndex){
		trips.remove(tripIndex);
	}
	public ShoppingTrip getLastTrip(){
		return trips.get(trips.size()-1);
	}
	public List<ShoppingTrip> getAllTrips(){
		return trips;
	}
	
	public static class ShoppingTrip{
		//Date is relative to GMT
		//TODO: Might want to look into Java Calendar and Date classes
		private long now = System.currentTimeMillis();
		private long tripDate = now - now % 86400000;
		private String name = "";
		
		//List of items bought
		private List<Item> items = new ArrayList<Item>();
		
		public ShoppingTrip(){}
		public ShoppingTrip(String name){		//TODO customizable trips?
			this.name = name;
		}
		
		public void addItem(String itemName){
			//If item not already entered
			if(!this.containsItem(itemName)){
				Item item = new Item(itemName);
				items.add(item);
			}
			//If item already entered, add to quantity
			else{
				this.getItem(itemName).addToQuantity(1);
			}
		}
		public void removeItem(String itemName){
			for(Item i : items){
				if(i.getName().equals(itemName)){
					items.remove(i);
				}
			}
		}
		public void clear(){
			for(Item i : items){
				items.remove(i);
			}
		}
		public Item getItem(String itemName){
			for(Item i : items){
				if(i.getName().equals(itemName)){
					return i;
				}
			}
			return null;
		}
		private boolean containsItem(String itemName){
			for(Item i : items){
				if(i.getName().equals(itemName)){
					return true;
				}
			}
			return false;
		}
		public List<Item> getAllItems(){
			return items;
		}
		public String getItemNamesString(){
			List<String> nameList = new ArrayList<String>();
			for(Item i : items){
				nameList.add(i.getName());
			}
			String names = nameList.toString()
					.replace("[", "")
					.replace("]", "")
					.replace(",", "\n");
			return names;
		}
		public long getDate(){
			return tripDate;
		}
		public boolean isEmpty(){
			return items.isEmpty();
		}
	}
	private static class Item{
		private String name = "";
		private long expirationDate;	//TODO set expDates
		private int quantity=0;
		
		public Item(String name){
			this.name = name;
		}
		
		public void setName(String name){
			this.name = name;
		}
		public void setExpiration(long date){
			this.expirationDate = date;
		}
		public void setQuantity(int q){
			this.quantity = q;
		}
		public void addToQuantity(int q){
			this.quantity += q;
		}
		public String getName(){
			return this.name;
		}
		public long getExpiration(){
			return expirationDate;
		}
		public int getQuantity(){
			return quantity;
		}
	}
	
}
