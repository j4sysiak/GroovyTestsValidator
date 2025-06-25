package com.example

import com.example.satellite.SatelliteControlCenter
import com.example.satellite.model.TelemetryData
import com.example.satellite.model.ValidationResult
import com.example.satellite.service.TelemetryValidator
import spock.lang.Specification

class SatelliteControlCenterTest extends Specification {

    def "should generate correct report for mixed valid and invalid data"() {
        given: "a mock validator and some data packets"
        // 1. Tworzymy "atrapę" walidatora. On nie ma żadnej logiki.
        TelemetryValidator mockValidator = Mock()

        def validPacket = new TelemetryData()
        def invalidPacket = new TelemetryData()
        def packets = [validPacket, invalidPacket]

        // 2. Tworzymy instancję naszej klasy, wstrzykując FAŁSZYWY walidator
        def controlCenter = new SatelliteControlCenter(mockValidator)

        when: "we process the packets and generate a report"
        // 3. Definiujemy zachowanie naszego mocka.
        // Mówimy: "Gdy ktoś wywoła na tobie metodę 'validate' z 'validPacket', ZWRÓĆ poprawny wynik"
        mockValidator.validate(validPacket) >> new ValidationResult(valid: true)

        // Mówimy: "Gdy ktoś wywoła na tobie metodę 'validate' z 'invalidPacket', ZWRÓĆ wynik z błędem"
        def failureResult = new ValidationResult(valid: false, failureReasons: ["Test Failure"])
        mockValidator.validate(invalidPacket) >> failureResult

        // 4. Uruchamiamy naszą logikę
        def report = controlCenter.processAndGenerateReport(packets)

        then: "the report summary is correct"
        // 5. Sprawdzamy, czy raport zawiera poprawne podsumowanie
        report.contains("Total packets processed: 2")
        report.contains("Accepted packets: 0")
        report.contains("Rejected packets: 2")
        report.contains("Unique failure reasons found:")
        report.contains("Data is outdated")
        report.contains("Altitude is out of operational range")
        report.contains("Satellite is not in ONLINE status")
    }
}