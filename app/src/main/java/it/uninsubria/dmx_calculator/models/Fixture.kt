package it.uninsubria.dmx_calculator.models

import it.uninsubria.dmx_calculator.enumerator.FixtureTypeEnum
import it.uninsubria.dmx_calculator.enumerator.LampTypeEnum

data class Fixture (var name: String,
                    var manufacturer: String,
                    var mode: String,
                    var footprint: Int,
                    var powerConsumption: Int,
                    var projectorType: List<FixtureTypeEnum>,
                    var lampType: LampTypeEnum) {

    private var firestoreReference: String = ""
    private var id: Int = -1
    private var startAddress: Int = -1

    fun setReference(firestoreReference: String) {
        this.firestoreReference = firestoreReference
    }
    fun getReference() : String {
        return this.firestoreReference
    }

    fun setID(id: Int) {
        this.id = id
    }
    fun getID() : Int {
        return this.id
    }

    fun setStartAddress(startAddress: Int) {
        this.startAddress = startAddress
    }
    fun getStartAddress() : Int {
        return this.startAddress
    }
}