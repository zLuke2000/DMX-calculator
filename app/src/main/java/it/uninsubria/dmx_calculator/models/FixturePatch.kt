package it.uninsubria.dmx_calculator.models

import java.util.HashMap

data class FixturePatch(var id: Int,
                        var name: String,
                        var fixtureRef: String,
                        var mode: HashMap<String, Int>,
                        var universe: Int,
                        var startAddress: Int,
                        var panInversion: Boolean,
                        var tiltInversion: Boolean,
                        var colorGel: String,
                        var boReact: Boolean)