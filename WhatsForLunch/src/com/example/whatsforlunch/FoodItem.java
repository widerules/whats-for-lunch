package com.example.whatsforlunch;

import java.util.ArrayList;
import java.util.List;

/****FoodItem**********************************************
 * Each Item consists of
 * 		the name of the item,
 * 		the condition of the item,
 * 		the name of the trip,
 * 		the date of the trip,
 * 		the expiration date of the item
 *
 */
	
public class FoodItem{
	
	private String itemName = "";
	private String condition;
	private String tripName = "";
	//Date is relative to GMT
	//Date is adjusted to midnight of the current day
	//TODO: Might want to look into Java Calendar and Date classes
	private long now = System.currentTimeMillis();
	private long tripDate = now - now % 86400000;
	private long expDate;

	public FoodItem(){}
	public FoodItem(String name){
		this.itemName = name;
	}

	public void setItemName(String name){
		this.itemName = name;
	}
	public void setCondition(String cond){
		this.condition = cond;
	}
	public void setTripName(String name){
		this.tripName = name;
	}
	public void setPurchaseDate(long date){
		this.tripDate = date;
	}
	public void setExpiration(long date){
		this.expDate = date;
	}
	public String getItemName(){
		return itemName;
	}
	public String getCondition(){
		return condition;
	}
	public String getTripName(){
		return tripName;
	}
	public long getDatePurchased(){
		return tripDate;
	}
	public long getExpiration(){
		return expDate;
	}
	
}