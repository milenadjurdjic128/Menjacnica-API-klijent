package rs.ac.bg.fon.update;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import org.json.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import rs.ac.bg.fon.Valuta;
import rs.ac.bg.fon.util.JsonRatesAPIKomunikacija;

public class AzuriranjeKursneListe {

	private final String putanjaDoFajlaKursnaLista = "data/kursnaLista.json";
	LinkedList<Valuta> kursevi = new LinkedList<Valuta>();


	@SuppressWarnings("unchecked")
	public LinkedList<Valuta> ucitajValute() {
		try {
			ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(putanjaDoFajlaKursnaLista)));
			kursevi = (LinkedList<Valuta>)(in.readObject());

			in.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return kursevi;
	}

	public void upisiValute(LinkedList<Valuta> valute, GregorianCalendar datum) {

		try {
			ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(putanjaDoFajlaKursnaLista)));
			JsonObject dateJson = new JsonObject();
			dateJson.addProperty("datum", datum.toString());


			out.writeObject(dateJson);
			JsonArray kursArray = new JsonArray();

			for (int i = 0; i < valute.size(); i++) {

				JsonObject kursJson = new JsonObject();
				kursJson.addProperty("naziv", valute.get(i).getNaziv());
				kursJson.addProperty("kurs", valute.get(i).getKurs());

				kursArray.add(kursJson);
			}
			out.writeObject(kursArray);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 

	}
	
	public void azurirajValute() {
		
		LinkedList<Valuta> novi = ucitajValute();
		
		String[] nazivi = new String[kursevi.size()];
		
		for (int i = 0; i < nazivi.length; i++) {
			nazivi[i] = kursevi.get(i).getNaziv();
		}
		
		Valuta[] updateValute = JsonRatesAPIKomunikacija.vratiIznosKurseva(nazivi);
		
		for (int i = 0; i < updateValute.length; i++) {
			if(!novi.contains(updateValute)) {
				novi.add(updateValute[i]);
			}
		}
		
		upisiValute(novi, new GregorianCalendar());
	}

}
