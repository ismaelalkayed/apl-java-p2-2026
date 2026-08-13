package de.htwd.wi.zahnarzt.model;

import java.time.LocalDate; 
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Repraesentiert einen Termin in der Zahnarztpraxis.
 * <p>
 * Ein Termin besteht aus einem Datum, einer Uhrzeit, einer Dauer,
 * dem zugewiesenen Patienten und einer Behandlungsart.
 * Implementiert {@link Comparable} fuer die Sortierung nach Datum/Zeit.
 * </p>
 *
 * @author Ismael Alkayed
 * @version 1.0
 */
public class Termin implements Comparable<Termin> {

    /** Datums-Format für Anzeige und Parsen */
    public static final DateTimeFormatter DATUM_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    /** Uhrzeit-Format für Anzeige und Parsen */
    public static final DateTimeFormatter ZEIT_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    /** Eindeutige ID des Termins */
    private int id;

    /** Datum des Termins */
    private LocalDate datum;

    /** Startzeit des Termins */
    private LocalTime uhrzeit;

    /** Dauer des Termins in Minuten */
    private int dauerMinuten;

    /** ID des zugeordneten Patienten */
    private int patientId;

    /** Art der Behandlung */
    private String behandlungsArt;

    /** Optionale Notizen zum Termin */
    private String notizen;

    /**
     * Erstellt einen neuen Termin mit allen noetigen Daten.
     *
     * @param id             eindeutige Termin-ID
     * @param datum          Datum des Termins
     * @param uhrzeit        Startzeit des Termins
     * @param dauerMinuten   Dauer in Minuten
     * @param patientId      ID des Patienten
     * @param behandlungsArt Art der Behandlung
     * @param notizen        optionale Notizen
     */
    public Termin(int id, LocalDate datum, LocalTime uhrzeit,
                  int dauerMinuten, int patientId,
                  String behandlungsArt, String notizen) {
        this.id = id;
        this.datum = datum;
        this.uhrzeit = uhrzeit;
        this.dauerMinuten = dauerMinuten;
        this.patientId = patientId;
        this.behandlungsArt = behandlungsArt;
        this.notizen = notizen;
    }

    // --- Getter und Setter ---

    /** @return die Termin-ID */
    public int getId() { return id; }
    /** @param id neue Termin-ID */
    public void setId(int id) { this.id = id; }

    /** @return das Datum */
    public LocalDate getDatum() { return datum; }
    /** @param datum neues Datum */
    public void setDatum(LocalDate datum) { this.datum = datum; }

    /** @return die Startzeit */
    public LocalTime getUhrzeit() { return uhrzeit; }
    /** @param uhrzeit neue Startzeit */
    public void setUhrzeit(LocalTime uhrzeit) { this.uhrzeit = uhrzeit; }

    /** @return die Dauer in Minuten */
    public int getDauerMinuten() { return dauerMinuten; }
    /** @param dauerMinuten neue Dauer */
    public void setDauerMinuten(int dauerMinuten) { this.dauerMinuten = dauerMinuten; }

    /** @return die Patienten-ID */
    public int getPatientId() { return patientId; }
    /** @param patientId neue Patienten-ID */
    public void setPatientId(int patientId) { this.patientId = patientId; }

    /** @return die Behandlungsart */
    public String getBehandlungsArt() { return behandlungsArt; }
    /** @param behandlungsArt neue Behandlungsart */
    public void setBehandlungsArt(String behandlungsArt) { this.behandlungsArt = behandlungsArt; }

    /** @return die Notizen */
    public String getNotizen() { return notizen; }
    /** @param notizen neue Notizen */
    public void setNotizen(String notizen) { this.notizen = notizen; }

    /**
     * Berechnet die Endzeit des Termins anhand von Startzeit und Dauer.
     *
     * @return Endzeit als {@link LocalTime}
     */
    public LocalTime getEndzeit() {
        return uhrzeit.plusMinutes(dauerMinuten);
    }

    /**
     * Prueft, ob dieser Termin sich zeitlich mit einem anderen ueberschneidet.
     * Gilt nur fuer Termine am gleichen Tag.
     *
     * @param anderer der andere Termin
     * @return {@code true} wenn Zeitkonflikt vorliegt
     */
    public boolean ueberschneidetSich(Termin anderer) {
        // Nur Konflikte am selben Tag prüfen
        if (!this.datum.equals(anderer.datum)) {
            return false;
        }
        // Zeitintervalle überlappen sich, wenn Start A < Ende B UND Start B < Ende A
        return this.uhrzeit.isBefore(anderer.getEndzeit())
            && anderer.uhrzeit.isBefore(this.getEndzeit());
    }

    /**
     * Vergleicht Termine nach Datum und Uhrzeit für die Sortierung.
     *
     * @param anderer der zu vergleichende Termin
     * @return negativer Wert wenn früherer Termin, 0 wenn gleich, positiver Wert wenn spaeter
     */
    @Override
    public int compareTo(Termin anderer) {
        int datumVergleich = this.datum.compareTo(anderer.datum);
        if (datumVergleich != 0) {
            return datumVergleich;
        }
        return this.uhrzeit.compareTo(anderer.uhrzeit);
    }

    /**
     * Gibt eine formatierte Darstellung des Termins zurück.
     *
     * @return String mit allen Termindaten
     */
    @Override
    public String toString() {
        return String.format(
            "Termin [ID: %d | %s %s-%s (%d min) | PatID: %d | Behandlung: %s]",
            id,
            datum.format(DATUM_FORMAT),
            uhrzeit.format(ZEIT_FORMAT),
            getEndzeit().format(ZEIT_FORMAT),
            dauerMinuten,
            patientId,
            behandlungsArt
        ); 
    }
}
