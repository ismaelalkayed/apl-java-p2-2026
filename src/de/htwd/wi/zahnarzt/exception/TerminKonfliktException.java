package de.htwd.wi.zahnarzt.exception;

/**
 * Exception für Terminkonflikt-Situationen (Doppelbuchungen).
 * <p>
 * Wird geworfen, wenn ein neuer Termin sich zeitlich mit einem
 * bestehenden Termin überschneidet.
 * </p>
 *
 * @author Ismael Alkayed
 * @version 1.0
 */
public class TerminKonfliktException extends Exception {

    /**
     * Erstellt eine neue TerminKonfliktException mit erklärnder Nachricht.
     *
     * @param nachricht Beschreibung des Konflikts
     */
    public TerminKonfliktException(String nachricht) {
        super(nachricht);
    } 
}
