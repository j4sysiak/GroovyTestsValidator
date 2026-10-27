package com.example.satellite.service

import com.example.satellite.model.Command
import com.example.satellite.model.Severity
import com.example.satellite.model.ValidationIssue
import spock.lang.Specification
import spock.lang.Unroll

class CommandIssuerTest extends Specification {

    def commandIssuer = new CommandIssuer()

    def "should issue CONTINUE_NORMAL_OPERATION when there are no issues"() {
        given: "an empty list of issues"
        def issues = []

        when: "a command is decided"
        def decision = commandIssuer.decideCommand("SAT-01", issues)

        then: "the command is to continue normal operation"
        decision.command == Command.CONTINUE_NORMAL_OPERATION
    }

    @Unroll
    def "should issue #expectedCommand for critical errors containing '#reason'"() {
        given: "a list of issues with a critical error"
        def issues = [new ValidationIssue(reason: reason, severity: Severity.ERROR)]

        when: "a command is decided"
        def decision = commandIssuer.decideCommand("SAT-01", issues)

        then: "the correct critical command is issued"
        decision.command == expectedCommand

        where:
        reason                               | expectedCommand
        "Altitude is out of range"           | Command.ADJUST_ORBIT
        "Temperature is too high"            | Command.REBOOT_SYSTEM
        "Satellite is not in ONLINE status"  | Command.REBOOT_SYSTEM
    }

    def "should issue IGNORE_WARNINGS when there are only warnings"() {
        given: "a list containing only warnings"
        def issues = [
                new ValidationIssue(reason: "Signal too low", severity: Severity.WARNING),
                new ValidationIssue(reason: "Data is outdated", severity: Severity.WARNING)
        ]

        when: "a command is decided"
        def decision = commandIssuer.decideCommand("SAT-01", issues)

        then: "the command is to ignore warnings"
        decision.command == Command.IGNORE_WARNINGS
    }

    def "should prioritize critical errors over warnings"() {
        given: "a list with both warnings and a critical error"
        def issues = [
                new ValidationIssue(reason: "Signal too low", severity: Severity.WARNING),
                new ValidationIssue(reason: "Altitude is too low", severity: Severity.ERROR)
        ]

        when: "a command is decided"
        def decision = commandIssuer.decideCommand("SAT-01", issues)

        then: "the command is based on the critical error"
        decision.command == Command.ADJUST_ORBIT
    }
}