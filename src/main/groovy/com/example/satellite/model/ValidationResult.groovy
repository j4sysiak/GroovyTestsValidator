package com.example.satellite.model

import groovy.transform.ToString

@ToString
class ValidationResult {
    // Zamiast listy stringów, mamy listę obiektów ValidationIssue
    List<ValidationIssue> issues = []

    // Metoda pomocnicza do dodawania problemu
    void addIssue(String reason, Severity severity) {
        issues.add(new ValidationIssue(reason: reason, severity: severity))
    }

    // Nowa, inteligentna właściwość. Zwraca 'true' tylko jeśli nie ma ŻADNYCH błędów typu ERROR.
    boolean isValid() {
        // .every{} sprawdza, czy WSZYSTKIE elementy na liście spełniają warunek.
        // Jeśli lista błędów jest pusta, .every{} zwraca true.
        return issues.every { it.severity != Severity.ERROR }
    }

    // Właściwość pomocnicza do pobierania tylko błędów krytycznych
    List<ValidationIssue> getErrors() {
        issues.findAll { it.severity == Severity.ERROR }
    }

    // Właściwość pomocnicza do pobierania tylko ostrzeżeń
    List<ValidationIssue> getWarnings() {
        issues.findAll { it.severity == Severity.WARNING }
    }
}