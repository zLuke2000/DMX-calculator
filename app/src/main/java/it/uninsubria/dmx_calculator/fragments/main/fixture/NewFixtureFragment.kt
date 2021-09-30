package it.uninsubria.dmx_calculator.fragments.main.fixture

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import it.uninsubria.dmx_calculator.R
import it.uninsubria.dmx_calculator.databinding.FragmentNewFixtureBinding
import it.uninsubria.dmx_calculator.enumerator.FixtureTypeEnum
import it.uninsubria.dmx_calculator.enumerator.LampTypeEnum
import it.uninsubria.dmx_calculator.firebase.Firestore
import it.uninsubria.dmx_calculator.models.Fixture
import it.uninsubria.dmx_calculator.models.MultiModeView

@Suppress("unused")
private const val TAG = "New_Fixture_Fragment"
private const val DMX_LIMIT = 512

class NewFixtureFragment : Fragment() {

    private var _binding: FragmentNewFixtureBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val myDB: Firestore = Firestore()
    private lateinit var myFixture: Fixture
    private var modeNumber = 1
    private val modeView: HashMap<Int,MultiModeView> = hashMapOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentNewFixtureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Imposto i listener
        binding.ETNfName.doOnTextChanged{ _,_,_,_ -> binding.TILNfName.error = null }
        binding.ETNfManufacturer.doOnTextChanged{ _,_,_,_ -> binding.TILNfManufacturer.error = null }
        binding.ETNfMode.doOnTextChanged{ _,_,_,_ -> binding.TILNfMode.error = null }
        binding.ETNfFootprint.doOnTextChanged{ _,_,_,_ -> binding.TILNfFootprint.error = null }
        binding.ETNfPowerConsumption.doOnTextChanged{ _,_,_,_ -> binding.TILNfPowerConsumption.error = null }
        binding.ETMinZoom.doOnTextChanged{ _,_,_,_ -> binding.TILMinZoom.error = null }
        binding.ETMaxZoom.doOnTextChanged{ _,_,_,_ -> binding.TILMaxZoom.error = null }

