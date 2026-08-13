package de.htwd.wi.zahnarzt;

import de.htwd.wi.zahnarzt.exception.DateiException;
import de.htwd.wi.zahnarzt.logic.PatientenService;
import de.htwd.wi.zahnarzt.logic.TerminService;
import de.htwd.wi.zahnarzt.persistence.PatientRepository;
import de.htwd.wi.zahnarzt.persistence.TerminRepository;
import de.htwd.wi.zahnarzt.view.KonsolenView;

/**
 * Startklasse der Zahnarztpraxis-Verwaltungsanwendung.
 * <p>
 * Diese Klasse initialisiert alle Schichten der 3-Schichten-Architektur:
 * <ol>
 *   <li>Persistenzschicht: {@link PatientRepository}, {@link TerminRepository}</li>
 *   <li>Logikschicht: {@link PatientenService}, {@link TerminService}</li>
 *   <li>View-Schicht: {@link KonsolenView}</li>
 * </ol>
 * Die Datendateien werden im Unterordner {@code data/} relativ zum
 * Ausführungsverzeichnis gespeichert.n 
 * </p>
 *
 * <p>
 * <b>HTW Dresden – Programmierung II – APL-Belegarbeit</b><br>
 * Autor: Ismael Alkayed, s90314, Matrikel 57814<br>
 * Thema: Termin- und Verwaltungssystem für eine Zahnarztpraxis
 * </p>
 *
 * @author Ismael Alkayed
 * @version 1.0
 */
public class Main {
 
    /** Pfad zur JSON-Datei fuer Patienten */
    private static final String PATIENTEN_DATEI = "data/patienten.json";

    /** Pfad zur JSON-Datei fuer Termine */
    private static final String TERMINE_DATEI = "data/termine.json";

    /**
     * Hauptmethode – Einstiegspunkt der Anwendung.
     *
     * @param args Kommandozeilenargumente (werden nicht verwendet)
     */
    public static void main(String[] args) {
        System.out.println("Starte Zahnarztpraxis-Verwaltungssystem...");

        try {
            // --- Persistenzschicht initialisieren ---
            PatientRepository patientRepo = new PatientRepository(PATIENTEN_DATEI);
            TerminRepository  terminRepo  = new TerminRepository(TERMINE_DATEI);

            // --- Logikschicht initialisieren (lädt Daten aus Dateien) ---
            PatientenService patientenService = new PatientenService(patientRepo);
            TerminService    terminService    = new TerminService(terminRepo);

            System.out.printf("Geladen: %d Patienten, %d Termine%n",
                patientenService.anzahl(), terminService.anzahl());

            // --- View-Schicht starten ---
            KonsolenView view = new KonsolenView(patientenService, terminService);
            view.starten();

        } catch (DateiException e) {
        	// kritischer Initialisierungsfehler → Programm beenden
            System.err.println("Kritischer Fehler beim Laden der Daten: " + e.getMessage());
            System.exit(1);
        }
    }
}
