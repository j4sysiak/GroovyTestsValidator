package com.example.satellite.service

import com.example.satellite.model.SatelliteData
import com.example.satellite.model.SatelliteStatus
import com.example.satellite.model.TelemetryData
import groovy.json.JsonSlurper

class TelemetryDataReader {

    List<TelemetryData> readFromFile(String filePath) {
        try {
            // Wczytujemy plik z zasobów
            def inputFile = getClass().getResource(filePath)
            if (!inputFile) {
                throw new FileNotFoundException("Cannot find resource file: $filePath")
            }

            // U?ywamy JsonSlurper do sparsowania JSONa
            def jsonData = new JsonSlurper().parse(inputFile)

            // Przekszta?camy list? map z JSONa na list? naszych obiektów TelemetryData
            def telemetryPackets = jsonData.collect { dataMap ->
                try {
                    // U?ywamy 'as' do bezpiecznej konwersji typu dla enuma
                    def status = dataMap.status as SatelliteStatus

                    return new TelemetryData(
                            satelliteId: dataMap.satelliteId,
                            timestamp: dataMap.timestamp,
                            altitudeKm: dataMap.altitudeKm,
                            temperatureCelsius: dataMap.temperatureCelsius,
                            signalStrengthDBm: dataMap.signalStrengthDBm,
                            status: status
                    )
                } catch (Exception e) {
                    // Je?li co? pójdzie nie tak (np. brak pola, z?y typ), zwracamy null
                    println "WARN: Could not parse data record: $dataMap. Reason: ${e.message}"
                    return null
                }
            }

            // Na ko?cu filtrujemy list?, aby usun?? wszystkie nulle, które powsta?y z b??dnych rekordów
            return telemetryPackets.findAll { it != null }

        } catch (Exception e) {
            println "FATAL: Could not read or parse telemetry file: $filePath. Reason: ${e.message}"
            return [] // Zwracamy pust? list? w przypadku powa?nego b??du
        }
    }
}