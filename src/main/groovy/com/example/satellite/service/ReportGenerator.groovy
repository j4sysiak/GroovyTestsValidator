package com.example.satellite.service

import com.example.satellite.model.ValidationResult

class ReportGenerator {

    String generate(List<ValidationResult> validationResults, int totalPackets) {
        new StringBuilder().with {
            // Używamy naszych nowych, pomocniczych właściwości
            def acceptedCount = validationResults.count { it.valid }
            def rejectedCount = validationResults.count { !it.valid }

            append("--- Validation Report ---\n")
            append("Total packets processed: $totalPackets\n")
            append("Accepted (no errors): $acceptedCount\n")
            append("Rejected (with errors): $rejectedCount\n")

            // Osobno zbieramy błędy i ostrzeżenia
            def allErrors = validationResults.collectMany { it.errors }.unique { it.reason }
            def allWarnings = validationResults.collectMany { it.warnings }.unique { it.reason }

            if (allErrors) {
                append("\nCritical Errors Found:\n")
                allErrors.each { issue ->
                    append(" - ${issue.reason}\n")
                }
            }

            if (allWarnings) {
                append("\nWarnings Found:\n")
                allWarnings.each { issue ->
                    append(" - ${issue.reason}\n")
                }
            }

            append("\n--- End of Report ---")

            return it.toString()
        }
    }
}