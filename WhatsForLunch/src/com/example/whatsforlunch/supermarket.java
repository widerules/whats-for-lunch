/*package com.example.whatsforlunch;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class supermarket {
	
	
	try{
		HttpClient client = new DefaultHttpClient();
		String url = "http:http://www.SupermarketAPI.com/api.asmx/SearchByProductName?APIKEY=953413ffd7&ItemName=";
		String item = name;
		HttpGet get = new HttpGet(url+item);
		HttpResponse responseGet = client.execute(get);
		HttpEntity resEntityGet = responseGet.getEntity();
		if(resEntityGet != null) {
			String response = EntityUtils.toString(resEntityGet);
			Log.i("GET RESPONSE", response);
			output.setText(reponse);
		}
	}
}*/
