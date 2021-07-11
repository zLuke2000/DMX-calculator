package it.uninsubria.dmx_calculator.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import it.uninsubria.dmx_calculator.R
import it.uninsubria.dmx_calculator.databinding.FragmentNewFixtureBinding
import it.uninsubria.dmx_calculator.enumerator.FixtureTypeEnum
import it.uninsubria.dmx_calculator.enumerator.LampTypeEnum
import it.uninsubria.dmx_calculator.firebase.Firestore
import it.uninsubria.dmx_calculator.models.Fixture
import java.lang.NumberFormatException

class NewFixtureFragment : Fragment() {
    private val TAG = "New Fixture Fragment"

    private var _binding: FragmentNewFixtureBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val myDB: Firestore = Firestore()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentNewFixtureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.BSaveFixture.setOnClickListener{checkParam()}
    }

    private fun checkParam() {
        val name: String = binding.ETNewFixtureName.text.toString().trim()
        val manufacturer: String = binding.ETNewFixtureManufacturer.text.toString().trim()
        val mode: String = binding.ETNewFixtureMode.text.toString().trim()
        val footprint: String = binding.ETNewFixtureFootprint.text.toString().trim()
        val powerConsumption: String = binding.ETNewFixturePowerConsumption.text.toString().trim()
        val myFixture = Fixture("", "", "", 0, 0, arrayListOf(), LampTypeEnum.UNKNOWN)

        if( checkBasicText(binding.TILNewFixtureName, name, 2) and checkBasicText(binding.TILNewFixtureManufacturer, manufacturer, 4) and checkBasicText(binding.TILNewFixtureMode, mode, 2) and checkBasicInt(binding.TILNewFixtureFootprint, footprint, 512) and checkBasicInt(binding.TILNewFixturePowerConsumption, powerConsumption, null)) {
            myFixture.name = name
            myFixture.manufacturer = manufacturer
            myFixture.mode = mode
            myFixture.footprint = footprint.toInt()
            myFixture.powerConsumption = powerConsumption.toInt()

            setFixtureType(myFixture)
            setLampType(myFixture)

            myDB.addFixture(myFixture) { result ->
                if(result) {
                    binding.ETNewFixtureName.text = null
                    binding.ETNewFixtureManufacturer.text = null
                    binding.ETNewFixtureMode.text = null
                    binding.ETNewFixtureFootprint.text = null
                    binding.ETNewFixturePowerConsumption.text = null
                    binding.CBSpot.isChecked = false
                    binding.CBWash.isChecked = false
                    binding.CBBeam.isChecked = false
                    binding.CBProfile.isChecked = false
                    binding.RBDischarge.isChecked = false
                    binding.RBLed.isChecked = false
                    binding.RBIncandescent.isChecked = false
                    binding.RBHalogen.isChecked = false
                    binding.RBFluorescent.isChecked = false
                    Toast.makeText(context, getString(R.string.fixtureSaveOK), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, getString(R.string.fixtureSaveKO), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkBasicText(til: TextInputLayout, tilText: String,  minChar: Int): Boolean {
        return if(tilText.length < minChar) {
            til.error = getString(R.string.minChar).replace("$", "" + minChar)
            false
        } else {
            til.error = null
            true
        }
    }

    private fun checkBasicInt(til: TextInputLayout, num: String, dmxLimit: Int?): Boolean {
        try {
            val intNumber = num.toInt()
            if(dmxLimit != null) {
                if(intNumber > dmxLimit) {
                    til.error = getString(R.string.numberTooHigh)
                    return false
                }
            }
            til.error = null
            return true
        } catch (e: NumberFormatException) {
            til.error = getString(R.string.onlyNumber)
            return false
        }
    }

    private fun setFixtureType(myFixture: Fixture) {
        var isOneSelected = false
        if(binding.CBSpot.isChecked) {
            myFixture.projectorType.add(FixtureTypeEnum.SPOT)
            isOneSelected = true
        }
        if(binding.CBWash.isChecked) {
            myFixture.projectorType.add(FixtureTypeEnum.WASH)
            isOneSelected = true
        }
        if(binding.CBBeam.isChecked) {
            myFixture.projectorType.add(FixtureTypeEnum.BEAM)
            isOneSelected = true
        }
        if(binding.CBProfile.isChecked) {
            myFixture.projectorType.add(FixtureTypeEnum.PROFILE)
            isOneSelected = true
        }
        if(!isOneSelected) {
            myFixture.projectorType.add(FixtureTypeEnum.UNKNOWN)
        }
    }

    private fun setLampType(myFixture: Fixture) {
        when {
            binding.RBDischarge.isChecked ->    myFixture.lampType = LampTypeEnum.DISCHARGE
            binding.RBLed.isChecked ->          myFixture.lampType = LampTypeEnum.LED
            binding.RBIncandescent.isChecked -> myFixture.lampType = LampTypeEnum.INCANDESCENT
            binding.RBHalogen.isChecked ->      myFixture.lampType = LampTypeEnum.HALOGEN
            binding.RBFluorescent.isChecked ->  myFixture.lampType = LampTypeEnum.FLUORESCENT
            else -> myFixture.lampType = LampTypeEnum.UNKNOWN
        }
    }
}