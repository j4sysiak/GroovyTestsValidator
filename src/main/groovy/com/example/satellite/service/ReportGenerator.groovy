package com.example.satellite.service

import com.example.satellite.model.ValidationResult

class ReportGenerator {

    String generate(List<ValidationResult> validationResults, int totalPackets) {
        // Używamy .with { ... } aby uniknąć powtarzania 'stringBuilder'
        def report = new StringBuilder().with {
            def acceptedCount = validationResults.count { it.valid }
            def rejectedCount = validationResults.count { !it.valid }

            append "--- Validation Report ---"
            append "Total packets processed: $totalPackets"
            append "Accepted packets: $acceptedCount"
            append "Rejected packets: $rejectedCount"

            def allFailureReasons = validationResults
                    .findAll { !it.valid }
                    .collectMany { it.failureReasons }
                    .unique()

            if (allFailureReasons) {
                append "\nUnique failure reasons found:"
                allFailureReasons.each { reason ->
                    append " - $reason"
                }
            }
            append "\n--- End of Report ---"

            return it.toString() // .with zwraca to, co jest w ostatniej linii
        }
        return report
    }
}