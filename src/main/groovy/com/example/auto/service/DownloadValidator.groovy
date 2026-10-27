package com.example.auto.service

import com.example.auto.model.AutoStatus
import com.example.auto.model.DownloadData
import com.example.auto.model.ValidationResult
import com.example.auto.model.Severity

//  Serce systemu logika walidacji danych.
class DownloadValidator {

    // Definicja stalych walidacyjnych
    static final long MAX_DATA_AGE_MS = 5 * 60 * 1000 // 5 minut
    static final double MIN_ALTITUDE_KM = 160.0
    static final double MAX_ALTITUDE_KM = 2000.0
    static final double MIN_TEMP_C = -50.0
    static final double MAX_TEMP_C = 100.0
    static final double MIN_SIGNAL_DBM = -90.0

    ValidationResult validate(DownloadData data) {
        def result = new ValidationResult()
        def currentTime = System.currentTimeMillis()

        // 1. Sprawdź świeżość danych (teraz to WARNING)
        if (currentTime - data.timestamp > MAX_DATA_AGE_MS) {
            result.addIssue("Data is outdated", Severity.WARNING)
        }

        // 2. Sprawdź wysokość (to jest ERROR)
        if (data.altitudeKm < MIN_ALTITUDE_KM || data.altitudeKm > MAX_ALTITUDE_KM) {
            result.addIssue("Altitude is out of operational range", Severity.ERROR)
        }

        // 3. Sprawdź temperaturę (to jest ERROR)
        if (data.temperatureCelsius < MIN_TEMP_C || data.temperatureCelsius > MAX_TEMP_C) {
            result.addIssue("Temperature is out of safe range", Severity.ERROR)
        }

        // 4. Sprawdź siłę sygnału (teraz to WARNING)
        if (data.signalStrengthDBm < MIN_SIGNAL_DBM) {
            result.addIssue("Signal strength is too low", Severity.WARNING)
        }

        // 5. Sprawdź status operacyjny (to jest ERROR)
        if (data.status != AutoStatus.ONLINE) {
            result.addIssue("Auto is not in ONLINE status", Severity.ERROR)
        }

        return result
    }
}