package it.uninsubria.dmx_calculator.models

import android.view.View
import android.widget.ImageButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import it.uninsubria.dmx_calculator.R

class MultiModeView(var view: View) {

    fun getModeTIL(): TextInputLayout {
        return view.findViewById(R.id.TIL_newFixtureMode)

    }
    fun getModeET(): TextInputEditText {
        return view.findViewById(R.id.ET_fdpManufacturer)

    }
    fun getFootprintTIL(): TextInputLayout {
        return view.findViewById(R.id.TIL_newFixtureFootprint)

    }
    fun getFootprintET(): TextInputEditText {
        return view.findViewById(R.id.ET_newFixtureFootprint)

    }
    fun getImage(): ImageButton {
        return view.findViewById(R.id.IB_removeMode)
    }
}