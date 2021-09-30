package it.uninsubria.dmx_calculator.models

import android.util.Log
import it.uninsubria.dmx_calculator.enumerator.FixtureTypeEnum
import it.uninsubria.dmx_calculator.enumerator.LampTypeEnum
import java.io.Serializable
import java.lang.NumberFormatException
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.tan

/**
 * @author Luca Centore
 *
 * @property name
 * @property manufacturer
 * @property mode
 * @property projectorType
 * @property lampType
 * @property minZoom
 * @property maxZoom
 */
data class Fixture(var name: String,
                   var manufacturer: String,
                   var mode: HashMap<String, Int>,
                   var powerConsumption: Int,
                   var projectorType: ArrayList<FixtureTypeEnum>,
                   var lampType: LampTypeEnum,
                   var minZoom: Float,
                   var maxZoom: Float): Serializable {

    private var firestoreReference: String = ""
    /**
     * @param fireRef riferiemnto della fixture al database Firestore
     */
    fun setReference(fireRef: String) {
        this.firestoreReference = fireRef
    }

    /**
     * @return riferiemnto della fixture al database Firestore
     */
    fun getReference() : String {
        return this.firestoreReference
    }

    fun addMode(key: String, value: Int) {
        mode[key] = value
    }

    /**
     * @param zoom in gradi, deve essere compreso tra minZoom e maxZoom
     * @param distance in metri
     * @return diameter in metri
     * @Suppress("unused")
     */
    @Suppress("unused")
    fun getRadius(zoom: Float, distance: Float) : Float {
        var diameter: Float = -1.0F
        if(zoom in minZoom..maxZoom) {
            diameter = (distance*tan(zoom/2*PI/180)).toFloat()
        }
        return (diameter * 100F).roundToInt() /100F
    }
}