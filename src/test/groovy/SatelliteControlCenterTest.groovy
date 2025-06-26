package com.example.satellite

import com.example.satellite.model.SatelliteStatus // <-- DODAJ TĘ LINIĘ
import com.example.satellite.model.TelemetryData
import com.example.satellite.service.TelemetryValidator
import spock.lang.Specification

class SatelliteControlCenterTest extends Specification {

    // Używamy PRAWDZIWEGO walidatora, a nie mocka
    def validator = new TelemetryValidator()

    // Nasz obiekt poddawany testom (Subject Under Test)
    // Wstrzykujemy do niego mocka
    def controlCenter = new SatelliteControlCenter(validator)

    def "should generate correct report for a mix of valid and invalid data"() {
        given: "a list of data packets with various issues"
        def now = System.currentTimeMillis()

        // Przygotowujemy dane, które będą testować prawdziwą logikę walidatora
        def packet1_valid = new TelemetryData(
                satelliteId: 'SAT-01', status: SatelliteStatus.ONLINE, timestamp: now,
                altitudeKm: 500, temperatureCelsius: 25, signalStrengthDBm: -80
        )

        def packet2_error = new TelemetryData(
                satelliteId: 'SAT-02', status: SatelliteStatus.ONLINE, timestamp: now,
                altitudeKm: 100, // BŁĄD
                temperatureCelsius: 25, signalStrengthDBm: -80
        )

        def packet3_warning = new TelemetryData(
                satelliteId: 'SAT-03', status: SatelliteStatus.ONLINE, timestamp: now,
                altitudeKm: 500, temperatureCelsius: 25, signalStrengthDBm: -100 // OSTRZEŻENIE
        )

        def packets = [packet1_valid, packet2_error, packet3_warning]

        when: "we process the packets and generate a report"
        def report = controlCenter.processAndGenerateReport(packets)

        then: "the generated report contains the correct summary based on real validation"
        report.contains("Total packets processed: 3")
        report.contains("Accepted (no errors): 2")      // <-- OSTATECZNA POPRAWKA
        report.contains("Rejected (with errors): 1")    // <-- OSTATECZNA POPRAWKA

        and: "the report lists the correct issues"
        report.contains("Critical Errors Found:")
        report.contains("- Altitude is out of operational range")
        report.contains("Warnings Found:")
        report.contains("- Signal strength is too low")
    }
}
