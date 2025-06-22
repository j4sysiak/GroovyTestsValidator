package com.example.satellite.service

import com.example.satellite.model.TelemetryData
import spock.lang.Specification
import spock.lang.Unroll
import com.example.satellite.model.SatelliteStatus

// Kompleksowy test dla walidatora napisany w Spocku.
// Wykorzystuje potężny blok where: do testowania wielu przypadków naraz.

class TelemetryValidatorTest extends Specification {

    def validator = new TelemetryValidator()

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

        then: "the validation result is valid"
        result.valid
        result.failureReasons.empty
    }

    @Unroll // Ta adnotacja sprawi, ďż˝e kaďż˝dy wiersz z 'where' bďż˝dzie osobnym testem
    def "should fail validation for #reason"() {
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

        then: "the validation fails for the correct reason"
        !result.valid
        result.failureReasons.contains(expectedFailure)

        where:
        reason                       | timestamp                            | altitude | temperature | signalStrength | status                   | expectedFailure
        "outdated data"              | System.currentTimeMillis() - 400000  | 500      | 25          | -80            | SatelliteStatus.ONLINE   | "Data is outdated"
        "altitude too low"           | System.currentTimeMillis()           | 100      | 25          | -80            | SatelliteStatus.ONLINE   | "Altitude is out of operational range"
        "altitude too high"          | System.currentTimeMillis()           | 3000     | 25          | -80            | SatelliteStatus.ONLINE   | "Altitude is out of operational range"
        "temperature too low"        | System.currentTimeMillis()           | 500      | -60         | -80            | SatelliteStatus.ONLINE   | "Temperature is out of safe range"
        "temperature too high"       | System.currentTimeMillis()           | 500      | 110         | -80            | SatelliteStatus.ONLINE   | "Temperature is out of safe range"
        "signal strength too low"    | System.currentTimeMillis()           | 500      | 25          | -100           | SatelliteStatus.ONLINE   | "Signal strength is too low"
        "satellite is offline"       | System.currentTimeMillis()           | 500      | 25          | -80            | SatelliteStatus.OFFLINE  | "Satellite is not in ONLINE status"

    }

    def "should collect all failure reasons for a multi-failure packet"() {
        given: "a data packet with multiple issues"
        def badData = new TelemetryData(
                timestamp: System.currentTimeMillis() - 400000, // stary
                altitudeKm: 100, // za nisko
                temperatureCelsius: 25,
                signalStrengthDBm: -100, // za sďż˝aby sygnaďż˝
                status: com.example.satellite.model.SatelliteStatus.ONLINE // <-- BRAKUJďż˝CA LINIA!
        )

        when: "the data is validated"
        def result = validator.validate(badData)

        then: "the result contains all three failure reasons"
        !result.valid
        result.failureReasons.size() == 3
        result.failureReasons.contains("Data is outdated")
        result.failureReasons.contains("Altitude is out of operational range")
        result.failureReasons.contains("Signal strength is too low")
    }
}