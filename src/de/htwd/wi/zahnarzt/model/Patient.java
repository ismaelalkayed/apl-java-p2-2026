package de.htwd.wi.zahnarzt.model;

import java.util.ArrayList; 
import java.util.List;

/**
 * Repräsentiert einen Patienten in der Zahnarztpraxis.
 * <p>
 * Erbt von {@link Person} und erweitert diese um zahnarztspezifische
 * Attribute wie Krankenversicherung und Behandlungshistorie.
 * </p>
 *
 * @author Ismael Alkayed
 * @version 1.0
 */
public class Patient extends Person {

    /** Krankenkasse des Patienten */
    private String krankenkasse;

    /** Liste der bisherigen Behandlungen (Kurznotizen) */
    private List<String> behandlungsHistorie;

    /**
     * Erstellt einen neuen Patienten mit allen Basisdaten.
     *
     * @param id           eindeutige Patienten-ID
     * @param vorname      Vorname des Patienten
     * @param nachname     Nachname des Patienten
     * @param alter        Alter des Patienten
     * @param krankenkasse Name der Krankenkasse
     */
    	// Konstruktor
    public Patient(int id, String vorname, String nachname, int alter, String krankenkasse) {
        super(id, vorname, nachname, alter);
        this.krankenkasse = krankenkasse;
        this.behandlungsHistorie = new ArrayList<>();
    } 

    /**
     * Gibt die Krankenkasse des Patienten zurück.
     *
     * @return Name der Krankenkasse
     */
    public String getKrankenkasse() {
        return krankenkasse;
    }

    /**
     * Setzt die Krankenkasse des Patienten.
     *
     * @param krankenkasse neuer Name der Krankenkasse
     */
    public void setKrankenkasse(String krankenkasse) {
        this.krankenkasse = krankenkasse;
    }

    /**
     * Gibt die Behandlungshistorie des Patienten zurueck.
     *
     * @return Liste der Behandlungsnotizen
     */
    public List<String> getBehandlungsHistorie() {
        return behandlungsHistorie;
    }

    /**
     * Setzt die Behandlungshistorie des Patienten.
     *
     * @param behandlungsHistorie neue Liste der Behandlungsnotizen
     */
    public void setBehandlungsHistorie(List<String> behandlungsHistorie) {
        this.behandlungsHistorie = behandlungsHistorie;
    }

    /**
     * Fügt einen Eintrag zur Behandlungshistorie hinzu.
     *
     * @param eintrag der neue Behandlungseintrag
     */
    public void addBehandlungsEintrag(String eintrag) {
        this.behandlungsHistorie.add(eintrag);
    }

    /**
     * Gibt eine lesbare Darstellung des Patienten zurück.
     * Beinhaltet alle wesentlichen Attribute für Konsolenausgabe.
     *
     * @return formatierter String mit allen Patientendaten
     */
    @Override
    public String toString() {
        return String.format(
            "Patient [ID: %d | Name: %s %s | Alter: %d | Kasse: %s | Behandlungen: %d]",
            id, vorname, nachname, alter, krankenkasse, behandlungsHistorie.size()
        );
    }
}
