package it.uninsubria.dmx_calculator.fragments.main.project

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputLayout
import it.uninsubria.dmx_calculator.R
import it.uninsubria.dmx_calculator.databinding.FixturePatchDialogBinding
import it.uninsubria.dmx_calculator.databinding.FragmentNewProjectBinding
import it.uninsubria.dmx_calculator.firebase.Firestore
import java.lang.NumberFormatException
import android.widget.AdapterView.OnItemSelectedListener
import androidx.core.content.ContextCompat
import it.uninsubria.dmx_calculator.databinding.ChangeIdDialogBinding
import it.uninsubria.dmx_calculator.databinding.ChangeNameDialogBinding
import it.uninsubria.dmx_calculator.models.*


@Suppress("unused")
private const val TAG = "New_Project_Fragment"
private const val UNIVERSE_LIMIT = 32768
private const val DMX_LIMIT = 512

class NewProjectFragment : Fragment() {

    private var _binding: FragmentNewProjectBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    // tabella
    private var fixtureAbsNumber = 0
    private val listFixtureView: HashMap<Int,MultiFixtureView> = hashMapOf()
    private val usedID: MutableList<Int> = mutableListOf()
    private val patch: MutableMap<Int, Universe> = mutableMapOf()

    // Firestore
    private val db: Firestore = Firestore()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentNewProjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Imposto i listener
        binding.ETNpName.doOnTextChanged{ _,_,_,_ -> binding.TILNpName.error = null }
        binding.ETNplocation.doOnTextChanged{ _,_,_,_ -> binding.TILNpLocation.error = null }

