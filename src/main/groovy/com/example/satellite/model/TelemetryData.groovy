package com.example.satellite.model

import groovy.transform.ToString

/*
 enum SatelliteStatus {
    ONLINE, OFFLINE, MAINTENANCE
} 
 */


// klasa przechowuj¹ca dane z jednego pakietu telemetrycznego.
@ToString // U³atwia drukowanie obiektu
class TelemetryData {
    String satelliteId
    long timestamp // Czas w milisekundach (Unix time)
    double altitudeKm // Wysokoœæ w kilometrach
    double temperatureCelsius // Temperatura w stopniach Celsjusza
    double signalStrengthDBm // Si³a sygna³u w dBm
    SatelliteStatus status // <-- NOWE POLE
}