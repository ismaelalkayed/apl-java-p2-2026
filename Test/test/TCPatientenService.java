package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.htwd.wi.zahnarzt.exception.DateiException;
import de.htwd.wi.zahnarzt.exception.PatientNichtGefundenException;
import de.htwd.wi.zahnarzt.logic.PatientenService;
import de.htwd.wi.zahnarzt.model.Patient;
import de.htwd.wi.zahnarzt.persistence.PatientRepository;

/**
 * JUnit-5-Testklasse für den PatientenService.
 * Testet Anlegen, Suchen, Bearbeiten, Loeschen und Sortieren von Patienten.
 */

class TCPatientenService {

	static final String TEST_DATEI = "data/test_patienten.json";

	PatientenService service;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		// Vor jedem Test: alte Testdatei löschen und frischen Service anlegen
		new File(TEST_DATEI).delete();
		service = new PatientenService(new PatientRepository(TEST_DATEI));
	}
 
	@AfterEach
	void tearDown() throws Exception {
		// Nach jedem Test: Testdatei wieder löschen
		new File(TEST_DATEI).delete();
	}

	@Test
	void testPatientAnlegen() throws Exception {
		// Prüft, ob ein Patient korrekt angelegt und gespeichert wird
		Patient p = service.anlegenUndSpeichern("Anna", "Muster", 30, "AOK");
		assertNotNull(p);                     // Objekt darf nicht null sein
        assertEquals(1, p.getId());           // Erste ID muss 1 sein
        assertEquals("Anna", p.getVorname()); // Vorname muss korrekt gesetzt sein
        assertEquals(1, service.anzahl());    // Genau ein Patient im System
	}

	@Test
	void testEindeutigeIds() throws Exception {
		// Zwei Patienten anlegen -> ihre IDs müssen verschieden sein
		Patient p1 = service.anlegenUndSpeichern("Anna", "Muster", 30, "AOK");
		Patient p2 = service.anlegenUndSpeichern("Ben", "Test", 25, "TK");
		assertNotEquals(p1.getId(), p2.getId());
	}

	@Test
	void testFindNachId() throws Exception {
		// Angelegten Patienten per ID wieder finden
		Patient p = service.anlegenUndSpeichern("Anna", "Muster", 30, "AOK");
		Patient gefunden = service.findeNachId(p.getId());
		assertEquals(p.getId(), gefunden.getId());
	}

	@Test
	void testFindNachIdNichtGefunden() throws Exception {
		// Suche nach nicht existierender ID muss Exception auslösen
		assertThrows(PatientNichtGefundenException.class,
				() -> service.findeNachId(999),
				"Eine nicht vorhandene ID muss eine Exception ausloesen");
	}

	@Test
	void testSucheNachName() throws Exception {
		// Zwei Patienten anlegen, nur einer passt zum Suchbegriff
		service.anlegenUndSpeichern("Anna", "Muster", 30, "AOK");
		service.anlegenUndSpeichern("Ben", "Test", 25, "TK");

		List<Patient> ergebnis = service.suchteNachName("muster");
		assertEquals(1, ergebnis.size());
		assertEquals("Anna", ergebnis.get(0).getVorname());
	}

	@Test
	void testSucheCaseInsensitiv() throws Exception {
		// Suche mit GROSSBUCHSTABEN muss denselben Patienten finden
		service.anlegenUndSpeichern("Anna", "Mueller", 30, "AOK");
		List<Patient> ergebnis = service.suchteNachName("MUELLER");
		assertEquals(1, ergebnis.size());
	}

	@Test
	void testBearbeiten() throws Exception {
		// Patient anlegen und dann seine Daten ändern
		Patient p = service.anlegenUndSpeichern("Anna", "Muster", 30, "AOK");
		service.bearbeiten(p.getId(), "Anna", "Neuname", 31, "TK");
		// Geänderter Patient aus dem Service laden und Werte prüfen

		Patient aktualisiert = service.findeNachId(p.getId());
		assertEquals("Neuname", aktualisiert.getNachname());
		assertEquals(31, aktualisiert.getAlter());
		assertEquals("TK", aktualisiert.getKrankenkasse());
	}

	@Test
	void testLoeschen() throws Exception {
		// Patient anlegen, löschen, Anzahl muss 0 sein
		Patient p = service.anlegenUndSpeichern("Anna", "Muster", 30, "AOK");
		service.loeschen(p.getId());
		assertEquals(0, service.anzahl());
	}

	@Test
	void testGeloeschterNichtAuffindbar() throws Exception {
		// Gelöschter Patient darf nicht mehr per findeNachId auffindbar sein
		Patient p = service.anlegenUndSpeichern("Anna", "Muster", 30, "AOK");
		int id = p.getId();
		service.loeschen(id);
		assertThrows(PatientNichtGefundenException.class,
				() -> service.findeNachId(id),
				"Ein geloeschter Patient darf nicht mehr auffindbar sein");
	}

	@Test
	void testSortierungNachNachname() throws Exception {
		// Drei Patienten in falscher alphabetischer Reihenfolge anlegen
		service.anlegenUndSpeichern("C", "Zimmermann", 40, "AOK");
		service.anlegenUndSpeichern("A", "Bauer", 30, "TK");
		service.anlegenUndSpeichern("B", "Meier", 35, "BKK");

		// Sortierte Liste muss alphabetisch nach Nachname geordnet sein
		List<Patient> sortiert = service.sortierteNachNachname();
		assertEquals("Bauer", sortiert.get(0).getNachname());
		assertEquals("Meier", sortiert.get(1).getNachname());
		assertEquals("Zimmermann", sortiert.get(2).getNachname());
	}

	@Test
	void testToString() throws Exception {
		// toString()-Methode muss alle wesentlichen Attribute enthalten
		Patient p = service.anlegenUndSpeichern("Anna", "Muster", 30, "AOK");
		String s = p.toString();
		assertTrue(s.contains("Anna"));
		assertTrue(s.contains("Muster"));
		assertTrue(s.contains("AOK"));
	}
}
