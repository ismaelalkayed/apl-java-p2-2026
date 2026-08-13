package de.htwd.wi.zahnarzt.model;

import java.util.List;

/**
 * Interface für Klassen, die Terminkonfliktprüfungen durchführen können.
 * <p>
 * Dieses Interface definiert den Vertrag für die Prüfung ob ein neuer
 * Termin mit bestehenden Terminen kollidiert (Doppelbuchung).
 * </p>
 *
 * @author Ismael Alkayed
 * @version 1.0
 */
public interface Terminpruefbar {

    /**
     * Prüft ob der übergebene Termin einen Konflikt mit der Liste
     * bestehender Termine erzeugt.
     *
     * @param neuerTermin       der zu prüfende neue Termin
     * @param bestehendeTermine Liste aller vorhandenen Termine
     * @return {@code true} wenn ein Konflikt (Überschneidung) vorliegt
     */
    boolean hatKonflikt(Termin neuerTermin, List<Termin> bestehendeTermine);
}
