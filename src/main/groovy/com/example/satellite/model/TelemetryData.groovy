package com.example.satellite.model

import groovy.transform.ToString

/*
 enum SatelliteStatus {
    ONLINE, OFFLINE, MAINTENANCE
} 
 */


// klasa przechowuj�ca dane z jednego pakietu telemetrycznego.
@ToString // U�atwia drukowanie obiektu
class TelemetryData {
    String satelliteId
    long timestamp // Czas w milisekundach (Unix time)
    double altitudeKm // Wysoko�� w kilometrach
    double temperatureCelsius // Temperatura w stopniach Celsjusza
    double signalStrengthDBm // Si�a sygna�u w dBm
    SatelliteStatus status // <-- NOWE POLE
}