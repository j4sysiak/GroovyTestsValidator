package com.example.satellite

import com.example.satellite.model.SatelliteStatus
import com.example.satellite.model.TelemetryData
import com.example.satellite.service.TelemetryValidator
import com.example.satellite.service.ReportGenerator

// Gl?wna klasa aplikacji, kt?ra symuluje dzialanie systemu.
class SatelliteControlCenter {

    // Przechowujemy walidator jako pole
    private final TelemetryValidator validator

    // Konstruktor, kt?ry pozwala "wstrzykn??" walidator (prawdziwy lub mocka)
    SatelliteControlCenter(TelemetryValidator validator) {
        this.validator = validator
    }

    // Metoda main jest teraz bardzo prosta - tylko uruchamia system
    static void main(String[] args) {
        println "--- Satellite Control Center Initializing ---"

        // 1. Stw?rz zale?no?ci
        def validator = new TelemetryValidator()
        def packetsToProcess = createSamplePackets()

        // 2. Stw?rz instancj? naszej klasy, wstrzykuj?c zale?no??
        def controlCenter = new SatelliteControlCenter(validator)

        // 3. Uruchom logik? i pobierz raport
        def report = controlCenter.processAndGenerateReport(packetsToProcess)

        // 4. Wy?wietl wynik
        println(report)
        println "--- End of Report ---"
    }

    // Przenie?li?my g??wn? logik? do oddzielnej, publicznej metody
    String processAndGenerateReport(List<TelemetryData> packets) {
        def validationResults = packets.collect { validator.validate(it) }

        def acceptedCount = validationResults.count { it.valid }
        def rejectedCount = validationResults.count { !it.valid }

        def allFailureReasons = validationResults
                .findAll { !it.valid }
                .collectMany { it.failureReasons }
                .unique()

        // Zamiast drukowa?, budujemy stringa i go zwracamy. To u?atwia testowanie.
        def report = new StringBuilder()
        report.append("--- Validation Report ---\n")
        report.append("Total packets processed: ${packets.size()}\n")
        report.append("Accepted packets: $acceptedCount\n")
        report.append("Rejected packets: $rejectedCount\n")

        if (allFailureReasons) {
            report.append("\nUnique failure reasons found:\n")
            allFailureReasons.each { reason ->
                report.append(" - $reason\n")
            }
        }

        return report.toString()
    }

    // Pomocnicza metoda do tworzenia danych, ?eby nie za?mieca? main
    private static List<TelemetryData> createSamplePackets() {
        def now = System.currentTimeMillis()
        return [
                new TelemetryData(satelliteId: 'SAT-001', status: SatelliteStatus.ONLINE, timestamp: now - 10000, altitudeKm: 400.0, temperatureCelsius: 25.5, signalStrengthDBm: -75.0),
                new TelemetryData(satelliteId: 'SAT-002', status: SatelliteStatus.ONLINE, timestamp: now - (6 * 60 * 1000), altitudeKm: 500.0, temperatureCelsius: 30.0, signalStrengthDBm: -80.0),
                new TelemetryData(satelliteId: 'SAT-003', status: SatelliteStatus.ONLINE, timestamp: now, altitudeKm: 100.0, temperatureCelsius: 150.0, signalStrengthDBm: -100.0),
                new TelemetryData(satelliteId: 'SAT-004', status: SatelliteStatus.OFFLINE, timestamp: now, altitudeKm: 600.0, temperatureCelsius: 20.0, signalStrengthDBm: -70.0)
        ]
    }
}