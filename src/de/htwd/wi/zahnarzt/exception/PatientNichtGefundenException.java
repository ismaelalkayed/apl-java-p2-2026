package de.htwd.wi.zahnarzt.exception;

/**
 * Exception fuer den Fall, dass ein Patient nicht gefunden wird.
 * <p>
 * Wird geworfen, wenn eine Suche nach einer Patienten-ID kein Ergebnis liefert.
 * </p>
 *
 * @author Ismael Alkayed
 * @version 1.0
 */
public class PatientNichtGefundenException extends Exception {

    /**
     * Erstellt eine neue PatientNichtGefundenException.
     *
     * @param patientId die ID, die nicht gefunden wurde
     */
    public PatientNichtGefundenException(int patientId) {
        super("Patient mit ID " + patientId + " wurde nicht gefunden.");
    }
} 
