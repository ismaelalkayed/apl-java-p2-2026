package de.htwd.wi.zahnarzt.exception;

/**
 * Exception für Fehler bei Dateioperationen (Laden/Speichern).
 * <p>
 * Kapselt I/O-Fehler, die beim Lesen oder Schreiben der JSON-Datendateien
 * auftreten koennen.
 * </p>
 *
 * @author Ismael Alkayed
 * @version 1.0
 */
public class DateiException extends Exception {

    /**
     * Erstellt eine neue DateiException mit Beschreibung und Ursache.
     *
     * @param nachricht Beschreibung des Fehlers
     * @param ursache   die zugrundeliegende Ausnahme
     */
    public DateiException(String nachricht, Throwable ursache) { //Overloading (Konstruktor)
        super(nachricht, ursache);
    }
 
    /**
     * Erstellt eine neue DateiException nur mit Beschreibung.
     *
     * @param nachricht Beschreibung des Fehlers
     */
    public DateiException(String nachricht) { //Overloading (Konstruktor)
        super(nachricht);
    }
}
