package de.htwd.wi.zahnarzt.logic;

import de.htwd.wi.zahnarzt.exception.DateiException;
import de.htwd.wi.zahnarzt.exception.PatientNichtGefundenException;
import de.htwd.wi.zahnarzt.model.Patient;
import de.htwd.wi.zahnarzt.persistence.PatientRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Logikschicht für die Verwaltung von Patienten.
 * <p>
 * Diese Klasse enthaelt die Geschaeftslogik fuer alle Operationen rund
 * um Patienten: Anlegen, Abrufen, Bearbeiten, Loeschen und Sortieren.
 * Sie kommuniziert mit dem {@link PatientRepository} fuer die Persistenz.
 * </p>
 *
 * @author Ismael Alkayed
 * @version 1.0
 */
public class PatientenService {

    /** Zaehler fuer automatische ID-Vergabe */
    private int naechsteId = 1;

    /** In-Memory-Liste aller Patienten */
    private List<Patient> patienten;

    /** Persistenzschicht fuer Patienten */
    private final PatientRepository repository;

    /**
     * Erstellt einen neuen PatientenService und laedt bestehende Patienten.
     *
     * @param repository das zugehoerige PatientRepository
     * @throws DateiException wenn die Datei nicht gelesen werden kann
     */
    public PatientenService(PatientRepository repository) throws DateiException {
        this.repository = repository;
        this.patienten = new ArrayList<>();
        laden();
    }

    /**
     * Laedt alle Patienten aus der Persistenzschicht und setzt den ID-Zaehler.
     *
     * @throws DateiException bei Lesefehler
     */
    public void laden() throws DateiException {
        this.patienten = repository.ladeAlle();
        // Naechste ID = bisheriges Maximum + 1
        naechsteId = patienten.stream()
            .mapToInt(Patient::getId)
            .max()
            .orElse(0) + 1;
    }

    /**
     * Speichert alle Patienten in die Persistenzschicht.
     *
     * @throws DateiException bei Schreibfehler
     */
    public void speichern() throws DateiException {
        repository.speichereAlle(patienten);
    }

    /**
     * Legt einen neuen Patienten an und speichert ihn.
     *
     * @param vorname      Vorname
     * @param nachname     Nachname
     * @param alter        Alter
     * @param krankenkasse Krankenkasse
     * @return der neu angelegte Patient mit vergebener ID
     * @throws DateiException bei Schreibfehler
     */
    public Patient anlegenUndSpeichern(String vorname, String nachname,
                                        int alter, String krankenkasse) throws DateiException {
        Patient p = new Patient(naechsteId++, vorname, nachname, alter, krankenkasse);
        patienten.add(p);
        speichern();
        return p;
    }

    /**
     * Gibt alle Patienten zurück.
     *
     * @return unveraenderte Liste aller Patienten
     */
    public List<Patient> alleAnzeigen() {
        return new ArrayList<>(patienten);
    }

    /**
     * Sucht einen Patienten anhand seiner ID.
     *
     * @param id die gesuchte ID
     * @return der gefundene Patient
     * @throws PatientNichtGefundenException wenn kein Patient mit dieser ID existiert
     */
    public Patient findeNachId(int id) throws PatientNichtGefundenException {
        Optional<Patient> result = patienten.stream()
            .filter(p -> p.getId() == id)
            .findFirst();
        return result.orElseThrow(() -> new PatientNichtGefundenException(id));
    }

    /**
     * Sucht Patienten, deren Name den Suchbegriff enthaelt (Gross-/Kleinschreibung ignoriert).
     *
     * @param suchbegriff der Suchbegriff
     * @return Liste passender Patienten
     */
    public List<Patient> suchteNachName(String suchbegriff) {
        String lower = suchbegriff.toLowerCase();
        List<Patient> ergebnis = new ArrayList<>();
        for (Patient p : patienten) {
            if (p.getVollstaendigerName().toLowerCase().contains(lower)) {
                ergebnis.add(p);
            }
        }
        return ergebnis;
    }

    /**
     * Aktualisiert die Daten eines bestehenden Patienten und speichert.
     *
     * @param id           ID des zu aktualisierenden Patienten
     * @param vorname      neuer Vorname
     * @param nachname     neuer Nachname
     * @param alter        neues Alter
     * @param krankenkasse neue Krankenkasse
     * @throws PatientNichtGefundenException wenn kein Patient mit dieser ID existiert
     * @throws DateiException                bei Schreibfehler
     */
    public void bearbeiten(int id, String vorname, String nachname,
                           int alter, String krankenkasse)
            throws PatientNichtGefundenException, DateiException {
        Patient p = findeNachId(id);
        p.setVorname(vorname);
        p.setNachname(nachname);
        p.setAlter(alter);
        p.setKrankenkasse(krankenkasse);
        speichern();
    }

    /**
     * Löscht einen Patienten anhand seiner ID und speichert.
     *
     * @param id die ID des zu loeschenden Patienten
     * @throws PatientNichtGefundenException wenn kein Patient mit dieser ID existiert
     * @throws DateiException                bei Schreibfehler
     */
    public void loeschen(int id) throws PatientNichtGefundenException, DateiException {
        Patient p = findeNachId(id);
        patienten.remove(p);
        speichern();
    }

    /**
     * Gibt die Patienten nach Nachnamen sortiert zurueck.
     *
     * @return alphabetisch sortierte Patientenliste
     */
    public List<Patient> sortierteNachNachname() {
        List<Patient> sortiert = new ArrayList<>(patienten);
        sortiert.sort(Comparator.comparing(Patient::getNachname));
        return sortiert;
    }

    /**
     * Gibt die Patienten nach ID sortiert zurueck.
     *
     * @return nach ID sortierte Patientenliste
     */
    public List<Patient> sortiertNachId() {
        List<Patient> sortiert = new ArrayList<>(patienten);
        // Liste nach der ID der Patienten aufsteigend sortieren
        sortiert.sort(Comparator.comparingInt(Patient::getId)); 
        return sortiert;
    }

    /**
     * Gibt die Gesamtanzahl der Patienten zurück.
     *
     * @return Anzahl der Patienten
     */
    public int anzahl() { 
        return patienten.size();
    }
}
