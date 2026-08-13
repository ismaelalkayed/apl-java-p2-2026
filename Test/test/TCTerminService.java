package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.htwd.wi.zahnarzt.exception.DateiException;
import de.htwd.wi.zahnarzt.exception.TerminKonfliktException;
import de.htwd.wi.zahnarzt.logic.TerminService;
import de.htwd.wi.zahnarzt.model.Termin;
import de.htwd.wi.zahnarzt.persistence.TerminRepository;

/**
 * JUnit-5-Testklasse für den TerminService.
 * Testet Erstellen, Konfliktprüfung, Sortierung, Filterung
 * sowie Löschen von Terminen.
 */

class TCTerminService {

	static final String TEST_DATEI = "data/test_termine.json";

	TerminService service;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		// Vor jedem Test: alte Testdatei loeschen und frischen Service anlegen
		new File(TEST_DATEI).delete();
		service = new TerminService(new TerminRepository(TEST_DATEI));
	}

	@AfterEach 
	void tearDown() throws Exception {
		// Nach jedem Test: Testdatei wieder loeschen
		new File(TEST_DATEI).delete();
	}

	@Test
	void testTerminErstellenErfolgreich() throws Exception { 	// Einen Termin anlegen und pruefen, ob er korrekt zurückgegeben wird
		Termin t = service.erstellenUndSpeichern(
				LocalDate.of(2026, 6, 10), LocalTime.of(9, 0),
				30, 1, "Kontrolle", null);
		assertNotNull(t);										// Objekt darf nicht null sein
		assertEquals(1, t.getId());								// Erste ID muss 1 sein
		assertEquals(1, service.anzahl());						// Genau ein Termin im System
	}

	@Test
	void testIdHochzaehlen() throws Exception {					// Prüft, dass jeder neue Termin eine eindeutig höhere ID bekommt
		service.erstellenUndSpeichern(
				LocalDate.of(2026, 6, 10), LocalTime.of(9, 0), 30, 1, "Kontrolle", null);
		Termin t2 = service.erstellenUndSpeichern(
				LocalDate.of(2026, 6, 10), LocalTime.of(10, 0), 30, 1, "Fuellung", null);
		assertEquals(2, t2.getId());							// Zweiter Termin muss ID 2 haben
	}

	@Test
	void testKonfliktDirektUeberlappung() throws Exception {
		// Termin 09:00-09:30 anlegen
		service.erstellenUndSpeichern(
				LocalDate.of(2026, 6, 10), LocalTime.of(9, 0), 30, 1, "Kontrolle", null);
		// Termin 09:15-09:45 -> Konflikt
		assertThrows(TerminKonfliktException.class,
				() -> service.erstellenUndSpeichern(
						LocalDate.of(2026, 6, 10), LocalTime.of(9, 15), 30, 2, "Fuellung", null),
				"Es haette ein Zeitkonflikt erkannt werden muessen");
	}

	@Test
	void testKeinKonfliktAnderenTag() throws Exception {
		service.erstellenUndSpeichern(
				LocalDate.of(2026, 6, 10), LocalTime.of(9, 0), 30, 1, "Kontrolle", null);
		// Gleiche Zeit, anderer Tag -> kein Konflikt, beide Termine werden gespeichert
		service.erstellenUndSpeichern(
				LocalDate.of(2026, 6, 11), LocalTime.of(9, 0), 30, 2, "Kontrolle", null);
		assertEquals(2, service.anzahl());
	}

	@Test
	void testKeinKonfliktDirektAnschliessend() throws Exception {
		// 09:00-09:30
		service.erstellenUndSpeichern(
				LocalDate.of(2026, 6, 10), LocalTime.of(9, 0), 30, 1, "Kontrolle", null);
		// 09:30-10:00 -> grenzt direkt an, kein Konflikt
		service.erstellenUndSpeichern(
				LocalDate.of(2026, 6, 10), LocalTime.of(9, 30), 30, 2, "Fuellung", null);
		assertEquals(2, service.anzahl());
	}

	@Test
	void testKonfliktEinschluss() throws Exception {
		// Langer Termin 09:00-10:00 vorhanden
		service.erstellenUndSpeichern(
				LocalDate.of(2026, 6, 10), LocalTime.of(9, 0), 60, 1, "OP", null);
		// Kurzer Termin 09:15-09:45 liegt komplett darin -> Konflikt
		assertThrows(TerminKonfliktException.class,
				() -> service.erstellenUndSpeichern(
						LocalDate.of(2026, 6, 10), LocalTime.of(9, 15), 30, 2, "Kontrolle", null),
				"Ein eingeschlossener Termin muss als Konflikt erkannt werden");
	}

	@Test
	void testSortierungNachDatum() throws Exception {
		// Termine in ungeordneter Reihenfolge anlegen
		service.erstellenUndSpeichern(
				LocalDate.of(2026, 6, 10), LocalTime.of(14, 0), 30, 1, "Termin3", null);
		service.erstellenUndSpeichern(
				LocalDate.of(2026, 6, 9), LocalTime.of(9, 0), 30, 1, "Termin1", null);
		service.erstellenUndSpeichern(
				LocalDate.of(2026, 6, 10), LocalTime.of(9, 0), 30, 1, "Termin2", null);

		// Sortierte Liste muss chronologisch korrekt sein
		List<Termin> sortiert = service.sortierteNachDatum();
		assertEquals("Termin1", sortiert.get(0).getBehandlungsArt());
		assertEquals("Termin2", sortiert.get(1).getBehandlungsArt());
		assertEquals("Termin3", sortiert.get(2).getBehandlungsArt());
	}

	@Test
	void testMonatsFilter() throws Exception {
		// Je einen Termin in Juni und Juli anlegen
		service.erstellenUndSpeichern(
				LocalDate.of(2026, 6, 1), LocalTime.of(9, 0), 30, 1, "Juni", null);
		service.erstellenUndSpeichern(
				LocalDate.of(2026, 7, 1), LocalTime.of(9, 0), 30, 1, "Juli", null);

		// Filter nach Juni darf nur den Juni-Termin zurückgeben
		List<Termin> juni = service.fuerMonat(2026, Month.JUNE);
		assertEquals(1, juni.size());
		assertEquals("Juni", juni.get(0).getBehandlungsArt());
	}

	@Test
	void testAuslastungProTag() throws Exception {
		// Drei Termine am selben Tag anlegen
		LocalDate tag = LocalDate.of(2026, 6, 15);
		service.erstellenUndSpeichern(tag, LocalTime.of(9, 0), 30, 1, "A", null);
		service.erstellenUndSpeichern(tag, LocalTime.of(10, 0), 30, 1, "B", null);
		service.erstellenUndSpeichern(tag, LocalTime.of(11, 0), 30, 2, "C", null);

		// Auslastungsmap muss für diesen Tag den Wert 3 liefern
		Map<LocalDate, Long> auslastung = service.auslastungProTag(2026, Month.JUNE);
		assertEquals(3L, auslastung.get(tag));
	}

	@Test
	void testTerminLoeschen() throws Exception {
		// Termin anlegen und danach löschen
		Termin t = service.erstellenUndSpeichern(
				LocalDate.of(2026, 6, 10), LocalTime.of(9, 0), 30, 1, "Test", null);

		boolean ergebnis = service.loeschen(t.getId());
		assertTrue(ergebnis);            // Löschen muss true zurückgeben
        assertEquals(0, service.anzahl()); // Liste muss danach leer sein

	}

	@Test
	void testLoeschenNichtVorhanden() throws Exception {
		// Löschen einer nicht existierenden ID muss false zurückgeben
		boolean ergebnis = service.loeschen(999);
		assertFalse(ergebnis);
	}
}
