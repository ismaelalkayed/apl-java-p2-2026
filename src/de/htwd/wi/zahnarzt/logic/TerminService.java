package de.htwd.wi.zahnarzt.logic;

import de.htwd.wi.zahnarzt.exception.DateiException;
import de.htwd.wi.zahnarzt.exception.TerminKonfliktException;
import de.htwd.wi.zahnarzt.model.Termin;
import de.htwd.wi.zahnarzt.model.Terminpruefbar;
import de.htwd.wi.zahnarzt.persistence.TerminRepository;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Logikschicht fuer die Verwaltung von Terminen.
 * <p>
 * Enthaelt die gesamte Geschaeftslogik fuer Terminoperationen:
 * Erstellen, Aendern, Loeschen, Konfliktpruefung, Sortierung und Statistiken.
 * Implementiert {@link Terminprüfbar} fuer die Konfliktpruefung.
 * </p>
 *
 * @author Ismael Alkayed
 * @version 1.0
 */
public class TerminService implements Terminpruefbar {

    /** Zähler für automatische Termin-ID-Vergabe */
    private int naechsteId = 1; 

    /** In-Memory-Liste aller Termine */
    private List<Termin> termine;

    /** Persistenzschicht fuer Termine */
    private final TerminRepository repository;

    /**
     * Erstellt einen neuen TerminService und laedt bestehende Termine.
     *
     * @param repository das zugehoerige TerminRepository
     * @throws DateiException wenn die Datei nicht gelesen werden kann
     */
    public TerminService(TerminRepository repository) throws DateiException {
        this.repository = repository;
        this.termine = new ArrayList<>();
        laden();
    }

    /**
     * Laedt alle Termine aus der Persistenzschicht.
     *
     * @throws DateiException bei Lesefehler
     */
    public void laden() throws DateiException {
        this.termine = repository.ladeAlle();
        naechsteId = termine.stream()
            .mapToInt(Termin::getId)
            .max()
            .orElse(0) + 1;
    }

    /**
     * Speichert alle Termine in die Persistenzschicht.
     *
     * @throws DateiException bei Schreibfehler
     */
    public void speichern() throws DateiException {
        repository.speichereAlle(termine);
    }

