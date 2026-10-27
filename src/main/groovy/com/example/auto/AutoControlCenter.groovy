package com.example.auto

import com.example.auto.model.ActionDecision
import com.example.auto.model.AutoStatus
import com.example.auto.model.DownloadData
import com.example.auto.service.CommandIssuer
import com.example.auto.service.ReportGenerator
import com.example.auto.service.DownloadDataReader
import com.example.auto.service.DownloadValidator

class AutoControlCenter {

    private final DownloadValidator validator

    AutoControlCenter(DownloadValidator validator) {
        this.validator = validator
    }

    List<ActionDecision> processPacketsAndDecideCommands(List<DownloadData> packets) {
        println "\n--- Processing ${packets.size()} data packets ---"

        // To jest serce przetwarzania strumieniowego w Groovy!
        def decisions = packets
        // Krok 1: Waliduj każdy pakiet
                .collect { validator.validate(it) }
        // Krok 2: Odrzuć puste wyniki (na wszelki wypadek)
                .findAll { it != null }
        // Krok 3: Zbierz wszystkie problemy (błędy i ostrzeżenia) do jednej listy
                .collectMany { it.issues }
        // Krok 4: Pogrupuj problemy według ID auto, z którego pochodzą
        // Niestety, musimy wrócić do danych wejściowych, żeby to zrobić. Uprośćmy to.
        // Poniżej lepsze podejście.

        // Lepsze podejście:
        // Najpierw grupujemy pakiety, potem walidujemy i decydujemy dla każdej grupy.
        def commandIssuer = new CommandIssuer()

        def decisionsByAuto = packets
        // Krok 1: Grupujemy pakiety po ID auto. Wynikiem jest mapa [ID -> ListaPakietów]
                .groupBy { it.autoId }
        // Krok 2: Przechodzimy przez każdą parę (ID, ListaPakietów) w mapie
                .collect { autoId, autoPackets ->
                    // Krok 3: Dla danego auto, zbieramy WSZYSTKIE problemy ze wszystkich jego pakietów
                    def allIssuesForAuto = autoPackets
                            .collect { validator.validate(it) }
                            .collectMany { it.issues }

                    // Krok 4: Podejmujemy JEDNĄ decyzję dla auto na podstawie wszystkich jego problemów
                    return commandIssuer.decideCommand(autoId, allIssuesForAuto)
                }

        return decisionsByAuto
    }

    static void main(String[] args) {
        println "--- Auto Control Center Initializing ---"

        // 1. Stwórz zależno?ci
        def dataReader = new DownloadDataReader()
        def validator = new DownloadValidator()
        def reportGenerator = new ReportGenerator()
        def controlCenter = new AutoControlCenter(validator) // Przekazujemy tylko walidator

        // 2. Wczytaj dane z pliku JSON
        println "Reading Download stream from file..."
        def packetsToProcess = dataReader.readFromFile('/download-stream.json')

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

    String processAndGenerateReport(List<DownloadData> packets) {
        // Krok 1: Uruchom walidacj? dla wszystkich pakiet?w
        def validationResults = packets.collect { validator.validate(it) }

        // Krok 2: Stw?rz instancj? generatora i przeka? mu wyniki
        // Generator sam zajmie si? reszt?!
        def reportGenerator = new ReportGenerator()
        def report = reportGenerator.generate(validationResults, packets.size())

        return report
    }

    private static List<DownloadData> createSamplePackets() {
        def now = System.currentTimeMillis()
        return [
                new DownloadData(autoId: 'AUTO-001', status: AutoStatus.ONLINE, timestamp: now - 10000, altitudeKm: 400.0, temperatureCelsius: 25.5, signalStrengthDBm: -75.0),
                new DownloadData(autoId: 'AUTO-002', status: AutoStatus.ONLINE, timestamp: now - (6 * 60 * 1000), altitudeKm: 500.0, temperatureCelsius: 30.0, signalStrengthDBm: -80.0),
                new DownloadData(autoId: 'AUTO-003', status: AutoStatus.ONLINE, timestamp: now, altitudeKm: 100.0, temperatureCelsius: 150.0, signalStrengthDBm: -100.0),
                new DownloadData(autoId: 'AUTO-004', status: AutoStatus.OFFLINE, timestamp: now, altitudeKm: 600.0, temperatureCelsius: 20.0, signalStrengthDBm: -70.0)
        ]
    }
}