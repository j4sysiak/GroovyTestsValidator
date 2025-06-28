package com.example.satellite

import com.example.satellite.model.ActionDecision
import com.example.satellite.model.SatelliteStatus
import com.example.satellite.model.TelemetryData
import com.example.satellite.service.CommandIssuer
import com.example.satellite.service.ReportGenerator
import com.example.satellite.service.TelemetryDataReader
import com.example.satellite.service.TelemetryValidator

// Glówna klasa aplikacji, kt?ra symuluje dzialanie systemu.
class SatelliteControlCenter {

    private final TelemetryValidator validator

    SatelliteControlCenter(TelemetryValidator validator) {
        this.validator = validator
    }

// W klasie SatelliteControlCenter

    List<ActionDecision> processPacketsAndDecideCommands(List<TelemetryData> packets) {
        println "\n--- Processing ${packets.size()} data packets ---"

        // To jest serce przetwarzania strumieniowego w Groovy!
        def decisions = packets
        // Krok 1: Waliduj każdy pakiet
                .collect { validator.validate(it) }
        // Krok 2: Odrzuć puste wyniki (na wszelki wypadek)
                .findAll { it != null }
        // Krok 3: Zbierz wszystkie problemy (błędy i ostrzeżenia) do jednej listy
                .collectMany { it.issues }
        // Krok 4: Pogrupuj problemy według ID satelity, z którego pochodzą
        // Niestety, musimy wrócić do danych wejściowych, żeby to zrobić. Uprośćmy to.
        // Poniżej lepsze podejście.

        // Lepsze podejście:
        // Najpierw grupujemy pakiety, potem walidujemy i decydujemy dla każdej grupy.
        def commandIssuer = new CommandIssuer()

        def decisionsBySatellite = packets
        // Krok 1: Grupujemy pakiety po ID satelity. Wynikiem jest mapa [ID -> ListaPakietów]
                .groupBy { it.satelliteId }
        // Krok 2: Przechodzimy przez każdą parę (ID, ListaPakietów) w mapie
                .collect { satelliteId, satellitePackets ->
                    // Krok 3: Dla danego satelity, zbieramy WSZYSTKIE problemy ze wszystkich jego pakietów
                    def allIssuesForSatellite = satellitePackets
                            .collect { validator.validate(it) }
                            .collectMany { it.issues }

                    // Krok 4: Podejmujemy JEDNĄ decyzję dla satelity na podstawie wszystkich jego problemów
                    return commandIssuer.decideCommand(satelliteId, allIssuesForSatellite)
                }

        return decisionsBySatellite
    }

    static void main(String[] args) {
        println "--- Satellite Control Center Initializing ---"

        // 1. Stwórz zależno?ci
        def dataReader = new TelemetryDataReader()
        def validator = new TelemetryValidator()
        def reportGenerator = new ReportGenerator()
        def controlCenter = new SatelliteControlCenter(validator) // Przekazujemy tylko walidator

        // 2. Wczytaj dane z pliku JSON
        println "Reading telemetry stream from file..."
        def packetsToProcess = dataReader.readFromFile('/telemetry-stream.json')

        if (packetsToProcess.empty) {
            println "No valid data packets to process. Exiting."
            return
        }

        // 3. Uruchom logikę i pobierz decyzje
        def decisions = controlCenter.processPacketsAndDecideCommands(packetsToProcess)

        // 4. Wyświetl wynik
        println "\n--- Action Decisions ---"
        decisions.each { println it }
        println "----------------------"
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