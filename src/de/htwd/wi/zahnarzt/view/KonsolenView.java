package de.htwd.wi.zahnarzt.view;

import de.htwd.wi.zahnarzt.exception.DateiException;
import de.htwd.wi.zahnarzt.exception.PatientNichtGefundenException;
import de.htwd.wi.zahnarzt.exception.TerminKonfliktException;
import de.htwd.wi.zahnarzt.logic.PatientenService;
import de.htwd.wi.zahnarzt.logic.TerminService;
import de.htwd.wi.zahnarzt.model.Patient;
import de.htwd.wi.zahnarzt.model.Termin;
import de.htwd.wi.zahnarzt.util.InputHelper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

/**
 * View-Schicht der Zahnarztpraxis-Anwendung.
 * <p>
 * Diese Klasse implementiert die gesamte Konsolenbenutzeroberflaeche.
 * Sie delegiert alle Logik-Operationen an {@link PatientenService} und
 * {@link TerminService} und ist ausschliesslich fuer die Ein-/Ausgabe zustaendig.
 * Gehoert zur View-Schicht der 3-Schichten-Architektur.
 * </p>
 *
 * @author Ismael Alkayed
 * @version 1.0
 */
public class KonsolenView {

    // --- Konstanten fuer Menue-Optionen ---

    /** Hauptmenue: Patientenverwaltung */
    private static final int MENU_PATIENTEN = 1;
    /** Hauptmenue: Terminverwaltung */
    private static final int MENU_TERMINE = 2;
    /** Hauptmenue: Statistiken */
    private static final int MENU_STATISTIK = 3;
    /** Hauptmenue: Beenden */
    private static final int MENU_BEENDEN = 0;

    /** Scanner für Konsoleneingaben */
    private final Scanner scanner;

    /** Logikschicht Patienten */
    private final PatientenService patientenService;

    /** Logikschicht Termine */
    private final TerminService terminService;

    /**
     * Erstellt die KonsolenView mit den nötigen Diensten.
     *
     * @param patientenService Service für Patientenverwaltung
     * @param terminService    Service für Terminverwaltung
     */
    public KonsolenView(PatientenService patientenService, TerminService terminService) {
        this.patientenService = patientenService;
        this.terminService = terminService;
        this.scanner = new Scanner(System.in);
    } 

    /**
     * Startet die Hauptschleife der Anwendung.
     * Zeigt das Hauptmenue und verarbeitet Benutzereingaben.
     */
    public void starten() {
        zeigeWillkommen();

        int wahl;
        do {
            zeigeHauptmenue();
            wahl = InputHelper.leseInt(scanner, "Ihre Wahl: ", MENU_BEENDEN, MENU_STATISTIK);

            switch (wahl) {
                case MENU_PATIENTEN  -> patientenMenue();
                case MENU_TERMINE    -> terminMenue();
                case MENU_STATISTIK  -> statistikMenue();
                case MENU_BEENDEN    -> System.out.println("\nAuf Wiedersehen!");
            }
        } while (wahl != MENU_BEENDEN);

        scanner.close();
    }

    // =========================================================
    //  HAUPTMENUE
    // =========================================================

