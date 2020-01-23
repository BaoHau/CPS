package no.hiof.project1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class ZStickDriver {

	public String getItemState(String itemUrl) throws IOException {
		URL url = new URL(itemUrl + "/state");
		StringBuilder response = new StringBuilder();
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		BufferedReader buff = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String data;
		while((data = buff.readLine()) != null) {
			response.append(data);
		}
		buff.close();
		return response.toString();
	}
	
	public void setItemState(String itemUrl, String newState) throws IOException {
		URL url = new URL(itemUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Content-Type", "text/plain");
		
		OutputStream os = conn.getOutputStream();
	    byte[] input = newState.getBytes("utf-8");
	    os.write(input, 0, input.length);           
		os.close();
		
		String response = String.format("Reponse code: %d \nResponse message: %s", conn.getResponseCode(),conn.getResponseMessage());
		System.out.println(response);
	}
	
	public static String[] getAllLinkId(String baseUrl, String thingTypeUID, String channelType) throws IOException {
		URL url = new URL(baseUrl + "/things");
		StringBuilder response = new StringBuilder();
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		BufferedReader buff = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String data;
		while((data = buff.readLine()) != null) {
			response.append(data);
		}
		buff.close();
		String jsonReponse = response.toString();
		String[] lst = new String[25];
		int counter = 0;
		String typeUID,link;
		JSONObject obj;
		JSONArray channels;
		JSONArray test = new JSONArray(jsonReponse);
		for (int i = 0; i < test.length(); i++) {
			obj = test.getJSONObject(i);
			typeUID = obj.getString("thingTypeUID");
			if (typeUID.equalsIgnoreCase(thingTypeUID)) {
				channels = obj.getJSONArray("channels");
				for (int j = 0 ; j< channels.length(); j++) {
					if (channels.getJSONObject(j).getString("channelTypeUID").equalsIgnoreCase(channelType)) {
						link = channels.getJSONObject(j).getJSONArray("linkedItems").getString(0);
						System.out.println(link);
						lst[counter++] = baseUrl + "/items/" + link;
						break;
					}
				}
			}
		}
		return lst;
	}
}
