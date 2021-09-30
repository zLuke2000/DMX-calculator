package it.uninsubria.dmx_calculator.models

data class FixturePatchTemp(var id: Int,
                            var name: String,
                            var manufacturer: String,
                            var fixName: String,
                            var mode: String,
                            var channelCount: Int,
                            var universe: Int,
                            var startAddress: Int,
                            var reference: String)