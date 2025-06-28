package com.example.satellite.service

import com.example.satellite.model.Severity
import com.example.satellite.model.ValidationResult
import spock.lang.Specification

class ReportGeneratorTest extends Specification {




    def reportGenerator = new ReportGenerator()

    def "should generate a correct report for a mix of valid and invalid results"() {
        given: "a list of validation results with errors and warnings"
        def result1 = new ValidationResult()

        def result2 = new ValidationResult()
        result2.addIssue("Altitude is out of operational range", Severity.ERROR)
        result2.addIssue("Signal strength is too low", Severity.WARNING)

        def result3 = new ValidationResult()
        result3.addIssue("Signal strength is too low", Severity.WARNING) // Powtórzone ostrzeżenie

        def results = [result1, result2, result3]

        when: "the report is generated"
        def report = reportGenerator.generate(results, 3)

        then: "the report contains correct summaries and unique reasons for errors and warnings"
        report.contains("Total packets processed: 3")
        report.contains("Accepted (no errors): 2") // result1 i result3 są 'valid'
        report.contains("Rejected (with errors): 1") // tylko result2 jest 'invalid'

        and: "it lists errors and warnings separately"
        report.contains("Critical Errors Found:")
        report.contains("- Altitude is out of operational range")

        report.contains("Warnings Found:")
        report.contains("- Signal strength is too low")

        and: "it does not contain duplicated reasons"
        // Sprawdzamy, ile razy występuje dany tekst. Powinien wystąpić tylko raz.
        (report.count("Signal strength is too low")) == 1
    }

    def "should generate a report with no failure reasons when all data is valid"() {
        given: "a list of only valid results"
        def results = [new ValidationResult(), new ValidationResult()]

        when: "the report is generated"
        def report = reportGenerator.generate(results, 2)

        then: "the report indicates all packets were accepted and shows no reasons"
        report.contains("Accepted (no errors): 2")
        report.contains("Rejected (with errors): 0")
        !report.contains("Critical Errors Found:")
        !report.contains("Warnings Found:")
    }
}