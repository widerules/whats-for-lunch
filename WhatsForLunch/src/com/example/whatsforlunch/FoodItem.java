package com.example.whatsforlunch;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
	public static final String default_date = "unknown exp date";
	private String itemName;
	private String condition = "Normal";
	private String tripName = "default trip name";
	//Date is relative to GMT
	//Date is adjusted to midnight of the current day
	//TODO: Might want to look into Java Calendar and Date classes
	//use joda --Ford
	private String tripDate = Today();
	private String expDate = default_date;

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
	public void setPurchaseDate(String date){
		this.tripDate = date;
	}
	public void setExpiration(String date){
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
	public String getDatePurchased(){
		return tripDate;
	}
	public String getExpiration(){
		return expDate;
	}
	private String Today(){
		Calendar c = Calendar.getInstance();

		SimpleDateFormat df = new SimpleDateFormat("MMM-dd-yyyy");
		
		return df.format(c.getTime());
	}
	
}