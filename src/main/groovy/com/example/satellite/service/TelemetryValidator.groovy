package com.example.satellite.service

import com.example.satellite.model.SatelliteStatus
import com.example.satellite.model.Severity
import com.example.satellite.model.TelemetryData
import com.example.satellite.model.ValidationResult

class TelemetryValidator {

    // Zamiast stałych, mamy teraz pole przechowujące konfigurację
    private final ValidatorConfig config

    // Wstrzykujemy konfigurację przez konstruktor
    TelemetryValidator(ValidatorConfig config) {
        this.config = config
    }

    ValidationResult validate(TelemetryData data) {
        def result = new ValidationResult()
        def currentTime = System.currentTimeMillis()

        // Używamy wartości z obiektu 'config', a nie stałych
        if (currentTime - data.timestamp > config.maxDataAgeMs) {
            result.addIssue("Data is outdated", Severity.WARNING)
        }
        if (data.altitudeKm < config.minAltitudeKm || data.altitudeKm > config.maxAltitudeKm) {
            result.addIssue("Altitude is out of operational range", Severity.ERROR)
        }
        if (data.temperatureCelsius < config.minTempC || data.temperatureCelsius > config.maxTempC) {
            result.addIssue("Temperature is out of safe range", Severity.ERROR)
        }
        if (data.signalStrengthDBm < config.minSignalDbm) {
            result.addIssue("Signal strength is too low", Severity.WARNING)
        }
        if (data.status != SatelliteStatus.ONLINE) {
            result.addIssue("Satellite is not in ONLINE status", Severity.ERROR)
        }

        return result
    }
}