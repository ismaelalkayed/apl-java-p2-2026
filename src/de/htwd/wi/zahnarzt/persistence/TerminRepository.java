package de.htwd.wi.zahnarzt.persistence;

import de.htwd.wi.zahnarzt.exception.DateiException;
import de.htwd.wi.zahnarzt.model.Termin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistenzklasse fuer Termindaten im JSON-Format.
 * <p>
 * Verantwortlich fuer das Laden und Speichern von {@link Termin}-Objekten
 * als JSON-Datei. Gehoert zur Persistenzschicht der 3-Schichten-Architektur.
 * </p>
 *
 * @author Ismael Alkayed
 * @version 1.0
 */
public class TerminRepository {

    /** Pfad zur JSON-Datei fuer Termindaten */
    private final String dateipfad;

    /**
     * Erstellt ein neues TerminRepository fuer den angegebenen Dateipfad.
     *
     * @param dateipfad Pfad zur JSON-Datei
     */ 
    public TerminRepository(String dateipfad) {
        this.dateipfad = dateipfad;
    }

    /**
     * Laedt alle Termine aus der JSON-Datei.
     * Gibt eine leere Liste zurueck, wenn die Datei noch nicht existiert.
     *
     * @return Liste der geladenen Termine
     * @throws DateiException bei Lesefehler
     */
    public List<Termin> ladeAlle() throws DateiException {
        List<Termin> termine = new ArrayList<>();	// Leere Liste für alle geladenen Termine
        File datei = new File(dateipfad);

        if (!datei.exists()) {
            return termine;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(datei), StandardCharsets.UTF_8))) {

            StringBuilder sb = new StringBuilder();
            String zeile;
            while ((zeile = reader.readLine()) != null) {
                sb.append(zeile).append("\n");
            }

            String json = sb.toString().trim();
            if (json.isEmpty() || json.equals("[]")) {
                return termine;
            }

            termine = parseTermineJson(json);

        } catch (IOException e) {
            throw new DateiException("Fehler beim Lesen der Termindatei: " + dateipfad, e);
        }

        return termine;
    }

    /**
     * Speichert alle Termine in die JSON-Datei.
     *
     * @param termine die zu speichernde Terminliste
     * @throws DateiException bei Schreibfehler
     */
    public void speichereAlle(List<Termin> termine) throws DateiException {
        File datei = new File(dateipfad);
        if (datei.getParentFile() != null) {
            datei.getParentFile().mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(datei), StandardCharsets.UTF_8))) {

            writer.println("[");	// JSON-Array öffnen
            for (int i = 0; i < termine.size(); i++) {
                writer.print(terminZuJson(termine.get(i)));
                if (i < termine.size() - 1) {
                    writer.println(",");
                } else {
                    writer.println();
                }
            }
            writer.println("]");	// JSON-Array schließen

        } catch (IOException e) {
            throw new DateiException("Fehler beim Schreiben der Termindatei: " + dateipfad, e);
        }
    }

    /**
     * Serialisiert einen Termin als JSON-String.
     *
     * @param t der Termin
     * @return JSON-Darstellung
     */
    private String terminZuJson(Termin t) {
        StringBuilder sb = new StringBuilder();
        sb.append("  {\n");
        sb.append("    \"id\": ").append(t.getId()).append(",\n");
        sb.append("    \"datum\": \"").append(t.getDatum().toString()).append("\",\n");
        sb.append("    \"uhrzeit\": \"").append(t.getUhrzeit().toString()).append("\",\n");
        sb.append("    \"dauerMinuten\": ").append(t.getDauerMinuten()).append(",\n");
        sb.append("    \"patientId\": ").append(t.getPatientId()).append(",\n");
        sb.append("    \"behandlungsArt\": \"").append(escapeJson(t.getBehandlungsArt())).append("\",\n");
        sb.append("    \"notizen\": \"").append(escapeJson(t.getNotizen() != null ? t.getNotizen() : "")).append("\"\n");
        sb.append("  }");
        return sb.toString();
    }

    /**
     * Parst den gesamten JSON-Inhalt und erstellt eine Terminliste.
     *
     * @param json JSON-Array-String
     * @return Liste von Termin-Objekten
     */
    private List<Termin> parseTermineJson(String json) {
        List<Termin> liste = new ArrayList<>();
        int pos = 0;
        while ((pos = json.indexOf('{', pos)) != -1) {
            int ende = findeObjektEnde(json, pos);
            if (ende == -1) break;
            String block = json.substring(pos, ende + 1);
            Termin t = parseTerminBlock(block);
            if (t != null) {
                liste.add(t);
            }
            pos = ende + 1;
        }
        return liste;
    }

    /**
     * Parst einen einzelnen JSON-Objekt-Block fuer einen Termin.
     *
     * @param block JSON-Block-String
     * @return Termin-Objekt oder null bei Parsefehler
     */
    private Termin parseTerminBlock(String block) {
        try {
            int id = Integer.parseInt(extrahiereWert(block, "id"));
            LocalDate datum = LocalDate.parse(extrahiereWert(block, "datum"));
            LocalTime uhrzeit = LocalTime.parse(extrahiereWert(block, "uhrzeit"));
            int dauer = Integer.parseInt(extrahiereWert(block, "dauerMinuten"));
            int patientId = Integer.parseInt(extrahiereWert(block, "patientId"));
            String art = extrahiereWert(block, "behandlungsArt");
            String notizen = extrahiereWert(block, "notizen");

            return new Termin(id, datum, uhrzeit, dauer, patientId, art, notizen);
        } catch (Exception e) {
            return null;
        }
    }

    // --- Hilfsmethoden (identisch zu PatientRepository) ---

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

    private String extrahiereWert(String json, String feldName) {
        String suche = "\"" + feldName + "\"";
        int pos = json.indexOf(suche);
        if (pos == -1) return "";
        int doppelpunkt = json.indexOf(':', pos);
        if (doppelpunkt == -1) return "";
        int wertStart = doppelpunkt + 1;
        while (wertStart < json.length() && Character.isWhitespace(json.charAt(wertStart))) {
            wertStart++;
        }
        if (wertStart >= json.length()) return "";
        char ersteZeichen = json.charAt(wertStart);
        if (ersteZeichen == '"') {
            int ende = wertStart + 1;
            while (ende < json.length()) {
                if (json.charAt(ende) == '"' && json.charAt(ende - 1) != '\\') break;
                ende++;
            }
            return json.substring(wertStart + 1, ende);
        } else {
            int ende = wertStart;
            while (ende < json.length() && !",\n}".contains(String.valueOf(json.charAt(ende)))) {
                ende++;
            }
            return json.substring(wertStart, ende).trim();
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r");
    }
}
