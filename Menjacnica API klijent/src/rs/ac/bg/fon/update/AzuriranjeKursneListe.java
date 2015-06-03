package rs.ac.bg.fon.update;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import rs.ac.bg.fon.Valuta;
import rs.ac.bg.fon.util.JsonRatesAPIKomunikacija;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

public class AzuriranjeKursneListe {

	private final String putanjaDoFajlaKursnaLista = "data/kursevi.json";
	private Valuta valuta;

	public LinkedList<Valuta> ucitajValute() {
		LinkedList<Valuta> kursevi = new LinkedList<Valuta>();
		try {
			FileReader read = new FileReader(putanjaDoFajlaKursnaLista);
			Gson obj = new GsonBuilder().setPrettyPrinting().create();
		
			JsonObject json = obj.fromJson(read, JsonObject.class);
			JsonArray nizKurseva = json.get("valute").getAsJsonArray(); 
			
			for (int i = 0; i < nizKurseva.size(); i++) {
				valuta = new Valuta();
				valuta.setKurs(nizKurseva.get(i).getAsJsonObject().get("kurs").getAsDouble());
				valuta.setNaziv(nizKurseva.get(i).getAsJsonObject().get("naziv").getAsString());
			}
			
			kursevi.add(valuta);
			read.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return kursevi;
	}

	
	public void upisiValute(LinkedList<Valuta> valute, GregorianCalendar datum) {

		try {
			PrintWriter upis = new PrintWriter(new BufferedWriter(new FileWriter(putanjaDoFajlaKursnaLista)));
			JsonObject json = new JsonObject();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();

			datum = new GregorianCalendar();
			json.addProperty("datum", datum.toString());

			JsonArray kursArray = new JsonArray();
			for (int i = 0; i < valute.size(); i++) {

				JsonObject kursJson = new JsonObject();
				kursJson.addProperty("naziv", valute.get(i).getNaziv());
				kursJson.addProperty("kurs", valute.get(i).getKurs());

				kursArray.add(kursJson);
			}
			json.add("kursevi", kursArray);
			
			upis.println(gson.toJson(kursArray));
			upis.close();

		} catch (Exception e) {
			e.printStackTrace();
		} 

	}

	public void azurirajValute() {

		LinkedList<Valuta> novi = ucitajValute();
		String[] nazivi = new String[novi.size()];

		for (int i = 0; i < nazivi.length; i++) {
			nazivi[i] = novi.get(i).getNaziv();
		}

		LinkedList<Valuta> updateValute = JsonRatesAPIKomunikacija.vratiIznosKurseva(nazivi);

		for (int i = 0; i < updateValute.size(); i++) {
			if(!novi.contains(updateValute)) {
				novi.add(updateValute.get(i));
			}
		}

		upisiValute(novi, new GregorianCalendar());
	}

}
