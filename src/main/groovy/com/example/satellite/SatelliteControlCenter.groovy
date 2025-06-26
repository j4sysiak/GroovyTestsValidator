package com.example.satellite

import com.example.satellite.model.SatelliteStatus
import com.example.satellite.model.TelemetryData
import com.example.satellite.service.ReportGenerator
import com.example.satellite.service.TelemetryValidator

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

    String processAndGenerateReport(List<TelemetryData> packets) {
        // Krok 1: Uruchom walidacj? dla wszystkich pakiet?w
        def validationResults = packets.collect { validator.validate(it) }

        // Krok 2: Stw?rz instancj? generatora i przeka? mu wyniki
        // Generator sam zajmie si? reszt?!
        def reportGenerator = new ReportGenerator()
        def report = reportGenerator.generate(validationResults, packets.size())

        return report
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