        binding.IVNpAddFromGallery.setOnClickListener { getImageFromGallery() }
        binding.IVNpAddFromcamera.setOnClickListener { getImageFromCamera() }
        binding.BNpValidateParam.setOnClickListener { validate() }
        binding.BNpSaveProject.setOnClickListener { save() }
        binding.IBNpNewMode.setOnClickListener{ openDialog() }
    }

    private fun getImageFromGallery() {}

    private fun getImageFromCamera() {}

    private fun validate(): Boolean {
        return(checkBasicText(binding.TILNpName, binding.ETNpName.text.toString().trim(), 4) and checkBasicText(binding.TILNpLocation, binding.ETNplocation.text.toString().trim(), 2) and checkNumber(binding.TILNpMaximunPower, binding.ETNpMaximunPower.text.toString().trim(), null))
    }

    private fun save() {
        if(validate()) {
            if(fixtureAbsNumber > 0) {
                val project = Project(binding.ETNpName.text.toString().trim(), binding.ETNplocation.text.toString().trim(), binding.ETNpMaximunPower.text.toString().trim().toInt(), arrayListOf())
                for((_,patch) in listFixtureView) {
                    project.fixtureList.add(FixturePatch(patch.getIDTV().text.toString().toInt(),
                                                         patch.getNameTV().text.toString(),
                                                         patch.fixtureReference,
                                                         hashMapOf(),
                                                         patch.patchUniverse,
                                                         patch.patchChannel,
                                                         patch.panInversion,
                                                         patch.tiltInversion,
                                                         patch.colorGel,
                                                         patch.boReact))
                }
                Log.i(TAG, project.toString())
            } else {
                Toast.makeText(context, "INSERIRE ALMENO UNA FIXTURE", Toast.LENGTH_SHORT).show()
            }
        }

    }

    /**
     * SEZIONE DIALOGO PATCH FIXTURE
     */
    @SuppressLint("InflateParams")
    private fun openDialog() {

        val patchDialog = context?.let { Dialog(it) }
        if (patchDialog != null) {

            val dialogBinding = FixturePatchDialogBinding.inflate(layoutInflater)
            patchDialog.setContentView(dialogBinding.root)

            // Assegnazione listener ai bottoni di selezione costruttore, fixture e modalita
            populateSpinner(dialogBinding.SFdpManufacturer, 0, "")
            dialogBinding.SFdpManufacturer.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long ) {
                    populateSpinner(dialogBinding.SFpdFixtureType, 1, dialogBinding.SFdpManufacturer.selectedItem as String)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {  }
            }
            dialogBinding.SFpdFixtureType.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long ) {
                    populateSpinner(dialogBinding.SFpdMode, 2, dialogBinding.SFpdFixtureType.selectedItem as String)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {  }
            }

            // Assegnazione listener al bottone salva
            dialogBinding.BFpdSavePatch.setOnClickListener {
                val tempPatchList = validateDialogParameter(dialogBinding)
                if(tempPatchList.size != 0) {
                    for(i in tempPatchList) {
                        fixtureAbsNumber++
                        val currentView: View = LayoutInflater.from(context).inflate(R.layout.row_fixture_project, null)
                        val layout = MultiFixtureView(currentView)
                        listFixtureView[fixtureAbsNumber] = layout
                        // assegno le fixture appena inserite nella riga
                        layout.getIDTV().text = i.id.toString()
                        layout.getNameTV().text = i.name
                        layout.getFixtureDataTV().text = ("${i.manufacturer} - ${i.fixName}")
                        layout.getPatchTV().text = ("${i.universe}.${i.startAddress}")
                        layout.getIDTV().text = i.id.toString()
                        layout.mode[i.mode] = i.channelCount
                        layout.fixtureReference = i.reference
                        Log.e(TAG, "openDialog: ${i.reference}")
                        binding.TLFixtureList.addView(currentView)

                        // aggiungo l'id corrente alla lista di id utilizzati
                        usedID.add(i.id)
                        // aggiungo i canali dmx utilizzati all'universo scelto
                        val tempPathcMap: MutableMap<Int, Int> = mutableMapOf()
                        // popolo la mappa dei canali utilizzati (temporanea)
                        for(j in 0..i.channelCount) {
                            tempPathcMap[i.startAddress+j] = i.id
                        }
                        // aggiungo la patch appena creata alla mappa generale degli universi
                        if(patch[i.universe] != null) {
                            patch[i.universe]!!.channel.putAll(tempPathcMap)
                        } else {
                            patch[i.universe] = Universe(i.universe, tempPathcMap)
                        }

                        // assegno i listener ai pulsanti
                        layout.getIDTV().setOnClickListener {
                            val idDialog = context?.let { Dialog(it) }
                            if(idDialog != null) {
                                val idDialogBinding = ChangeIdDialogBinding.inflate(layoutInflater)
                                idDialog.setContentView(idDialogBinding.root)
                                idDialogBinding.TVCidOldID.text = "${resources.getString(R.string.fdpOldID)} ${layout.getIDTV().text}"
                                idDialogBinding.BCidUpdate.setOnClickListener {
                                    if(checkNumber(idDialogBinding.TILCidNewID, idDialogBinding.ETCidNewID.text.toString().trim(), null)) {
                                        val newID = idDialogBinding.ETCidNewID.text.toString().trim().toInt()
                                        if(newID !in usedID) {
                                            usedID.remove(id)
                                            usedID.add(newID)
                                            layout.getIDTV().text = newID.toString()
                                            idDialog.hide()
                                        } else {
                                            Toast.makeText(context, "ID $newID GIA' UTILIZZATO", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                                idDialog.show()
                            }
                        }
                        layout.getNameTV().setOnClickListener {
                            val nameDialog = context?.let { Dialog(it) }
                            if(nameDialog != null) {
                                val nameDialogBinding = ChangeNameDialogBinding.inflate(layoutInflater)
                                nameDialog.setContentView(nameDialogBinding.root)
                                nameDialogBinding.TVCidOldName.text = "${resources.getString(R.string.fdpOldName)} ${layout.getNameTV().text}"
                                nameDialogBinding.BCidUpdate.setOnClickListener {
                                    if(checkBasicText(nameDialogBinding.TILCidNewName, nameDialogBinding.ETCidNewName.text.toString().trim(), 2)) {
                                        layout.getNameTV().text = nameDialogBinding.ETCidNewName.text.toString().trim()
                                        nameDialog.hide()
                                    }
                                }
                                nameDialog.show()
                            }
                        }
                        layout.getPanInvIB().setOnClickListener {
                                layout.panInversion = !layout.panInversion
                                if(layout.panInversion) {
                                    layout.getPanInvIB().setImageResource(R.drawable.ic_baseline_check_yes)
                                } else {
                                    layout.getPanInvIB().setImageResource(R.drawable.ic_baseline_check_no)
                                }
                            }
                        layout.getTiltInvIB().setOnClickListener {
                                layout.tiltInversion = !layout.tiltInversion
                                if(layout.tiltInversion) {
                                    layout.getTiltInvIB().setImageResource(R.drawable.ic_baseline_check_yes)
                                } else {
                                    layout.getTiltInvIB().setImageResource(R.drawable.ic_baseline_check_no)
                                }
                            }
                        layout.getGelColorTV().setOnClickListener {
                                val alertDialog: AlertDialog? = activity?.let { it1 ->
                                    val builder = AlertDialog.Builder(it1)
                                    builder.setTitle("Scegli il colore")
                                        .setItems(R.array.colorsName) { _, which ->
                                            layout.getGelColorTV().text = resources.getStringArray(R.array.colorsName)[which]
                                            layout.colorGel = resources.getStringArray(R.array.colorsCode)[which]
                                            when (which) {
                                                0 -> context?.let { layout.getGelColorTV().setTextColor(ContextCompat.getColor(it, R.color.gel_white)) }
                                                1 -> context?.let { layout.getGelColorTV().setTextColor(ContextCompat.getColor(it, R.color.gel_red)) }
                                                2 -> context?.let { layout.getGelColorTV().setTextColor(ContextCompat.getColor(it, R.color.gel_orange)) }
                                                3 -> context?.let { layout.getGelColorTV().setTextColor(ContextCompat.getColor(it, R.color.gel_yellow)) }
                                                4 -> context?.let { layout.getGelColorTV().setTextColor(ContextCompat.getColor(it, R.color.gel_fern_green)) }
                                                5 -> context?.let { layout.getGelColorTV().setTextColor(ContextCompat.getColor(it, R.color.gel_green)) }
                                                6 -> context?.let { layout.getGelColorTV().setTextColor(ContextCompat.getColor(it, R.color.gel_sea_green)) }
                                                7 -> context?.let { layout.getGelColorTV().setTextColor(ContextCompat.getColor(it, R.color.gel_cyan)) }
                                                8 -> context?.let { layout.getGelColorTV().setTextColor(ContextCompat.getColor(it, R.color.gel_lavender)) }
                                                9 -> context?.let { layout.getGelColorTV().setTextColor(ContextCompat.getColor(it, R.color.gel_blue)) }
                                                10 -> context?.let { layout.getGelColorTV().setTextColor(ContextCompat.getColor(it, R.color.gel_violet)) }
                                                11 -> context?.let { layout.getGelColorTV().setTextColor(ContextCompat.getColor(it, R.color.gel_magenta)) }
                                                12 -> context?.let { layout.getGelColorTV().setTextColor(ContextCompat.getColor(it, R.color.gel_pink)) }
                                            }
                                        }
                                    builder.create()
                                }
                                alertDialog!!.show()
                            }
                        layout.getBOReactIB().setOnClickListener {
                                layout.boReact = !layout.boReact
                                if(layout.boReact) {
                                    layout.getBOReactIB().setImageResource(R.drawable.ic_baseline_check_yes)
                                } else {
                                    layout.getBOReactIB().setImageResource(R.drawable.ic_baseline_check_no)
                                }
                            }
                        layout.getRemoveIB().setOnClickListener {
                            binding.TLFixtureList.removeView(currentView)
                            listFixtureView.remove(fixtureAbsNumber)
                            fixtureAbsNumber--
                            usedID.remove(i.id)
                            for(j in 0..i.channelCount) {
                                patch[i.universe]!!.channel.remove(i.startAddress+j)
                            }
                        }
                    }
                    patchDialog.hide()
                }
            }
            patchDialog.show()
        }
    }

    private fun populateSpinner(sp: Spinner, index: Int, string: String) {
        when(index) {
            0 -> db.getAllManufacturer { manuList ->
                    context?.let {
                            sp.adapter = ArrayAdapter<Any?>(it, android.R.layout.simple_spinner_dropdown_item, manuList)
                    }
                }
            1 ->
                db.getFixtureByManufacturer(string) { fixtList ->
                    context?.let {
                        sp.adapter = ArrayAdapter<Any?>(it, android.R.layout.simple_spinner_dropdown_item, fixtList)
                    }
                }
            2 ->
                db.getModeByFixtureName(string) { modeList ->
                    context?.let {
                        sp.adapter = ArrayAdapter<Any?>(it, android.R.layout.simple_spinner_dropdown_item, modeList)
                    }
                }
        }
    }

    private fun validateDialogParameter(currentDialog: FixturePatchDialogBinding) : MutableList<FixturePatchTemp> {
        val cpId: String = currentDialog.ETFpdID.text.toString().trim()
        val cpName: String = currentDialog.ETFdpName.text.toString().trim()
        val cpUniverse: String = currentDialog.ETFpdUniverse.text.toString().trim()
        val cpStartAddress: String = currentDialog.ETFpdStartAddress.text.toString().trim()
        val cpQuantity: String = currentDialog.ETFpdQuantity.text.toString().trim()
        val tempFixList: MutableList<FixturePatchTemp> = mutableListOf()

        if (checkNumber(
                currentDialog.TILFpdID,
                cpId,
                null
            ) and checkBasicText(
                currentDialog.TILFpdName,
                cpName,
                2
            ) and checkNumber(
                currentDialog.TILFpdUniverse,
                cpUniverse,
                UNIVERSE_LIMIT
            ) and checkNumber(
                currentDialog.TILFpdStartAddress,
                cpStartAddress,
                DMX_LIMIT
            ) and checkNumber(currentDialog.TILFpdQuantity, cpQuantity, DMX_LIMIT)
        ) {
            val valId: Int = cpId.toInt()
            val valName: String = cpName
            val valUniverse: Int = cpUniverse.toInt()
            val valStartAddress: Int = cpStartAddress.toInt()
            val valQuantity: Int = cpQuantity.toInt()
            val selManufacturer: String =
                currentDialog.SFdpManufacturer.selectedItem.toString().trim()
            val selFixture: String = currentDialog.SFpdFixtureType.selectedItem.toString().trim()
            val selMode: String = currentDialog.SFpdMode.selectedItem.toString().trim()

            var check = true
            var minID = -1
            var maxID = -1
            // controllo che l'id sia disponibile (o gli id in caso di quantità > 1)
            for (id in valId..valId + valQuantity) {
                if (id in usedID) {
                    if (minID == -1) {
                        minID = id
                    }
                    maxID = id
                    check = false
                }
            }
            if ((minID != -1) and (minID == maxID)) {
                Toast.makeText(context, "ID $minID GIA' UTILIZZATO", Toast.LENGTH_LONG).show()
            } else if ((minID != -1) and (minID != maxID)) {
                Toast.makeText(context, "ID $minID THRU $maxID GIA' UTILIZZATI", Toast.LENGTH_LONG)
                    .show()
            }

            // controllo che i canali siano nel range 0 - 512
            val fixtureChannel: Int = selMode.split("->").last().trim().toInt()
            if ((fixtureChannel * valQuantity) + valStartAddress > DMX_LIMIT) {
                Toast.makeText(
                    context,
                    "I CANALI TOTALI SUPERANO IL LIMITE DI: $DMX_LIMIT",
                    Toast.LENGTH_LONG
                ).show()
                check = false
            }

            // controllo che i canali non siano già occpuati
            var minChannel = -1
            var maxChannel = -1
            if (patch[valUniverse] != null) {
                for (channel in valStartAddress..(fixtureChannel * valQuantity) + valStartAddress) {
                    if (patch[valUniverse]!!.channel.containsKey(channel)) {
                        if (minChannel == -1) {
                            minChannel = channel
                        }
                        maxChannel = channel
                        check = false
                    }
                }
            }
            if ((minChannel != -1) and (minChannel == maxChannel)) {
                Toast.makeText(
                    context,
                    "CANALE $valUniverse.$minChannel GIA' UTILIZZATO",
                    Toast.LENGTH_LONG
                ).show()
            } else if ((minChannel != -1) and (minChannel != maxChannel)) {
                Toast.makeText(
                    context,
                    "CANALI $valUniverse.$minChannel THRU $valUniverse.$maxChannel GIA' UTILIZZATI",
                    Toast.LENGTH_LONG
                ).show()
            }


            if (check) {
                val fixtureMode: String = selMode.split("->").first().trim()
                for (i in 0 until valQuantity) {
                    val tempFixPatch = FixturePatchTemp(
                        valId + i,
                        "$valName ${i + 1}",
                        selManufacturer,
                        selFixture,
                        fixtureMode,
                        fixtureChannel,
                        valUniverse,
                        valStartAddress + fixtureChannel * i,
                        "")
                    tempFixList.add(tempFixPatch)
                }
            }
        }
        return tempFixList
    }

    /**
     * SEZIONE CONTROLLO DIALOG PATCH
     */
    private fun checkBasicText(til: TextInputLayout, text: String, minChar: Int): Boolean {
        // Controllo che la stringa inserita non sia vuota
        return if (text.isNotEmpty()) {
            if (text.length < minChar) {
                til.error = getString(R.string.minChar).replace("$", "" + minChar)
                false
            } else {
                til.error = null
                true
            }
        } else {
            til.error = getString(R.string.notBlank)
            false
        }
    }

    private fun checkNumber(til: TextInputLayout, text: String, limit: Int?): Boolean {
        // Controllo che la stringa inserita non sia vuota
        return if(text.isNotEmpty()) {
            // Controllo che la stringa inserita sia di soli numeri
            try {
                val dmx: Int = text.toInt()
                til.error = null
                // Controllo che il numero sia maggiore di 1
                if(dmx < 1) {
                    til.error = "${getString(R.string.mustBe)} > 1"
                    false
                }   else {
                    // Controllo che il numero sia minore del limite
                    if (limit != null && dmx > limit) {
                        til.error = getString(R.string.numberTooHigh).replace("$", "$limit")
                        false
                    } else {
                        til.error = null
                        true
                    }
                }
            } catch (e: NumberFormatException) {
                til.error = getString(R.string.onlyPositiveNumber)
                false
            }
        } else {
            til.error = getString(R.string.notBlank)
            false
        }
    }
}