    /** Gibt die Willkommensnachricht aus. */
    private void zeigeWillkommen() {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║   Zahnarztpraxis-Verwaltungssystem v1.0          ║");
        System.out.println("║   HTW Dresden – Programmierung II                ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println();
    }

    /** Zeigt das Hauptmenue an. */
    private void zeigeHauptmenue() {
        System.out.println("\n══════════ HAUPTMENUE ══════════");
        System.out.printf("[%d] Patientenverwaltung%n", MENU_PATIENTEN);
        System.out.printf("[%d] Terminverwaltung%n",    MENU_TERMINE);
        System.out.printf("[%d] Statistiken%n",         MENU_STATISTIK);
        System.out.printf("[%d] Beenden%n",             MENU_BEENDEN);
        System.out.println("────────────────────────────────");
    }

    // =========================================================
    //  PATIENTENMENUE
    // =========================================================

    /** Zeigt und verarbeitet das Patientenmenue. */
    private void patientenMenue() {
        int wahl;
        do {
            System.out.println("\n─── PATIENTENVERWALTUNG ───");
            System.out.println("[1] Alle Patienten anzeigen");
            System.out.println("[2] Patient anlegen");
            System.out.println("[3] Patient suchen");
            System.out.println("[4] Patient bearbeiten");
            System.out.println("[5] Patient loeschen");
            System.out.println("[0] Zurueck");
            wahl = InputHelper.leseInt(scanner, "Wahl: ", 0, 5);

            switch (wahl) {
                case 1 -> allePatientenAnzeigen();
                case 2 -> patientAnlegen();
                case 3 -> patientSuchen();
                case 4 -> patientBearbeiten();
                case 5 -> patientLoeschen();
            }
        } while (wahl != 0);
    }

    /** Zeigt alle Patienten an, mit Sortierauswahl. */
    private void allePatientenAnzeigen() {
        System.out.println("\nSortierung: [1] nach ID  [2] nach Nachname");
        int sort = InputHelper.leseInt(scanner, "Wahl: ", 1, 2);

        List<Patient> liste = (sort == 1)
            ? patientenService.sortiertNachId()
            : patientenService.sortierteNachNachname();

        if (liste.isEmpty()) {
            System.out.println("Noch keine Patienten vorhanden.");
            return;
        }
        System.out.println("\n── Patienten (" + liste.size() + ") ──");
        liste.forEach(System.out::println);
    }

    /** Fuehrt den Dialog zum Anlegen eines Patienten durch. */
    private void patientAnlegen() {
        System.out.println("\n── Neuen Patienten anlegen ──");
        String vorname = InputHelper.leseString(scanner, "Vorname: ");
        String nachname = InputHelper.leseString(scanner, "Nachname: ");
        int alter = InputHelper.leseInt(scanner, "Alter: ", 0, 150);
        String kasse = InputHelper.leseString(scanner, "Krankenkasse: ");

        try {
            Patient p = patientenService.anlegenUndSpeichern(vorname, nachname, alter, kasse);
            System.out.println("Patient angelegt: " + p);
        } catch (DateiException e) {
            System.out.println("FEHLER beim Speichern: " + e.getMessage());
        }
    }

    /** Sucht Patienten nach Name. */
    private void patientSuchen() {
        String begriff = InputHelper.leseString(scanner, "Suchbegriff (Name): ");
        List<Patient> ergebnis = patientenService.suchteNachName(begriff);
        if (ergebnis.isEmpty()) {
            System.out.println("Keine Patienten gefunden.");
        } else {
            ergebnis.forEach(System.out::println);
        }
    }

    /** Dialog zum Bearbeiten eines Patienten. */
    private void patientBearbeiten() {
        int id = InputHelper.leseInt(scanner, "Patienten-ID: ", 1, Integer.MAX_VALUE);
        try {
            Patient p = patientenService.findeNachId(id);
            System.out.println("Aktuell: " + p);
            System.out.println("(Felder leer lassen = unveraendert – nicht implementiert, alle Felder pflichtmaessig)");

            String vorname = InputHelper.leseString(scanner, "Neuer Vorname [" + p.getVorname() + "]: ");
            String nachname = InputHelper.leseString(scanner, "Neuer Nachname [" + p.getNachname() + "]: ");
            int alter = InputHelper.leseInt(scanner, "Neues Alter [" + p.getAlter() + "]: ", 0, 150);
            String kasse = InputHelper.leseString(scanner, "Neue Kasse [" + p.getKrankenkasse() + "]: ");

            patientenService.bearbeiten(id, vorname, nachname, alter, kasse);
            System.out.println("Patient aktualisiert.");
        } catch (PatientNichtGefundenException e) {
            System.out.println("FEHLER: " + e.getMessage());
        } catch (DateiException e) {
            System.out.println("FEHLER beim Speichern: " + e.getMessage());
        }
    }

    /** Dialog zum Löschen eines Patienten. */
    private void patientLoeschen() {
        int id = InputHelper.leseInt(scanner, "Patienten-ID: ", 1, Integer.MAX_VALUE);
        try {
            Patient p = patientenService.findeNachId(id);
            System.out.println("Zu loeschen: " + p);
            if (InputHelper.leseJaNein(scanner, "Wirklich loeschen?")) {
                patientenService.loeschen(id);
                System.out.println("Patient geloescht.");
            } else {
                System.out.println("Abgebrochen.");
            }
        } catch (PatientNichtGefundenException e) {
            System.out.println("FEHLER: " + e.getMessage());
        } catch (DateiException e) {
            System.out.println("FEHLER beim Speichern: " + e.getMessage());
        }
    }

    // =========================================================
    //  TERMINMENUE
    // =========================================================

    /** Zeigt und verarbeitet das Terminmenue. */
    private void terminMenue() {
        int wahl;
        do {
            System.out.println("\n─── TERMINVERWALTUNG ───");
            System.out.println("[1] Alle Termine anzeigen");
            System.out.println("[2] Termin erstellen");
            System.out.println("[3] Termine eines Patienten anzeigen");
            System.out.println("[4] Termin bearbeiten");
            System.out.println("[5] Termin loeschen");
            System.out.println("[0] Zurueck");
            wahl = InputHelper.leseInt(scanner, "Wahl: ", 0, 5);

            switch (wahl) {
                case 1 -> alleTermineAnzeigen();
                case 2 -> terminErstellen();
                case 3 -> termineVonPatient();
                case 4 -> terminBearbeiten();
                case 5 -> terminLoeschen();
            }
        } while (wahl != 0);
    }

    /** Zeigt alle Termine mit Sortierauswahl. */
    private void alleTermineAnzeigen() {
        System.out.println("\nSortierung: [1] nach Datum  [2] nach Patient");
        int sort = InputHelper.leseInt(scanner, "Wahl: ", 1, 2);

        List<Termin> liste = (sort == 1)
            ? terminService.sortierteNachDatum()
            : terminService.sortiertNachPatient();

        if (liste.isEmpty()) {
            System.out.println("Noch keine Termine vorhanden.");
            return;
        }

        System.out.println("\n── Termine (" + liste.size() + ") ──");
        for (Termin t : liste) {
            System.out.println(t);
            // Patientenname nachschlagen
            try {
                Patient p = patientenService.findeNachId(t.getPatientId());
                System.out.println("   -> Patient: " + p.getVollstaendigerName());
            } catch (PatientNichtGefundenException e) {
                System.out.println("   -> Patient nicht gefunden");
            }
        }
    }

    /** Dialog zum Erstellen eines neuen Termins. */
    private void terminErstellen() {
        System.out.println("\n── Neuen Termin erstellen ──");
        int patId = InputHelper.leseInt(scanner, "Patienten-ID: ", 1, Integer.MAX_VALUE);

        // Patient-Existenz pruefen
        try {
            Patient p = patientenService.findeNachId(patId);
            System.out.println("Patient: " + p.getVollstaendigerName());
        } catch (PatientNichtGefundenException e) {
            System.out.println("FEHLER: " + e.getMessage());
            return;
        }

        LocalDate datum = InputHelper.leseDatum(scanner, "Datum (TT.MM.JJJJ): ");
        LocalTime uhrzeit = InputHelper.leseUhrzeit(scanner, "Uhrzeit (HH:mm): ");
        int dauer = InputHelper.leseInt(scanner, "Dauer in Minuten: ", 5, 240);
        String art = InputHelper.leseString(scanner, "Behandlungsart: ");
        System.out.print("Notizen (optional, Enter ueberspringen): ");
        String notizen = scanner.nextLine().trim();
        if (notizen.isEmpty()) notizen = null;

        try {
            Termin t = terminService.erstellenUndSpeichern(datum, uhrzeit, dauer, patId, art, notizen);
            System.out.println("Termin erstellt: " + t);
        } catch (TerminKonfliktException e) {
            System.out.println("KONFLIKT: " + e.getMessage());
        } catch (DateiException e) {
            System.out.println("FEHLER beim Speichern: " + e.getMessage());
        }
    }

    /** Zeigt alle Termine eines bestimmten Patienten. */
    private void termineVonPatient() {
        int id = InputHelper.leseInt(scanner, "Patienten-ID: ", 1, Integer.MAX_VALUE);
        List<Termin> liste = terminService.fuerPatient(id);
        if (liste.isEmpty()) {
            System.out.println("Keine Termine fuer diesen Patienten.");
        } else {
            System.out.println("Termine von Patient " + id + ":");
            liste.forEach(System.out::println);
        }
    }

    /** Dialog zum Bearbeiten eines Termins. */
    private void terminBearbeiten() {
        int id = InputHelper.leseInt(scanner, "Termin-ID: ", 1, Integer.MAX_VALUE);
        terminService.findeNachId(id).ifPresentOrElse(
            t -> {
                System.out.println("Aktuell: " + t);
                try {
                    LocalDate datum = InputHelper.leseDatum(scanner, "Neues Datum (TT.MM.JJJJ): ");
                    LocalTime uhrzeit = InputHelper.leseUhrzeit(scanner, "Neue Uhrzeit (HH:mm): ");
                    int dauer = InputHelper.leseInt(scanner, "Neue Dauer (Min): ", 5, 240);
                    int patId = InputHelper.leseInt(scanner, "Neue Patienten-ID: ", 1, Integer.MAX_VALUE);
                    String art = InputHelper.leseString(scanner, "Neue Behandlungsart: ");
                    System.out.print("Neue Notizen: ");
                    String notizen = scanner.nextLine().trim();

                    terminService.bearbeiten(id, datum, uhrzeit, dauer, patId, art,
                                             notizen.isEmpty() ? null : notizen);
                    System.out.println("Termin aktualisiert.");
                } catch (TerminKonfliktException e) {
                    System.out.println("KONFLIKT: " + e.getMessage());
                } catch (DateiException e) {
                    System.out.println("FEHLER beim Speichern: " + e.getMessage());
                }
            },
            () -> System.out.println("Termin ID " + id + " nicht gefunden.")
        );
    }

    /** Dialog zum Löschen eines Termins. */
    private void terminLoeschen() {
        int id = InputHelper.leseInt(scanner, "Termin-ID: ", 1, Integer.MAX_VALUE);
        terminService.findeNachId(id).ifPresentOrElse(
            t -> {
                System.out.println("Zu loeschen: " + t);
                if (InputHelper.leseJaNein(scanner, "Wirklich loeschen?")) {
                    try {
                        terminService.loeschen(id);
                        System.out.println("Termin geloescht.");
                    } catch (DateiException e) {
                        System.out.println("FEHLER: " + e.getMessage());
                    }
                } else {
                    System.out.println("Abgebrochen.");
                }
            },
            () -> System.out.println("Termin ID " + id + " nicht gefunden.")
        );
    }

    // =========================================================
    //  STATISTIKMENUE
    // =========================================================

    /** Zeigt und verarbeitet das Statistikmenue. */
    private void statistikMenue() {
        int wahl;
        do {
            System.out.println("\n─── STATISTIKEN ───");
            System.out.println("[1] Monatsübersicht");
            System.out.println("[2] Auslastung pro Tag");
            System.out.println("[3] Gesamtübersicht");
            System.out.println("[0] Zurueck");
            wahl = InputHelper.leseInt(scanner, "Wahl: ", 0, 3);

            switch (wahl) {
                case 1 -> monatsUebersicht();
                case 2 -> auslastung();
                case 3 -> gesamtUebersicht();
            }
        } while (wahl != 0);
    }

    /** Zeigt alle Termine eines Monats an. */
    private void monatsUebersicht() {
        int jahr = InputHelper.leseInt(scanner, "Jahr (z.B. 2026): ", 2000, 2100);
        int monatNr = InputHelper.leseInt(scanner, "Monat (1-12): ", 1, 12);
        Month monat = Month.of(monatNr);

        List<Termin> liste = terminService.fuerMonat(jahr, monat);
        String monatName = monat.getDisplayName(TextStyle.FULL, Locale.GERMAN);
        System.out.printf("%nTermine im %s %d: %d Termine%n", monatName, jahr, liste.size());

        if (liste.isEmpty()) {
            System.out.println("Keine Termine in diesem Monat.");
        } else {
            liste.forEach(t -> {
                System.out.println(t);
                try {
                    Patient p = patientenService.findeNachId(t.getPatientId());
                    System.out.println("   -> " + p.getVollstaendigerName());
                } catch (PatientNichtGefundenException e) {
                    System.out.println("   -> (Patient nicht gefunden)");
                }
            });
        }
    }

    /** Zeigt die Auslastung pro Tag fuer einen Monat. */
    private void auslastung() {
        int jahr = InputHelper.leseInt(scanner, "Jahr: ", 2000, 2100);
        int monatNr = InputHelper.leseInt(scanner, "Monat (1-12): ", 1, 12);
        Month monat = Month.of(monatNr);

        Map<LocalDate, Long> auslastung = terminService.auslastungProTag(jahr, monat);
        String monatName = monat.getDisplayName(TextStyle.FULL, Locale.GERMAN);
        System.out.printf("%nAuslastung %s %d:%n", monatName, jahr);

        if (auslastung.isEmpty()) {
            System.out.println("Keine Termine in diesem Monat.");
        } else {
            auslastung.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> {
                    String balken = "█".repeat(e.getValue().intValue());
                    System.out.printf("  %s : %s (%d)%n",
                        e.getKey().format(Termin.DATUM_FORMAT), balken, e.getValue());
                });
        }
    }

    /** Zeigt eine allgemeine Gesamtstatistik. */
    private void gesamtUebersicht() {
        System.out.println("\n── Gesamtübersicht ──");
        System.out.println("Patienten gesamt : " + patientenService.anzahl());
        System.out.println("Termine gesamt   : " + terminService.anzahl());

        // Nächste 5 Termine ab heute
        LocalDate heute = LocalDate.now();
        List<Termin> naechste = terminService.sortierteNachDatum().stream()
            .filter(t -> !t.getDatum().isBefore(heute))
            .limit(5)
            .toList();

        System.out.println("\nNaechste " + naechste.size() + " Termine ab heute:");
        if (naechste.isEmpty()) {
            System.out.println("Keine zukuenftigen Termine.");
        } else {
            naechste.forEach(t -> {
                System.out.println("  " + t);
                try {
                    Patient p = patientenService.findeNachId(t.getPatientId());
                    System.out.println("    -> " + p.getVollstaendigerName());
                } catch (PatientNichtGefundenException e) {
                    System.out.println("    -> (unbekannter Patient)");
                }
            });
        }
    }
}
