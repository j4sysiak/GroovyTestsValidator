package com.example.auto.service

import com.example.auto.model.ActionDecision
import com.example.auto.model.Command
import com.example.auto.model.ValidationIssue
import com.example.auto.model.Severity

class CommandIssuer {

    ActionDecision decideCommand(String autoId, List<ValidationIssue> issues) {
        if (!issues) { // Jeśli lista problemów jest pusta
            return new ActionDecision(
                    autoId: autoId,
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
                        autoId: autoId,
                        command: Command.ADJUST_ORBIT,
                        justification: "Critical altitude deviation detected."
                )
            } else {
                // Dla wszystkich innych błędów krytycznych, zrób reboot.
                return new ActionDecision(
                        autoId: autoId,
                        command: Command.REBOOT_SYSTEM,
                        justification: "Critical system error detected. Rebooting."
                )
            }
        } else {
            // Jeśli nie ma błędów krytycznych, to znaczy, że są tylko ostrzeżenia.
            return new ActionDecision(
                    autoId: autoId,
                    command: Command.IGNORE_WARNINGS,
                    justification: "Minor warnings detected. Continuing operation."
            )
        }
    }
}