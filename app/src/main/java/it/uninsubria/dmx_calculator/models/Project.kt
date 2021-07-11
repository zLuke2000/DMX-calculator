package it.uninsubria.dmx_calculator.models

data class Project(var name: String, var fixtureList: List<Fixture>) {

    fun getTotalPowerConsumption(): Int {
        var currentPower = 0
        for(fixture in fixtureList) {
            currentPower += fixture.powerConsumption
        }
        return currentPower
    }

    fun getTotalDMXChannel(): Int {
        var currentChannel = 0
        for(fixture in fixtureList) {
            currentChannel += fixture.footprint
        }
        return currentChannel
    }

    fun getTotalFixtureNumber(): Int {
        return fixtureList.size
    }
}