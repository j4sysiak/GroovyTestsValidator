package com.example.auto.model

import groovy.transform.ToString

@ToString
class ActionDecision {
    String autoId
    Command command
    String justification // Uzasadnienie, dlaczego podjęto taką decyzję
}