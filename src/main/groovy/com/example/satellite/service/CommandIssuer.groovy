package com.example.satellite.service

import com.example.satellite.model.ActionDecision
import com.example.satellite.model.Command
import com.example.satellite.model.ValidationIssue
import com.example.satellite.model.Severity

class CommandIssuer {

    ActionDecision decideCommand(String satelliteId, List<ValidationIssue> issues) {
        if (!issues) { // Jeśli lista problemów jest pusta
            return new ActionDecision(
                    satelliteId: satelliteId,
                    command: Command.CONTINUE_NORMAL_OPERATION,
                    justification: "All systems nominal."
            )
        }

        // Sprawdzamy, czy jest JAKIKOLWIEK błąd krytyczny
        boolean hasCriticalError = issues.any { it.severity == Severity.ERROR }

        if (hasCriticalError) {
            // Logika dla błędów krytycznych
            // Jeśli problem dotyczy orbity, dostosuj orbitę.
            if (issues.any { it.reason.contains("Altitude") }) {
                return new ActionDecision(
                        satelliteId: satelliteId,
                        command: Command.ADJUST_ORBIT,
                        justification: "Critical altitude deviation detected."
                )
            } else {
                // Dla wszystkich innych błędów krytycznych, zrób reboot.
                return new ActionDecision(
                        satelliteId: satelliteId,
                        command: Command.REBOOT_SYSTEM,
                        justification: "Critical system error detected. Rebooting."
                )
            }
        } else {
            // Jeśli nie ma błędów krytycznych, to znaczy, że są tylko ostrzeżenia.
            return new ActionDecision(
                    satelliteId: satelliteId,
                    command: Command.IGNORE_WARNINGS,
                    justification: "Minor warnings detected. Continuing operation."
            )
        }
    }
}