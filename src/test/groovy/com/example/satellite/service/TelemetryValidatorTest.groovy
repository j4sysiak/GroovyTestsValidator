package com.example.satellite.service

import com.example.satellite.model.SatelliteStatus
import com.example.satellite.model.Severity
import com.example.satellite.model.TelemetryData
import spock.lang.Specification
import spock.lang.Unroll

class TelemetryValidatorTest extends Specification {

// Deklarujemy pola, które będą dostępne we wszystkich testach
    ValidatorConfig testConfig
    TelemetryValidator validator

    // Metoda setup() jest uruchamiana przed każdą metodą testową
    def setup() {
        // Tworzymy obiekt Properties bezpośrednio w teście
        def props = new Properties()
        props.setProperty('validation.data.max_age_ms', '300000')
        props.setProperty('validation.altitude.min_km', '160.0')
        props.setProperty('validation.altitude.max_km', '2000.0')
        props.setProperty('validation.temp.min_c', '-50.0')
        props.setProperty('validation.temp.max_c', '100.0')
        props.setProperty('validation.signal.min_dbm', '-90.0')

        // Używamy naszego nowego konstruktora
        testConfig = new ValidatorConfig(props)
        validator = new TelemetryValidator(testConfig)
    }

    // Wszystkie stare testy powinny teraz działać bez zmian,
    // ponieważ używają one obiektu 'validator', który jest tworzony
    // w metodzie setup() z kontrolowaną przez nas konfiguracją.

    // Kompleksowy test dla walidatora napisany w Spocku.
    // Wykorzystuje potężny blok where: do testowania wielu przypadków naraz.

    /*test-1*/
    def "should pass validation for a perfect data packet"() {
        given: "a valid telemetry data packet"
        def validData = new TelemetryData(
                timestamp: System.currentTimeMillis() - 1000,
                altitudeKm: 500,
                temperatureCelsius: 25,
                signalStrengthDBm: -80,
                status: com.example.satellite.model.SatelliteStatus.ONLINE
        )

        when: "the data is validated"
        def result = validator.validate(validData)

        then: "the validation result is valid and has no issues"
        result.valid // Sprawdzamy, czy nie ma żadnych ERRORÓW
        result.issues.empty // Sprawdzamy, czy lista problemów (też WARNINGów) jest pusta
    }

    /*test-2*/
    @Unroll
    def "should report a #severity issue for #reason"() { // Zmieniliśmy nazwę, aby była bardziej opisowa
        given: "a telemetry data packet with an invalid value"
        def data = new TelemetryData(
                timestamp: timestamp,
                altitudeKm: altitude,
                temperatureCelsius: temperature,
                signalStrengthDBm: signalStrength,
                status: status
        )

        when: "the data is validated"
        def result = validator.validate(data)

        then: "the validation fails and reports a single issue with the correct severity"
        !result.valid || severity == Severity.WARNING // Wynik jest nieważny LUB to tylko ostrzeżenie

        result.issues.size() == 1
        result.issues[0].reason == expectedFailure
        result.issues[0].severity == severity // Sprawdzamy poziom ważności!

        where:
        reason                    | timestamp                           | altitude | temperature | signalStrength | status                  | severity          | expectedFailure
        "outdated data"           | System.currentTimeMillis() - 400000 | 500      | 25          | -80            | SatelliteStatus.ONLINE  | Severity.WARNING  | "Data is outdated"
        "altitude too low"        | System.currentTimeMillis()          | 100      | 25          | -80            | SatelliteStatus.ONLINE  | Severity.ERROR    | "Altitude is out of operational range"
        "altitude too high"       | System.currentTimeMillis()          | 3000     | 25          | -80            | SatelliteStatus.ONLINE  | Severity.ERROR    | "Altitude is out of operational range"
        "temperature too low"     | System.currentTimeMillis()          | 500      | -60         | -80            | SatelliteStatus.ONLINE  | Severity.ERROR    | "Temperature is out of safe range"
        "temperature too high"    | System.currentTimeMillis()          | 500      | 110         | -80            | SatelliteStatus.ONLINE  | Severity.ERROR    | "Temperature is out of safe range"
        "signal strength too low" | System.currentTimeMillis()          | 500      | 25          | -100           | SatelliteStatus.ONLINE  | Severity.WARNING  | "Signal strength is too low"
        "satellite is offline"    | System.currentTimeMillis()          | 500      | 25          | -80            | SatelliteStatus.OFFLINE | Severity.ERROR    | "Satellite is not in ONLINE status"
    }

    /*test-3*/
    def "should collect multiple issues of different severities"() {
        given: "a data packet with multiple issues (one error and one warning)"
        def badData = new TelemetryData(
                timestamp: System.currentTimeMillis() - 400000, // to będzie WARNING
                altitudeKm: 100, // to będzie ERROR
                temperatureCelsius: 25,
                signalStrengthDBm: -80,
                status: SatelliteStatus.ONLINE
        )

        when: "the data is validated"
        def result = validator.validate(badData)

        then: "the result is invalid and contains two issues"
        !result.valid // Jest nieważny, bo zawiera co najmniej jeden ERROR
        result.issues.size() == 2

        and: "it correctly identifies one error and one warning"
        result.errors.size() == 1
        result.warnings.size() == 1
        result.errors[0].reason == "Altitude is out of operational range"
        result.warnings[0].reason == "Data is outdated"
    }
}