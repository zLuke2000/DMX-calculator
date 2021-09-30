package it.uninsubria.dmx_calculator.models

import java.io.Serializable

data class Project(var name: String, var location: String, var availablePower: Int, var fixtureList: MutableList<FixturePatch>): Serializable {
/*
    @Suppress("unused")
    fun getTotalPowerConsumption(): Int {
        var currentPower = 0
        for(fixture in fixtureList) {
            currentPower += fixture.powerConsumption
        }
        return currentPower
    }

    @Suppress("unused")
    fun getTotalDMXChannel(): Int {
        var currentChannel = 0
        //TODO Calcolo in base alla modalità utilizzata
        return currentChannel
    }

    @Suppress("unused")
    fun getTotalFixtureNumber(): Int {
        return fixtureList.size
    }
 */
}