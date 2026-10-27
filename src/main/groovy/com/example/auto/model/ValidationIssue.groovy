package com.example.auto.model

import groovy.transform.ToString

@ToString
class ValidationIssue {
    String reason
    Severity severity
}