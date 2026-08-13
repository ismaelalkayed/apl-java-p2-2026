package de.htwd.wi.zahnarzt.model;

/**
 * Abstrakte Basisklasse für alle Personen im System.
 * <p>
 * Diese Klasse definiert gemeinsame Attribute und Methoden für
 * alle Personen-Typen (z.B. Patienten). Sie implementiert das
 * Grundprinzip der Vererbung.
 * </p>
 *
 * @author Ismael Alkayed
 * @version 1.0
 */
public abstract class Person {
	//Attribute 
    /** Eindeutige ID der Person */
    protected int id;

    /** Vorname der Person */
    protected String vorname;

    /** Nachname der Person */
    protected String nachname;

    /** Alter der Person in Jahren */
    protected int alter;

    /**
     * Erstellt eine neue Person mit den angegebenen Basisdaten.
     *
     * @param id      eindeutige ID
     * @param vorname Vorname
     * @param nachname Nachname
     * @param alter   Alter in Jahren
     */
    public Person(int id, String vorname, String nachname, int alter) {
        this.id = id;
        this.vorname = vorname;
        this.nachname = nachname;
        this.alter = alter;
    } 

    /**
     * Gibt die eindeutige ID der Person zurück.
     *
     * @return die ID
     */
    public int getId() {
        return id;
    }

    /**
     * Setzt die eindeutige ID der Person.
     *
     * @param id die neue ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gibt den Vornamen der Person zurueck.
     *
     * @return der Vorname
     */
    public String getVorname() {
        return vorname;
    }

    /**
     * Setzt den Vornamen der Person.
     *
     * @param vorname der neue Vorname
     */
    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    /**
     * Gibt den Nachnamen der Person zurueck.
     *
     * @return der Nachname
     */
    public String getNachname() {
        return nachname;
    }

    /**
     * Setzt den Nachnamen der Person.
     *
     * @param nachname der neue Nachname
     */
    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    /**
     * Gibt das Alter der Person zurueck.
     *
     * @return das Alter
     */
    public int getAlter() {
        return alter;
    }

    /**
     * Setzt das Alter der Person.
     *
     * @param alter das neue Alter
     */
    public void setAlter(int alter) {
        this.alter = alter;
    }

    /**
     * Gibt den vollstaendigen Namen der Person zurueck.
     *
     * @return Vorname + Leerzeichen + Nachname
     */
    public String getVollstaendigerName() {
        return vorname + " " + nachname;
    }

    /**
     * Abstrakte Methode – jede Unterklasse muss sich selbst als
     * lesbare Zeichenkette repraesentieren koennen.
     *
     * @return String-Darstellung der Person
     */
    @Override
    public abstract String toString();
}