        binding.BNfValidateParam.setOnClickListener { validate() }
        binding.BNfSaveFixture.setOnClickListener { save() }
        binding.TILNfManufacturer.setStartIconOnClickListener{ showManufacturer() }
        binding.IBNewMode.setOnClickListener{ addMode() }
    }

    private fun validate(): Boolean {
        val name: String = binding.ETNfName.text.toString().trim()
        val manufacturer: String = binding.ETNfManufacturer.text.toString().trim()
        val mode: String = binding.ETNfMode.text.toString().trim()
        val footprint: String = binding.ETNfFootprint.text.toString().trim()
        val powerConsumption: String = binding.ETNfPowerConsumption.text.toString().trim()
        val minZoom: String = binding.ETMinZoom.text.toString().trim()
        val maxZoom: String = binding.ETMaxZoom.text.toString().trim()

        return if (checkBasicText(binding.TILNfName, name,2) and checkBasicText(binding.TILNfManufacturer, manufacturer,4) and checkBasicText(binding.TILNfMode, mode, 2) and checkDMX(binding.TILNfFootprint, footprint) and checkPowerConsumption(binding.TILNfPowerConsumption, powerConsumption) and checkZoom(binding.TILMinZoom, minZoom, binding.TILMaxZoom, maxZoom)) {
            myFixture = Fixture(name, manufacturer, hashMapOf(), powerConsumption.toInt(), arrayListOf(), LampTypeEnum.UNKNOWN, minZoom.toFloat(), maxZoom.toFloat())
            setFixtureType(myFixture)
            setLampType(myFixture)
            myFixture.mode.clear()
            myFixture.addMode(mode, footprint.toInt())
            true
        } else {
            false
        }
    }

    private fun validateExtraMode(): Boolean {
        var status = true
        for((_, layout) in modeView) {
            if(checkBasicText(layout.getModeTIL(), layout.getModeET().text.toString().trim(), 2) and checkDMX(layout.getFootprintTIL(), layout.getFootprintET().text.toString().trim())) {
                if(!myFixture.mode.containsKey(layout.getModeET().text.toString().trim())) {
                    myFixture.addMode(layout.getModeET().text.toString().trim(), layout.getFootprintET().text.toString().trim().toInt())
                } else {
                    layout.getModeTIL().error = getString(R.string.duplicate_mode)
                    myFixture.mode.clear()
                    status = false
                }
            } else {
                status = false
            }
        }
        return status
    }

    private fun save() {
        if(validate() and validateExtraMode()) {
            myDB.addFixture(myFixture) { result ->
                if (result) {
                    // RESET parametri generici
                    binding.ETNfName.text = null
                    binding.ETNfManufacturer.text = null
                    binding.ETNfMode.text = null
                    binding.ETNfFootprint.text = null
                    binding.ETNfPowerConsumption.text = null
                    // RESET tipo fixture
                    binding.CBSpot.isChecked = false
                    binding.CBWash.isChecked = false
                    binding.CBBeam.isChecked = false
                    binding.CBProfile.isChecked = false
                    // RESET tipo lampada
                    binding.RBDischarge.isChecked = false
                    binding.RBLed.isChecked = false
                    binding.RBLaser.isChecked = false
                    binding.RBIncandescent.isChecked = false
                    binding.RBHalogen.isChecked = false
                    binding.RBFluorescent.isChecked = false
                    // RESET zoom
                    binding.ETMinZoom.text = null
                    binding.ETMaxZoom.text = null
                    // RESET extra mode View
                    for((_, v) in modeView) {
                        binding.LLNfInfo0.removeView(v.view)
                    }
                    modeView.clear()
                    Toast.makeText(context, getString(R.string.nfSaveOK), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, getString(R.string.nfSaveKO), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkBasicText(til: TextInputLayout, tilText: String, minChar: Int): Boolean {
        return if (tilText.length < minChar) {
            til.error = getString(R.string.minChar).replace("$", "" + minChar)
            false
        } else {
            true
        }
    }

    private fun checkPowerConsumption(til: TextInputLayout, powerCons: String): Boolean {
        if(powerCons.isNotEmpty()) {
            try {
                val num = powerCons.toInt()
                if (num < 0) {
                    til.error = getString(R.string.onlyPositiveNumber)
                    return false
                }
                return true
            } catch (e: NumberFormatException) {
                til.error = getString(R.string.onlyNumber)
                return false
            }
        } else {
            til.error = getString(R.string.notBlank)
            return false
        }
    }

    private fun checkDMX(til: TextInputLayout, dmxNumber: String): Boolean {
        if(dmxNumber.isNotEmpty()) {
            try {
                val num = dmxNumber.toInt()
                if (num > DMX_LIMIT) {
                    til.error = getString(R.string.numberTooHigh).replace("$", "$DMX_LIMIT")
                    return false
                }
                return true
            } catch (e: NumberFormatException) {
                til.error = getString(R.string.onlyNumber)
                return false
            }
        }else {
            til.error = getString(R.string.notBlank)
            return false
        }
    }

    private fun checkZoom(minZoomTil: TextInputLayout, minZoom: String, maxZoomTil: TextInputLayout, maxZoom: String): Boolean {
        var minZoomInt: Float? = null
        var maxZoomInt: Float? = null
        var status = true

        if(minZoom.isNotEmpty()) {
            try {
                minZoomInt = minZoom.toFloat()
            } catch (e: NumberFormatException) {
                minZoomTil.error = getString(R.string.onlyNumber)
                status = false
            }
        }else {
            minZoomTil.error = getString(R.string.notBlank)
            status = false
        }

        if(maxZoom.isNotEmpty()) {
            try {
                maxZoomInt = maxZoom.toFloat()
            } catch (e: NumberFormatException) {
                maxZoomTil.error = getString(R.string.onlyNumber)
                status = false
            }
        }else {
            maxZoomTil.error = getString(R.string.notBlank)
            status = false
        }

        if(minZoomInt != null && maxZoomInt != null) {
            // Controllo se minZoom > 0
            if(minZoomInt <= 0F) {
                minZoomTil.error = getString(R.string.onlyPositiveNumber)
                status = false
            }

            // Controllo se maxZoom > 0
            if(maxZoomInt <= 0F) {
                maxZoomTil.error = getString(R.string.onlyPositiveNumber)
                status = false
            }

            // Controllo se minZoom < maxZoom
            if(minZoomInt > maxZoomInt) {
                minZoomTil.error = getString(R.string.mustBe) + " < $maxZoomInt"
                maxZoomTil.error = getString(R.string.mustBe) + " > $minZoomInt"
                status = false
            }
        } else {
            status = false
        }

        return status
    }

    private fun setFixtureType(myFixture: Fixture) {
        var isOneSelected = false
        if (binding.CBSpot.isChecked) {
            myFixture.projectorType.add(FixtureTypeEnum.SPOT)
            isOneSelected = true
        }
        if (binding.CBWash.isChecked) {
            myFixture.projectorType.add(FixtureTypeEnum.WASH)
            isOneSelected = true
        }
        if (binding.CBBeam.isChecked) {
            myFixture.projectorType.add(FixtureTypeEnum.BEAM)
            isOneSelected = true
        }
        if (binding.CBProfile.isChecked) {
            myFixture.projectorType.add(FixtureTypeEnum.PROFILE)
            isOneSelected = true
        }
        if (!isOneSelected) {
            myFixture.projectorType.add(FixtureTypeEnum.UNKNOWN)
        }
    }

    private fun setLampType(myFixture: Fixture) {
        when {
            binding.RBDischarge.isChecked ->    myFixture.lampType = LampTypeEnum.DISCHARGE
            binding.RBLed.isChecked ->          myFixture.lampType = LampTypeEnum.LED
            binding.RBLaser.isChecked ->        myFixture.lampType = LampTypeEnum.LASER
            binding.RBIncandescent.isChecked -> myFixture.lampType = LampTypeEnum.INCANDESCENT
            binding.RBHalogen.isChecked ->      myFixture.lampType = LampTypeEnum.HALOGEN
            binding.RBFluorescent.isChecked ->  myFixture.lampType = LampTypeEnum.FLUORESCENT
            else ->                             myFixture.lampType = LampTypeEnum.UNKNOWN
        }
    }

    private fun showManufacturer() {
        myDB.getAllManufacturer{ result ->
            if(result.isNotEmpty()) {
                val manufacturerArray = arrayOfNulls<String>(result.size)
                for(i in result.indices) {
                    manufacturerArray[i] = result[i]
                }
                MaterialAlertDialogBuilder(context)
                    .setTitle(resources.getString(R.string.nfManufacturer))
                    .setItems(manufacturerArray) { _, index ->
                        binding.ETNfManufacturer.setText(manufacturerArray[index])
                    }.show()
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun addMode() {
        modeNumber++
        val factory = LayoutInflater.from(context)
        val currentView: View = factory.inflate(R.layout.mode_template, null)
        val layout = MultiModeView(currentView)
        modeView[modeNumber] = layout

        layout.getModeET().doOnTextChanged{ _,_,_,_ -> layout.getModeTIL().error = null }
        layout.getFootprintET().doOnTextChanged{ _,_,_,_ -> layout.getFootprintTIL().error = null }
        layout.getImage().setOnClickListener { binding.LLNfInfo0.removeView(currentView)
                                               modeView.remove(modeNumber) }

        binding.LLNfInfo0.addView(currentView)
    }
}