    /**
     * Erstellt einen neuen Termin nach vorheriger Konfliktpruefung.
     *
     * @param datum          Datum des Termins
     * @param uhrzeit        Startzeit im Format HH:mm
     * @param dauerMinuten   Dauer in Minuten
     * @param patientId      ID des Patienten
     * @param behandlungsArt Art der Behandlung
     * @param notizen        optionale Notizen (darf null sein)
     * @return der neu erstellte Termin
     * @throws TerminKonfliktException wenn ein Zeitkonflikt vorliegt
     * @throws DateiException          bei Schreibfehler
     */
    public Termin erstellenUndSpeichern(LocalDate datum,
                                         java.time.LocalTime uhrzeit,
                                         int dauerMinuten,
                                         int patientId,
                                         String behandlungsArt,
                                         String notizen)
            throws TerminKonfliktException, DateiException {

        Termin neuer = new Termin(naechsteId, datum, uhrzeit, dauerMinuten,
                                   patientId, behandlungsArt, notizen);

        if (hatKonflikt(neuer, termine)) {
            // Konflikt genau benennen
            for (Termin best : termine) {
                if (best.ueberschneidetSich(neuer)) {
                    throw new TerminKonfliktException(
                        "Zeitkonflikt mit Termin ID " + best.getId() +
                        " (" + best.getDatum().format(Termin.DATUM_FORMAT) +
                        " " + best.getUhrzeit().format(Termin.ZEIT_FORMAT) +
                        "-" + best.getEndzeit().format(Termin.ZEIT_FORMAT) + ")"
                    );
                }
            }
        }

        termine.add(neuer);
        naechsteId++;
        speichern();
        return neuer;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Prueft alle bestehenden Termine auf Zeituberschneidung mit dem neuen Termin.
     * Ein zu bearbeitender Termin (gleiche ID) wird bei der Pruefung ausgeschlossen.
     * </p>
     */
    @Override
    public boolean hatKonflikt(Termin neuerTermin, List<Termin> bestehendeTermine) {
        for (Termin best : bestehendeTermine) {
            // Den Termin selbst bei Bearbeitung nicht pruefen
            if (best.getId() == neuerTermin.getId()) continue;
            if (neuerTermin.ueberschneidetSich(best)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gibt alle Termine zurück.
     *
     * @return Kopie der Terminliste
     */
    public List<Termin> alleAnzeigen() {
        return new ArrayList<>(termine);
    }

    /**
     * Sucht einen Termin anhand seiner ID.
     *
     * @param id die gesuchte Termin-ID
     * @return Optional mit dem Termin oder leer
     */
    public Optional<Termin> findeNachId(int id) {
        return termine.stream().filter(t -> t.getId() == id).findFirst();
    }

    /**
     * Loesch einen Termin anhand seiner ID.
     *
     * @param id die ID des zu loeschenden Termins
     * @return true wenn erfolgreich geloescht, false wenn nicht gefunden
     * @throws DateiException bei Schreibfehler
     */
    public boolean loeschen(int id) throws DateiException {
        Optional<Termin> t = findeNachId(id);
        if (t.isPresent()) {
            termine.remove(t.get());
            speichern();
            return true;
        }
        return false;
    }

    /**
     * Bearbeitet einen bestehenden Termin nach Konfliktpruefung.
     *
     * @param id             ID des zu aendernden Termins
     * @param datum          neues Datum
     * @param uhrzeit        neue Startzeit
     * @param dauerMinuten   neue Dauer in Minuten
     * @param patientId      neue Patienten-ID
     * @param behandlungsArt neue Behandlungsart
     * @param notizen        neue Notizen
     * @throws TerminKonfliktException wenn ein Zeitkonflikt vorliegt
     * @throws DateiException          bei Schreibfehler
     * @throws NoSuchElementException  wenn kein Termin mit dieser ID gefunden wird
     */
    public void bearbeiten(int id, LocalDate datum, java.time.LocalTime uhrzeit,
                            int dauerMinuten, int patientId,
                            String behandlungsArt, String notizen)
            throws TerminKonfliktException, DateiException {

        Termin existing = findeNachId(id)
            .orElseThrow(() -> new NoSuchElementException("Termin ID " + id + " nicht gefunden."));

        // Temporaer aendern fuer Konfliktpruefung
        Termin temp = new Termin(id, datum, uhrzeit, dauerMinuten,
                                  patientId, behandlungsArt, notizen);
        if (hatKonflikt(temp, termine)) {
            for (Termin best : termine) {
                if (best.getId() != id && best.ueberschneidetSich(temp)) {
                    throw new TerminKonfliktException(
                        "Zeitkonflikt mit Termin ID " + best.getId()
                    );
                }
            }
        }

        // Daten übernehmen
        existing.setDatum(datum);
        existing.setUhrzeit(uhrzeit);
        existing.setDauerMinuten(dauerMinuten);
        existing.setPatientId(patientId);
        existing.setBehandlungsArt(behandlungsArt);
        existing.setNotizen(notizen);
        speichern();
    }

    /**
     * Gibt alle Termine eines bestimmten Patienten zurueck.
     *
     * @param patientId die Patienten-ID
     * @return Liste der Termine des Patienten, sortiert nach Datum/Zeit
     */
    public List<Termin> fuerPatient(int patientId) {
        return termine.stream()
            .filter(t -> t.getPatientId() == patientId)
            .sorted()
            .collect(Collectors.toList());
    }

    /**
     * Gibt alle Termine eines bestimmten Monats zurueck.
     *
     * @param jahr  das Jahr
     * @param monat der Monat
     * @return Liste der Termine, sortiert nach Datum/Zeit
     */
    public List<Termin> fuerMonat(int jahr, Month monat) {
        return termine.stream()
            .filter(t -> t.getDatum().getYear() == jahr
                      && t.getDatum().getMonth() == monat)
            .sorted()
            .collect(Collectors.toList());
    }

    /**
     * Erstellt eine Auslastungsstatistik: Termine pro Tag fuer einen Monat.
     *
     * @param jahr  das Jahr
     * @param monat der Monat
     * @return Map von Tagesdatum auf Anzahl Termine
     */
    public Map<LocalDate, Long> auslastungProTag(int jahr, Month monat) {
        return fuerMonat(jahr, monat).stream()
            .collect(Collectors.groupingBy(Termin::getDatum, Collectors.counting()));
    }

    /**
     * Gibt alle Termine sortiert nach Datum und Uhrzeit zurueck.
     *
     * @return chronologisch sortierte Terminliste
     */
    public List<Termin> sortierteNachDatum() {
        List<Termin> sortiert = new ArrayList<>(termine);
        Collections.sort(sortiert); // nutzt Comparable<Termin>
        return sortiert;
    }

    /**
     * Gibt alle Termine sortiert nach Patienten-ID zurueck.
     *
     * @return nach Patienten-ID sortierte Terminliste
     */
    public List<Termin> sortiertNachPatient() {
        List<Termin> sortiert = new ArrayList<>(termine);
        sortiert.sort(Comparator.comparingInt(Termin::getPatientId));
        return sortiert;
    }

    /**
     * Gibt die Gesamtanzahl der Termine zurueck.
     *
     * @return Anzahl der Termine
     */
    public int anzahl() {
        return termine.size();
    }
}
