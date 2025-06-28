package com.example.satellite.model

import groovy.transform.ToString

@ToString
class ActionDecision {
    String satelliteId
    Command command
    String justification // Uzasadnienie, dlaczego podjęto taką decyzję
}