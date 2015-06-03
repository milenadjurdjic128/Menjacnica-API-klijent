package rs.ac.bg.fon.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;

import rs.ac.bg.fon.Valuta;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class JsonRatesAPIKomunikacija {

	static String appKey = "jr-ba8999934fc5a7ab64a4872fb4ed9af7";
	static String jsonRatesURL = "http://jsonrates.com/get/";
	static String url;

	public static LinkedList<Valuta> vratiIznosKurseva(String[] valute) {
		//http://jsonrates.com/get/?from=EUR&to=RSD&apiKey=jr-ba8999934fc5a7ab64a4872fb4ed9af7

		LinkedList<Valuta> kursevi = new LinkedList<Valuta>();
		String adresa;
		for (int j = 0; j < valute.length; j++) {
			for (int k = 0; k < valute.length; k++) {

				adresa = odDo(valute[j], valute[k]);

				try {
					String rez = sendGet(adresa);

					Gson gson = new GsonBuilder().create();
					JsonObject jsonResult = gson.fromJson(rez, JsonObject.class);

					Valuta obj = new Valuta();
					obj.setNaziv(valute[j]);
					obj.setKurs(Double.parseDouble(jsonResult.get("rate").getAsString()));

					kursevi.set(j, obj);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return kursevi;
	}

	private static String odDo(String iz, String u) {
		String odDo = jsonRatesURL + "?" +
				"from=" + iz +
				"&to=" + u +
				"&apiKey=" + appKey;
		return odDo;
	}


	private static String sendGet(String url) throws Exception {
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
