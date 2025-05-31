import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;



public class AnomalyScanner {

	public static void main(String[] args) {
		// Initialisierung
		Scanner scan = null;
		scan = new Scanner(System.in);
		double sma_intervall = 20;
		double sma_abweichung = 0.02;
		boolean [] anomalien = null;
		boolean nur_anomalien = false;
		boolean ausgefuehrt = false;

		// Begrüßung
		System.out.println("Wilkommen beim Anomaly Scanner.");
		
		// Programm Info
		System.out.println("<PROGRAMMINFO EINFÜGEN>");
		System.out.println("--------------------------------");
		
		// Datei einlesen
		System.out.println("Welche Aktie soll gescannt werden? Bitte den Dateinamen eingeben: ");
		String name = scan.nextLine();
		ArrayList<Double> kurs = einlesen(name);
		ArrayList<String> alle_werte = einlesenAlle(name);
		
		// Programm
		boolean beendet = false;
		while (!beendet) {
			System.out.print("\nDie aktuellen SMA Werte"
					+ "\nSMA Abweichung " + Math.round(sma_abweichung * 100) + " %"
					+ "\nSMA Längenintervall: " + Math.round(sma_intervall) + " Tage\n");
			System.out.print("\nBitte die Nummer auswählen, wie fortgefahren werden soll."
					+ "\n(1) Auswertung starten"
					+ "\n(2) Intervall ändern"
					+ "\n(3) Abweichung ändern"
					+ "\n(4) Ausgabe auf Anomalien beschränken"
					+ "\n(5) Neue Datei/Aktienname eingeben"
					+ "\n(6) Auswertung speichern"
					+ "\n(7) Programm beenden"
					+ "\nAuswahl: ");
			
			int wahl = scan.nextInt();
			scan.nextLine();
			
			System.out.println();
			
			if (wahl == 1) {
				ausgefuehrt = true;
				anomalien = auswerten(kurs, sma_abweichung, sma_intervall);
			}
			else if (wahl == 2) {
				sma_intervall = intervall(sma_intervall);
			}
			else if (wahl == 3) {
				sma_abweichung = abweichung(sma_abweichung);
			}
			else if (wahl == 4) {
				if (!nur_anomalien) {
					nur_anomalien = true;
				}
				else {
					nur_anomalien = false;
				}
			}
			else if (wahl == 5) {
				kurs = einlesenNeu();
			}
			else if (wahl == 6) {
				if (ausgefuehrt) {
					speichern(kurs, alle_werte, nur_anomalien, anomalien);
				}
				else {
					System.out.println("Bevor sie das Ergebnis speichern müssen sie erstmal eine Auswertung starten.");
				}
			}
			else if (wahl == 7) {
				beendet = beenden();
			}
		}
		
		scan.close();
	}
	
	public static boolean beenden() {
		System.out.print("Programm erfolgreich beendet");
		return true;
	}

	public static boolean[] auswerten(ArrayList<Double> arr, double abweichung, double intervall) {
		ArrayList<Double> sma = new ArrayList<Double>();
		int anzahl = arr.size();
		boolean [] anomalien = new boolean [anzahl];
		Collections.reverse(arr);
		for (int i = 0; i < anzahl; i++) {
			double avg = 0;
			for (double n:sma) {
				avg += n;
			}
			avg = avg/sma.size();	
			
			// TODO ist das mathematisch richtig?
			if (Math.abs((1 - (avg/arr.get(i)))) > abweichung) {
				anomalien[i] = true;
			}
			
			if (sma.size() != intervall) {
				sma.add(arr.get(i));
			}
			else if (sma.size() == intervall){
				sma.add(arr.get(i));
				sma.remove(0);
			}
			else {
				System.err.print("Fail");
			}	
		}
		System.out.print("Daten erfolgreich ausgewertet.\n");
		return anomalien;
	}
	
	public static double intervall(double alt) {
		double intervall = alt;
		Scanner scan = new Scanner(System.in);
		System.out.print("Wie soll der neue SMA Intervall Wert lauten? Tage: ");
		intervall = scan.nextDouble();
		return intervall;
	}
	
	public static double abweichung(double alt) {
		double abweichung = alt;
		Scanner scan = new Scanner(System.in);
		System.out.print("Wie soll der neue Wert der SMA Abweichung lauten? Prozent (%): ");
		abweichung = (scan.nextDouble() / 100);
		return abweichung;
	}
	
