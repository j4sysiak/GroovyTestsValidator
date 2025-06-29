package com.example.satellite.service

class ValidatorConfig {

    // Pola do przechowywania wartości z pliku
    long maxDataAgeMs
    double minAltitudeKm
    double maxAltitudeKm
    double minTempC
    double maxTempC
    double minSignalDbm

    // Konstruktor, który wczytuje plik .properties
    ValidatorConfig(String configFilePath) {
        def props = new Properties()
        def inputStream = getClass().getResourceAsStream(configFilePath)
        if (inputStream) {
            props.load(inputStream)

            // Wczytujemy wartości, konwertując je na odpowiednie typy.
            // Używamy metody .toLong() i .toDouble(), które Groovy dodaje do stringów.
            // Drugi argument to wartość domyślna, na wypadek gdyby klucza nie było w pliku.
            this.maxDataAgeMs = props.getProperty('validation.data.max_age_ms', '300000').toLong()
            this.minAltitudeKm = props.getProperty('validation.altitude.min_km', '160.0').toDouble()
            this.maxAltitudeKm = props.getProperty('validation.altitude.max_km', '2000.0').toDouble()
            this.minTempC = props.getProperty('validation.temp.min_c', '-50.0').toDouble()
            this.maxTempC = props.getProperty('validation.temp.max_c', '100.0').toDouble()
            this.minSignalDbm = props.getProperty('validation.signal.min_dbm', '-90.0').toDouble()
        } else {
            throw new FileNotFoundException("Cannot find configuration file: $configFilePath")
        }
    }

    // NOWY konstruktor, idealny do testów
    ValidatorConfig(Properties props) {
        this.maxDataAgeMs = props.getProperty('validation.data.max_age_ms', '300000').toLong()
        this.minAltitudeKm = props.getProperty('validation.altitude.min_km', '160.0').toDouble()
        this.maxAltitudeKm = props.getProperty('validation.altitude.max_km', '2000.0').toDouble()
        this.minTempC = props.getProperty('validation.temp.min_c', '-50.0').toDouble()
        this.maxTempC = props.getProperty('validation.temp.max_c', '100.0').toDouble()
        this.minSignalDbm = props.getProperty('validation.signal.min_dbm', '-90.0').toDouble()
    }
}