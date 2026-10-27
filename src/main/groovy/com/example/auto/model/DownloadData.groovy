package com.example.auto.model

import groovy.transform.ToString

@ToString
class DownloadData {
    String autoId
    long timestamp
    double altitudeKm
    double temperatureCelsius
    double signalStrengthDBm
    AutoStatus status
}