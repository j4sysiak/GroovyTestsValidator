package com.example.satellite.service

import com.example.satellite.model.ValidationResult
import spock.lang.Specification

class ReportGeneratorTest extends Specification {

    def reportGenerator = new ReportGenerator()

    def "should generate a correct report for a mix of valid and invalid results"() {
        given: "a list of validation results"
        def result1 = new ValidationResult() // Wynik poprawny

        def result2 = new ValidationResult()
        result2.addFailure("Data is outdated")
        result2.addFailure("Altitude is out of operational range")

        def result3 = new ValidationResult()
        result3.addFailure("Data is outdated") // Powtórzony powód

        def results = [result1, result2, result3]

        when: "the report is generated"
        def report = reportGenerator.generate(results, 3)

        then: "the report contains correct summaries and unique reasons"
        // Spock pozwala na bardzo czytelne, wielolinijkowe asercje
        report.contains("Total packets processed: 3")
        report.contains("Accepted packets: 1")
        report.contains("Rejected packets: 2")
        report.contains("Unique failure reasons found:")
        report.contains("- Data is outdated")
        report.contains("- Altitude is out of operational range")

        // Sprawdźmy też, czego w raporcie NIE MA
        !report.contains("Temperature is out of safe range")
    }

    def "should generate a report with no failure reasons when all data is valid"() {
        given: "a list of only valid results"
        def results = [new ValidationResult(), new ValidationResult()]

        when: "the report is generated"
        def report = reportGenerator.generate(results, 2)

        then: "the report indicates all packets were accepted and shows no reasons"
        report.contains("Accepted packets: 2")
        report.contains("Rejected packets: 0")
        !report.contains("Unique failure reasons found:")
    }
}