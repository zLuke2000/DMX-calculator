package it.uninsubria.dmx_calculator.models

/**
 * channel<numeroCanale, idFixtureAssegnata>
 */
data class Universe(var number: Int, var channel: MutableMap<Int, Int> = mutableMapOf())