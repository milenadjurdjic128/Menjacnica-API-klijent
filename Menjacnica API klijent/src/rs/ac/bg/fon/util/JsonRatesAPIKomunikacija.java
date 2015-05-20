package rs.ac.bg.fon.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import rs.ac.bg.fon.Valuta;

public class JsonRatesAPIKomunikacija {

	String appKey = "jr-ba8999934fc5a7ab64a4872fb4ed9af7";
	String jsonRatesURL = "http://jsonrates.com/get/";

	public Valuta[] vratiIznosKurseva(String[] valute, String from, String to) {
		//http://jsonrates.com/get/?from=EUR&to=RSD&apiKey=jr-ba8999934fc5a7ab64a4872fb4ed9af7

		Valuta[] kursevi = new Valuta[valute.length];
		
		for (int i = 0; i < valute.length; i++) {
			String url = jsonRatesURL + "?" +
					"from=" + from +
					"&to=" + to +
					"&apiKey=" + appKey;

			try {
				String result = sendGet(url);

				Gson gson = new GsonBuilder().create();
				JsonObject jsonResult = gson.fromJson(result, JsonObject.class);

				Valuta obj = new Valuta();
				obj.setNaziv(valute[i]);
				obj.setKurs(Double.parseDouble(jsonResult.get("rate").getAsString()));
				
				kursevi[i] = obj;
				
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		
		return kursevi;
	}

	private String sendGet(String url) throws Exception {
		URL obj = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

		connection.setRequestMethod("GET");

		BufferedReader in = new BufferedReader(
				new InputStreamReader(connection.getInputStream()));

		boolean endReading = false;
		String response = "";

		while (!endReading) {
			String s = in.readLine();

			if (s != null) {
				response += s;
			} else {
				endReading = true;
			}
		}
		in.close();

		return response.toString();
	}

}
