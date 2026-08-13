package de.htwd.wi.zahnarzt.util;

import java.time.LocalDate; 
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Hilfsklasse für DAU-sichere Konsoleneingaben.
 * <p>
 * Diese Klasse stellt statische Hilfsmethoden bereit, die Benutzereingaben
 * validieren und bei ungültigem Input erneut zur Eingabe auffordern.
 * "DAU-sicher" bedeutet: der duemmste anzunehmende User kann das Programm
 * nicht durch falsche Eingaben zum Absturz bringen.
 * </p>
 *
 * @author Ismael Alkayed
 * @version 1.0
 */
public final class InputHelper {

    /** Privater Konstruktor – Utility-Klasse, nicht instantiierbar */
    private InputHelper() {}

    /**
     * Liest eine Ganzzahl vom Benutzer, wiederholt die Abfrage bei ungültigem Input.
     *
     * @param scanner  der Scanner für die Konsoleneingabe
     * @param prompt   die Eingabeaufforderung
     * @param min      Minimalwert (inklusive)
     * @param max      Maximalwert (inklusive)
     * @return eine gültige Ganzzahl im Bereich [min, max]
     */
    public static int leseInt(Scanner scanner, String prompt, int min, int max) {
        while (true) { // solange nachfragen bis eine gültige Eingabe kommt
            System.out.print(prompt);
            String eingabe = scanner.nextLine().trim();
            try {
                int wert = Integer.parseInt(eingabe);
                if (wert >= min && wert <= max) {
                    return wert;
                }
                System.out.printf("  Bitte eine Zahl zwischen %d und %d eingeben.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  Ungueltige Eingabe. Bitte eine Zahl eingeben.");
            }
        }
    } 

    /**
     * Liest eine nicht-leere Zeichenkette vom Benutzer.
     *
     * @param scanner der Scanner fuer die Konsoleneingabe
     * @param prompt  die Eingabeaufforderung
     * @return eine nicht-leere, getrimmed Zeichenkette
     */
    public static String leseString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String eingabe = scanner.nextLine().trim();
            if (!eingabe.isEmpty()) {
                return eingabe;
            }
            System.out.println("  Eingabe darf nicht leer sein.");
        }
    }

    /**
     * Liest ein Datum im Format TT.MM.JJJJ vom Benutzer.
     *
     * @param scanner der Scanner fuer die Konsoleneingabe
     * @param prompt  die Eingabeaufforderung
     * @return ein gueltiges {@link LocalDate}
     */
    public static LocalDate leseDatum(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String eingabe = scanner.nextLine().trim();
            try {
                // Erwartet Format: TT.MM.JJJJ
                String[] teile = eingabe.split("\\.");
                if (teile.length != 3) throw new DateTimeParseException("Falsches Format", eingabe, 0);
                int tag  = Integer.parseInt(teile[0]);
                int monat = Integer.parseInt(teile[1]);
                int jahr  = Integer.parseInt(teile[2]);
                return LocalDate.of(jahr, monat, tag);
            } catch (Exception e) {
                System.out.println("  Ungueltig. Bitte Format TT.MM.JJJJ verwenden (z.B. 15.06.2026).");
            }
        }
    }

    /**
     * Liest eine Uhrzeit im Format HH:mm vom Benutzer.
     *
     * @param scanner der Scanner fuer die Konsoleneingabe
     * @param prompt  die Eingabeaufforderung
     * @return eine gueltige {@link LocalTime}
     */
    public static LocalTime leseUhrzeit(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String eingabe = scanner.nextLine().trim();
            try {
                return LocalTime.parse(eingabe);
            } catch (DateTimeParseException e) {
                System.out.println("  Ungueltig. Bitte Format HH:mm verwenden (z.B. 09:30).");
            }
        }
    }

    /**
     * Liest eine Ja/Nein-Entscheidung vom Benutzer.
     *
     * @param scanner der Scanner fuer die Konsoleneingabe
     * @param prompt  die Eingabeaufforderung
     * @return {@code true} bei "j", {@code false} bei "n"
     */
    public static boolean leseJaNein(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " [j/n]: ");
            String eingabe = scanner.nextLine().trim().toLowerCase();
            if (eingabe.equals("j") || eingabe.equals("ja")) return true;
            if (eingabe.equals("n") || eingabe.equals("nein")) return false;
            System.out.println("  Bitte 'j' (ja) oder 'n' (nein) eingeben.");
        }
    }
}
