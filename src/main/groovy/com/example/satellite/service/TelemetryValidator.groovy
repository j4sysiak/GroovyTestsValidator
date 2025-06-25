package com.example.satellite.service

import com.example.satellite.model.SatelliteStatus
import com.example.satellite.model.TelemetryData
import com.example.satellite.model.ValidationResult

//  Serce systemu logika walidacji danych.
class TelemetryValidator {

    // Definicja stalych walidacyjnych
    static final long MAX_DATA_AGE_MS = 5 * 60 * 1000 // 5 minut
    static final double MIN_ALTITUDE_KM = 160.0
    static final double MAX_ALTITUDE_KM = 2000.0
    static final double MIN_TEMP_C = -50.0
    static final double MAX_TEMP_C = 100.0
    static final double MIN_SIGNAL_DBM = -90.0

    static ValidationResult validate(TelemetryData data) {
        def result = new ValidationResult()
        def currentTime = System.currentTimeMillis()

        // 1. Sprawdź świeżość danych
        if (currentTime - data.timestamp > MAX_DATA_AGE_MS) {
            result.addFailure("Data is outdated")
        }

        // 2. Sprawdź wysokość
        if (data.altitudeKm < MIN_ALTITUDE_KM || data.altitudeKm > MAX_ALTITUDE_KM) {
            result.addFailure("Altitude is out of operational range")
        }

        // 3. Sprawdź temperaturę
        if (data.temperatureCelsius < MIN_TEMP_C || data.temperatureCelsius > MAX_TEMP_C) {
            result.addFailure("Temperature is out of safe range")
        }

        // 4. Sprawdź silę sygnalu
        if (data.signalStrengthDBm < MIN_SIGNAL_DBM) {
            result.addFailure("Signal strength is too low")
        }

        // 5. Sprawdź status operacyjny satelity
        if (data.status != SatelliteStatus.ONLINE) {
            result.addFailure("Satellite is not in ONLINE status")
        }

        return result
    }
}