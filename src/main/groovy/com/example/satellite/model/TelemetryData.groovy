package com.example.satellite.model

import groovy.transform.ToString

// klasa przechowująca dane z jednego pakietu telemetrycznego.
@ToString // Ulatwia drukowanie obiektu
class TelemetryData {
    String satelliteId
    long timestamp // Czas w milisekundach (Unix time)
    double altitudeKm // Wysokość w kilometrach
    double temperatureCelsius // Temperatura w stopniach Celsjusza
    double signalStrengthDBm // Sila sygnalu w dBm
    SatelliteStatus status
}