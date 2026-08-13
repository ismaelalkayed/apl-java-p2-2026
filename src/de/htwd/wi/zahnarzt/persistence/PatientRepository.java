package de.htwd.wi.zahnarzt.persistence;

import de.htwd.wi.zahnarzt.exception.DateiException;

import de.htwd.wi.zahnarzt.model.Patient;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistenzklasse fuer Patientendaten im JSON-Format.
 * <p>
 * Diese Klasse uebernimmt das Laden und Speichern von {@link Patient}-Objekten
 * in eine JSON-Textdatei. JSON wird manuell geparst (ohne externe Bibliothek),
 * um die Abhaengigkeiten minimal zu halten.
 * </p>
 *
 * @author Ismael Alkayed
 * @version 1.0
 */
public class PatientRepository {

    /** Pfad zur JSON-Datei fuer Patientendaten */
    private final String dateipfad;

    /**
     * Erstellt ein neues PatientRepository für den angegebenen Dateipfad.
     *
     * @param dateipfad absoluter oder relativer Pfad zur JSON-Datei
     */
    public PatientRepository(String dateipfad) {
        this.dateipfad = dateipfad;
    } 

    /**
     * Lädt alle Patienten aus der JSON-Datei.
     * <p>
     * Gibt eine leere Liste zurück, wenn die Datei nicht existiert.
     * </p>
     *
     * @return Liste der geladenen Patienten
     * @throws DateiException bei Lesefehler
     */
    public List<Patient> ladeAlle() throws DateiException { // Methode ladeAlle 
        List<Patient> patienten = new ArrayList<>(); // / Leere Liste für alle geladenen Patienten
        File datei = new File(dateipfad);

        if (!datei.exists()) {
            return patienten; // Leere Liste - noch keine Daten vorhanden
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(datei), StandardCharsets.UTF_8))) {

            StringBuilder sb = new StringBuilder();
            String zeile;
            while ((zeile = reader.readLine()) != null) { // null = Dateiendde
                sb.append(zeile).append("\n");	
            }

            String json = sb.toString().trim();
            if (json.isEmpty() || json.equals("[]")) {
                return patienten;
            }

            // Einfacher JSON-Parser für Patient-Array
            // Format: [{"id":1,"vorname":"Max",...},...]
            patienten = parsePatientenJson(json);

        } catch (IOException e) {
            throw new DateiException("Fehler beim Lesen der Patientendatei: " + dateipfad, e);
        }

        return patienten;	// Vollstaendige Liste zurueckgeben
    }

    /**
     * Speichert alle Patienten in die JSON-Datei.
     * <p>
     * Überschreibt den bisherigen Inhalt der Datei vollstaendig.
     * Erstellt die Datei neu, falls sie noch nicht existiert.
     * </p>
     *
     * @param patienten die zu speichernde Patientenliste
     * @throws DateiException bei Schreibfehler
     */
    public void speichereAlle(List<Patient> patienten) throws DateiException { // Methode speichereAlle
        // Verzeichnis anlegen falls nötig
        File datei = new File(dateipfad);
        if (datei.getParentFile() != null) {
            datei.getParentFile().mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(datei), StandardCharsets.UTF_8))) {

            writer.println("[");	// JSON-Array beginnt
            for (int i = 0; i < patienten.size(); i++) {
                writer.print(patientZuJson(patienten.get(i)));
                if (i < patienten.size() - 1) {	// Wenn nicht letzter Eintrag
                    writer.println(",");		// Komma schreiben
                } else {
                    writer.println();			// Sonst nur Zeilenumbruch
                }
            }
            writer.println("]");				// JSON-Array schliessen

        } catch (IOException e) {
            throw new DateiException("Fehler beim Schreiben der Patientendatei: " + dateipfad, e);
        }
    }

    /**
     * Wandelt ein Patient-Objekt in einen JSON-String um.
     *
     * @param p der Patient
     * @return JSON-Darstellung des Patienten
     */
    private String patientZuJson(Patient p) {
        StringBuilder sb = new StringBuilder();
        sb.append("  {\n");
        sb.append("    \"id\": ").append(p.getId()).append(",\n");
        sb.append("    \"vorname\": \"").append(escapeJson(p.getVorname())).append("\",\n");
        sb.append("    \"nachname\": \"").append(escapeJson(p.getNachname())).append("\",\n");
        sb.append("    \"alter\": ").append(p.getAlter()).append(",\n");
        sb.append("    \"krankenkasse\": \"").append(escapeJson(p.getKrankenkasse())).append("\",\n");
        sb.append("    \"historie\": [");

        List<String> hist = p.getBehandlungsHistorie();
        for (int i = 0; i < hist.size(); i++) {
            sb.append("\"").append(escapeJson(hist.get(i))).append("\"");
            if (i < hist.size() - 1) sb.append(", ");
        }
        sb.append("]\n");
        sb.append("  }");
        return sb.toString();
    }

    /**
     * Parst einen JSON-Array-String und erstellt daraus Patient-Objekte.
     *
     * @param json der gesamte JSON-Inhalt als String
     * @return Liste der geparsten Patienten
     */
    private List<Patient> parsePatientenJson(String json) {
        List<Patient> liste = new ArrayList<>();

        // Jedes Objekt-Block {} extrahieren
        int pos = 0;
        while ((pos = json.indexOf('{', pos)) != -1) {
            int ende = findeObjektEnde(json, pos);
            if (ende == -1) break;
            String block = json.substring(pos, ende + 1);
            Patient p = parsePatientBlock(block);
            if (p != null) {
                liste.add(p);
            }
            pos = ende + 1;
        }

        return liste;
    }

    /**
     * Findet den Index der schliessenden geschweiften Klammer eines JSON-Objekts.
     *
     * @param json   der JSON-String
     * @param start  Startposition der oeffnenden Klammer
     * @return Index der schliessenden Klammer oder -1
     */
    private int findeObjektEnde(String json, int start) {
        int tiefe = 0;
        boolean inString = false;
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '"' && (i == 0 || json.charAt(i - 1) != '\\')) {
                inString = !inString;
            }
            if (!inString) {
                if (c == '{') tiefe++;
                else if (c == '}') {
                    tiefe--;
                    if (tiefe == 0) return i;
                }
            }
        }
        return -1;
    }

    /**
     * Parst einen einzelnen JSON-Objekt-Block fuer einen Patienten.
     *
     * @param block der JSON-Block als String
     * @return das Patient-Objekt oder null bei Parsefehler
     */
    private Patient parsePatientBlock(String block) {
        try {
            int id = Integer.parseInt(extrahiereWert(block, "id"));
            String vorname = extrahiereWert(block, "vorname");
            String nachname = extrahiereWert(block, "nachname");
            int alter = Integer.parseInt(extrahiereWert(block, "alter"));
            String krankenkasse = extrahiereWert(block, "krankenkasse");

            Patient p = new Patient(id, vorname, nachname, alter, krankenkasse);

            // Historie parsen
            int histStart = block.indexOf("\"historie\"");
            if (histStart != -1) {
                int arrStart = block.indexOf('[', histStart);
                int arrEnd = block.indexOf(']', arrStart);
                if (arrStart != -1 && arrEnd != -1) {
                    String arrInhalt = block.substring(arrStart + 1, arrEnd).trim();
                    if (!arrInhalt.isEmpty()) {
                        String[] eintraege = arrInhalt.split("\",\\s*\"");
                        for (String eintrag : eintraege) {
                            String sauber = eintrag.replaceAll("^\"|\"$", "");
                            if (!sauber.isEmpty()) {
                                p.addBehandlungsEintrag(unescapeJson(sauber));
                            }
                        }
                    }
                }
            }

            return p;
        } catch (NumberFormatException | NullPointerException e) {
            return null; // Ungultiger Block wird uebersprungen
        }
    }

    /**
     * Extrahiert den Wert eines benannten JSON-Feldes aus einem Objekt-Block.
     *
     * @param json      der JSON-Block
     * @param feldName  der Feldname
     * @return Wert als String (ohne Anführungszeichen)
     */
    private String extrahiereWert(String json, String feldName) {
        String suche = "\"" + feldName + "\"";
        int pos = json.indexOf(suche);
        if (pos == -1) return "";
        int doppelpunkt = json.indexOf(':', pos);
        if (doppelpunkt == -1) return "";
        int wertStart = doppelpunkt + 1;
        // Whitespace überspringen
        while (wertStart < json.length() && Character.isWhitespace(json.charAt(wertStart))) {
            wertStart++;
        }
        if (wertStart >= json.length()) return "";

        char ersteZeichen = json.charAt(wertStart);
        if (ersteZeichen == '"') {
            // String-Wert
            int ende = wertStart + 1;
            while (ende < json.length()) {
                if (json.charAt(ende) == '"' && json.charAt(ende - 1) != '\\') break;
                ende++;
            }
            return unescapeJson(json.substring(wertStart + 1, ende));
        } else {
            // Numerischer Wert
            int ende = wertStart;
            while (ende < json.length() && !",\n}".contains(String.valueOf(json.charAt(ende)))) {
                ende++;
            }
            return json.substring(wertStart, ende).trim();
        }
    }

    /**
     * Maskiert Sonderzeichen fuer JSON-Ausgabe.
     *
     * @param s der zu maskierende String
     * @return maskierter String
     */
    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    /**
     * Entfernt JSON-Maskierungen aus einem String.
     *
     * @param s der maskierte String
     * @return unmaskierter String
     */
    private String unescapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\\"", "\"")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\\\", "\\");
    }
}
