package com.example.satellite.model

import groovy.transform.ToString

@ToString
class ValidationIssue {
    String reason
    Severity severity
}