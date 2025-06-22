package com.example.satellite

import com.example.satellite.model.SatelliteStatus
import com.example.satellite.model.TelemetryData
import com.example.satellite.model.ValidationResult
import com.example.satellite.service.TelemetryValidator

// Gl?wna klasa aplikacji, kt?ra symuluje dzialanie systemu.
class SatelliteControlCenter {

    static void main(String[] args) {
        println "--- Satellite Control Center Initializing ---"

        def validator = new TelemetryValidator()
        def now = System.currentTimeMillis()

        // Przygotujmy kilka pakiet?w danych do test?w
        def validData = new TelemetryData(
                satelliteId: 'SAT-001',
                timestamp: now - 10000, // 10 sekund temu
                altitudeKm: 400.0,
                temperatureCelsius: 25.5,
                signalStrengthDBm: -75.0,
                status: SatelliteStatus.OFFLINE // <-- DODANE
        )

        def oldData = new TelemetryData(
                satelliteId: 'SAT-002',
                timestamp: now - (6 * 60 * 1000), // 6 minut temu
                altitudeKm: 500.0,
                temperatureCelsius: 30.0,
                signalStrengthDBm: -80.0,
                status: SatelliteStatus.ONLINE // <-- DODANE
        )

        def failingData = new TelemetryData(
                satelliteId: 'SAT-003',
                timestamp: now,
                altitudeKm: 100.0, // Za nisko
                temperatureCelsius: 150.0, // Za gor?co
                signalStrengthDBm: -100.0, // Za s?aby sygna?
                status: SatelliteStatus.ONLINE // <-- DODANE
        )

        def offlineData = new TelemetryData(
                satelliteId: 'SAT-004',
                timestamp: now,
                altitudeKm: 600.0,
                temperatureCelsius: 20.0,
                signalStrengthDBm: -70.0,
                status: SatelliteStatus.OFFLINE // <-- Ten powinien zosta? odrzucony!
        )
        
        def packetsToProcess = [validData, oldData, failingData, offlineData]

        /*
        // Przetwarzanie danych - 1
        println "\n--- Processing all data packets ---"
        packetsToProcess.each { data ->
            println "\nProcessing data for: ${data.satelliteId}"
            def result = validator.validate(data)

            if (result.valid) {
                println "--> VALIDATION PASSED. Data accepted."
            } else {
                println "--> VALIDATION FAILED. Reasons:"
                result.failureReasons.each { reason ->
                    println "    - $reason"
                }
            }
        }
        println "\n--- Processing Complete ---"
        */

        // Przetwarzanie danych - 2
        println "\n--- Processing all data packets ---"
        // U?ywamy metody .collect(), aby przetworzy? ka?dy element i zebra? wyniki do nowej listy
        def validationResults = packetsToProcess.collect { data ->
            println "Processing data for: ${data.satelliteId}"
            return validator.validate(data) // Zwracamy wynik walidacji
        }
        println "--- Processing Complete ---"


        // --- NOWY KOD: Generowanie raportu ---
        println "\n--- Validation Report ---"
        // U?ywamy metody .count() z domkni?ciem, aby policzy? elementy spe?niaj?ce warunek
        def acceptedCount = validationResults.count { it.valid }
        def rejectedCount = validationResults.count { !it.valid }

        println "Total packets processed: ${packetsToProcess.size()}"
        println "Accepted packets: $acceptedCount"
        println "Rejected packets: $rejectedCount"

        // Teraz zbierzmy wszystkie powody odrzucenia
        // 1. .findAll() - filtruje list?, zostawiaj?c tylko odrzucone wyniki
        // 2. .collectMany() - bierze ka?dy odrzucony wynik i wyci?ga z niego list? powod?w, a nast?pnie sp?aszcza wszystko do jednej, du?ej listy
        // 3. .unique() - usuwa duplikaty
        def allFailureReasons = validationResults
                .findAll { !it.valid }
                .collectMany { it.failureReasons }
                .unique()

        if (allFailureReasons) { // Sprawdza, czy lista nie jest pusta
            println "\nUnique failure reasons found:"
            allFailureReasons.each { reason ->
                println " - $reason"
            }
        }
        println "\n--- End of Report ---"
    }
}