	public static ArrayList<Double> einlesen(String name) {
		Scanner scan = null;
		ArrayList<Double> kurs = new ArrayList<Double>();
		try {
			scan = new Scanner(new File(name));
		} catch (FileNotFoundException e) {
			System.err.println("Datei nicht gefunden");
			System.exit(0);
		}
		
		String zeile = scan.nextLine();
		while (scan.hasNext()) {
				zeile = scan.nextLine();
				String[] arr = zeile.split(";");
				arr[4] = arr[4].replace(".", "");
				arr[4] = arr[4].replace(",",".");
				double val = Double.parseDouble(arr[4]);
				kurs.add(val);
		}
		System.out.println("Daten vollständig geladen.");
		return kurs;
	}
	
	public static ArrayList<String> einlesenAlle(String name) {
		Scanner scan = null;
		ArrayList<String> alle_werte = new ArrayList<String>();
		
		try {
			scan = new Scanner(new File(name));
		} catch (FileNotFoundException e) {
			System.err.println("Datei nicht gefunden");
			System.exit(0);
		}
		
		scan.nextLine();
		while (scan.hasNext()) {
				alle_werte.add(scan.nextLine());
		}
		return alle_werte;
	}
	
	public static ArrayList<Double> einlesenNeu() {
		Scanner scan = null;
		ArrayList<Double> kurs = new ArrayList<Double>();
		System.out.println("Welche Aktie soll gescannt werden? Bitte den Dateinamen eingeben: ");
		scan = new Scanner(System.in);
		String name = scan.nextLine();
		try {
			scan = new Scanner(new File(name));
		} catch (FileNotFoundException e) {
			System.err.println("Datei nicht gefunden");
			System.exit(0);
		}
		
		String zeile = scan.nextLine();
		while (scan.hasNext()) {
				zeile = scan.nextLine();
				String[] arr = zeile.split(";");
				arr[4] = arr[4].replace(".", "");
				arr[4] = arr[4].replace(",",".");
				double val = Double.parseDouble(arr[4]);
				kurs.add(val);
		}
		System.out.println("Daten vollständig geladen.");
		return kurs;
	}
	
	public static void speichern(ArrayList<Double> kurs, ArrayList<String> alle_werte, boolean nur_anomalien, boolean [] anomalien) {
		Scanner scan = new Scanner(System.in);
		System.out.print("\nBitte die Nummer auswählen, wie fortgefahren werden soll."
				+ "\n(1) Auswertung in der Konsole speichern"
				+ "\n(2) Auswertung als Datei speichern\n");
		int wahl = scan.nextInt();
		Collections.reverse(alle_werte);
		if (anomalien == null) {
			System.err.println("Kein ergebnis zum speichern");
			System.exit(0);
		}
		
		if (wahl == 1) {
			
			System.out.print("Datum;Erster;Hoch;Tief;Schlusskurs;Stuecke;Volumen;Anomaly");
			if (nur_anomalien) {
				for (int i = 0; i < anomalien.length; i++) {
					if (anomalien[i]) {
						System.out.println(alle_werte.get(i));
					}
				}
			}
			else {
				for (int i = 0; i < anomalien.length; i++) {
					if (anomalien[i]) {
						System.out.println(alle_werte.get(i) + "ANOMALY!");
					}
					else {
						System.out.println(alle_werte.get(i));
					}
				}
			}
		}
		else {
			Scanner scan1 = new Scanner(System.in); 
			System.out.print("Wie soll ihre Ausgabedatei heißen? ");
			String fileName = scan1.nextLine();
			PrintWriter outFile = null;
			
			try {
				outFile = new PrintWriter(fileName);
			} catch (FileNotFoundException e) {
				System.err.println("Ausgabedatei konnte nicht erstellt werden");
				System.exit(0);
			}
			outFile.println("Datum;Erster;Hoch;Tief;Schlusskurs;Stuecke;Volumen;Anomaly");
			
			if (nur_anomalien) {
				for (int i = 0; i < anomalien.length; i++) {
					if (anomalien[i]) {
						outFile.println(alle_werte.get(i) + ";" + "Anomaly");
					}
				}
			}
			else {
				for (int i = 0; i < anomalien.length; i++) {
					if (anomalien[i]) {
						outFile.println(alle_werte.get(i) + ";" + "Anomaly");
					}
					else {
						outFile.println(alle_werte.get(i));
					}
				}
			}
			outFile.close();
			System.out.println("Output file has been created: " + fileName);
		}
	}
	
}
	
	
	
