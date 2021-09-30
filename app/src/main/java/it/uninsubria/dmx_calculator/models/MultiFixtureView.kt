package it.uninsubria.dmx_calculator.models

import android.view.View
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.ToggleButton
import it.uninsubria.dmx_calculator.R
import java.util.HashMap

class MultiFixtureView(var view: View,
                       var fixtureReference: String = "",
                       var patchUniverse: Int = 0,
                       var patchChannel: Int = 0,
                       var mode: MutableMap<String, Int> = hashMapOf(),
                       var panInversion: Boolean = false,
                       var tiltInversion: Boolean = false,
                       var colorGel: String = "#FFFFFF",
                       var boReact: Boolean = true) {



    fun getIDTV(): TextView {
        return view.findViewById(R.id.TV_nprFixID)
    }
    fun getFixtureDataTV(): TextView {
        return view.findViewById(R.id.TV_nprFixData)
    }
    fun getNameTV(): TextView {
        return view.findViewById(R.id.TV_nprFixName)
    }
    fun getPatchTV(): TextView {
        return view.findViewById(R.id.TV_nprFixDMX)
    }
    fun getPanInvIB(): ImageButton {
        return view.findViewById(R.id.IB_nprFixPanInversion)
    }
    fun getTiltInvIB(): ImageButton {
        return view.findViewById(R.id.IB_nprFixTiltInversion)
    }
    fun getGelColorTV(): TextView {
        return view.findViewById(R.id.TV_nprFixColorGel)
    }
    fun getBOReactIB(): ImageButton {
        return view.findViewById(R.id.IB_nprBOReact)
    }
    fun getRemoveIB(): ImageButton {
        return view.findViewById(R.id.IB_removeFixture)
    }
}