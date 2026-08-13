# ZahnarztVerwaltung

Konsolenbasierte Java-Anwendung zur Verwaltung von Patienten und Terminen einer Zahnarztpraxis. Entstanden als Beleg­aufgabe im Modul **Programmierung 2** (Wirtschaftsinformatik, HTW Dresden, Sommersemester 2026).

## Über das Projekt

Die Anwendung bildet den Praxisalltag einer Zahnarztpraxis ab: Patienten können angelegt, gesucht und verwaltet werden, Termine lassen sich vergeben und auf Konflikte prüfen. Alle Daten werden persistent im JSON-Format gespeichert, sodass der Datenbestand über mehrere Programmstarts hinweg erhalten bleibt.

Das Projekt wurde bewusst in drei fachliche Schichten gegliedert, um eine klare Trennung von Verantwortlichkeiten zu erreichen:

- **View-Schicht** – Benutzerinteraktion über die Konsole
- **Logik-Schicht** – Geschäftsregeln und Ablaufsteuerung
- **Persistenz-Schicht** – Speichern und Laden der Daten

## Funktionen

- Anlegen, Suchen und Verwalten von Patientendaten
- Vergabe und Verwaltung von Terminen inklusive Konfliktprüfung
- Persistente Speicherung aller Daten im JSON-Format (`data/patienten.json`, `data/termine.json`)
- Eingabevalidierung zur Vermeidung von Fehleingaben
- Individuelles Exception-Handling für fachliche Fehlerfälle
- Automatisierte Tests der Kernlogik mit JUnit 5

## Projektstruktur

```
ZahnarztVerwaltung/
├── src/de/htwd/wi/zahnarzt/
│   ├── Main.java                  Einstiegspunkt der Anwendung
│   ├── exception/                 Fachliche Ausnahmen
│   │   ├── DateiException.java
│   │   ├── PatientNichtGefundenException.java
│   │   └── TerminKonfliktException.java
│   ├── logic/                     Geschäftslogik
│   │   ├── PatientenService.java
│   │   └── TerminService.java
│   ├── model/                     Datenklassen
│   │   ├── Person.java
│   │   ├── Patient.java
│   │   ├── Termin.java
│   │   └── Terminpruefbar.java
│   ├── persistence/                Datenzugriff (JSON)
│   │   ├── PatientRepository.java
│   │   └── TerminRepository.java
│   ├── util/
│   │   └── InputHelper.java        Eingabevalidierung
│   └── view/
│       └── KonsolenView.java        Konsolen-Menüführung
├── Test/test/                       JUnit-Tests
│   ├── TCPatientenService.java
│   └── TCTerminService.java
└── data/                             Persistente Datendateien
    ├── patienten.json
    └── termine.json
```

## Technische Umsetzung

| Aspekt | Umsetzung |
|---|---|
| Programmiersprache | Java (JDK 17) |
| Architektur | 3-Schichten-Architektur (View, Logic, Persistence) |
| Persistenz | JSON-Dateien |
| Objektorientierung | Vererbung über `Person` als Basisklasse, Interface `Terminpruefbar` |
| Fehlerbehandlung | Eigene Exception-Klassen für fachliche Fehlerfälle |
| Tests | JUnit 5 für Logik-Schicht |
| Entwicklungsumgebung | Eclipse |

## Voraussetzungen

- Java Development Kit (JDK) 17 oder höher
- Eclipse IDE (empfohlen) oder eine andere Java-IDE
- JUnit 5 zur Ausführung der Tests

## Ausführen

1. Repository klonen:
   ```bash
   git clone https://github.com/ismaelalkayed/apl-java-p2-2026.git
   ```
2. Projekt in Eclipse importieren (**File → Import → Existing Projects into Workspace**)
3. `Main.java` ausführen, um die Konsolenanwendung zu starten

## Tests ausführen

Die JUnit-Tests liegen unter `Test/test/` und lassen sich direkt aus Eclipse heraus ausführen (Rechtsklick auf die Testklasse → **Run As → JUnit Test**).

## Autor

Ismael Alkayed – Wirtschaftsinformatik, HTW Dresden

