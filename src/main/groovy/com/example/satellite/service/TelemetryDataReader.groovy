package com.example.satellite.service

import com.example.satellite.model.SatelliteStatus
import com.example.satellite.model.TelemetryData
import groovy.json.JsonSlurper

class TelemetryDataReader {

    List<TelemetryData> readFromFile(String filePath) {
        try {
            InputStream inputStream

            // NOWA, INTELIGENTNA LOGIKA
            def file = new File(filePath)
            if (file.isAbsolute()) {
                // Jeśli ścieżka jest bezwzględna (jak w naszych testach), wczytaj ją bezpośrednio z systemu plików
                println "INFO: Reading from absolute file path: $filePath"
                if (!file.exists()) {
                    throw new FileNotFoundException("File not found at absolute path: $filePath")
                }
                inputStream = file.newInputStream()
            } else {
                // Jeśli ścieżka jest względna (jak w aplikacji '/telemetry-stream.json'), wczytaj ją jako zasób
                println "INFO: Reading from classpath resource: $filePath"
                inputStream = getClass().getResourceAsStream(filePath)
                if (inputStream == null) {
                    throw new FileNotFoundException("Cannot find resource file: $filePath")
                }
            }

            // Używamy JsonSlurper do sparsowania strumienia danych
            def jsonData = new JsonSlurper().parse(inputStream)

            // Reszta metody pozostaje bez zmian...
            def telemetryPackets = jsonData.collect { dataMap ->
                try {
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
                    println "WARN: Could not parse data record: $dataMap. Reason: ${e.message}"
                    return null
                }
            }

            return telemetryPackets.findAll { it != null }

        } catch (Exception e) {
            println "FATAL: Could not read or parse telemetry file: $filePath. Reason: ${e.message}"
            return []
        }
    }
}