package com.example.satellite.model

import groovy.transform.ToString

//Obiekt przechowuj¹cy wynik walidacji.
@ToString
class ValidationResult {
    boolean valid = true
    List<String> failureReasons = []

    void addFailure(String reason) {
        this.valid = false
        this.failureReasons.add(reason)
    }
}