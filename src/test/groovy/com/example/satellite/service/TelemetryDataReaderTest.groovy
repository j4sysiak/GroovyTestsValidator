package com.example.satellite.service

import spock.lang.Specification
import spock.lang.TempDir

class TelemetryDataReaderTest extends Specification {

    def dataReader = new TelemetryDataReader()

    @TempDir
    File tempDir

    def "should correctly read and parse a valid JSON file"() {
        given: "a temporary file with valid JSON content"
        def validJson = """
        [
          {
            "satelliteId": "SAT-VALID",
            "timestamp": 1672531200000,
            "altitudeKm": 450.5,
            "temperatureCelsius": 22.7,
            "signalStrengthDBm": -78.0,
            "status": "ONLINE"
          }
        ]
        """
        def testFile = new File(tempDir, "good-data.json")
        testFile.text = validJson

        when: "the data reader reads the file"
        def result = dataReader.readFromFile(testFile.absolutePath)

        then: "it returns a list with one valid TelemetryData object"
        result.size() == 1

        and: "the data in the object is correct"
        def dataObject = result[0]
        dataObject.satelliteId == "SAT-VALID"
        dataObject.altitudeKm == 450.5
        dataObject.status.toString() == "ONLINE"
    }

    def "should ignore invalid records and only return valid ones"() {
        given: "a file with a mix of good and bad data"
        def mixedJson = """
        [
          { "satelliteId": "GOOD-1", "timestamp": 1, "altitudeKm": 200, "temperatureCelsius": 20, "signalStrengthDBm": -80, "status": "ONLINE" },
          { "satelliteId": "BAD-1 (missing altitude)" },
          { "satelliteId": "GOOD-2", "timestamp": 2, "altitudeKm": 300, "temperatureCelsius": 25, "signalStrengthDBm": -70, "status": "ONLINE" }
        ]
        """
        def testFile = new File(tempDir, "mixed-data.json")
        testFile.text = mixedJson

        when: "the reader processes the file"
        def result = dataReader.readFromFile(testFile.absolutePath)

        then: "it returns a list containing only the two valid records"
        result.size() == 2
        result.any { it.satelliteId == "GOOD-1" }
        result.any { it.satelliteId == "GOOD-2" }
    }

    def "should return an empty list if the file does not exist"() {
        given: "a path to a non-existent file"
        def nonExistentPath = new File(tempDir, "non/existent/file.json").absolutePath

        when: "the reader tries to read the file"
        def result = dataReader.readFromFile(nonExistentPath)

        then: "it returns an empty list"
        result.empty
    }
}
