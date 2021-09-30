package it.uninsubria.dmx_calculator.fragments.main

import android.content.res.Resources
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import it.uninsubria.dmx_calculator.R
import it.uninsubria.dmx_calculator.databinding.ActivityFixtureInfoBinding
import it.uninsubria.dmx_calculator.models.Fixture

@Suppress("unused")
private const val TAG = "Fixture_info_activity"
private const val minDistance = 0F
private const val maxDistance = 100F
private const val maxDistanceChart = 20F

class FixtureInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFixtureInfoBinding
    private var selectedZoom = 0F
    private var selectedDistance = minDistance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFixtureInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fixture = intent.getSerializableExtra("selectedFixture") as Fixture
        binding.TVFixInfoName.text = fixture.name
        binding.TVFixInfoManufacturer.text = fixture.manufacturer
        binding.TVFixInfoPowerConsumpion.text = ("${this.getString(R.string.fi_powerConsumption)} ${fixture.powerConsumption} W")
        binding.CFirstFixType.text = fixture.projectorType[0].toString()

        // Imposto info tipo fixture
        if (fixture.projectorType.size > 0) {
            for (i in 1 until fixture.projectorType.size) {
                val newTypeChip = Chip(this)
                newTypeChip.text = fixture.projectorType[i].toString()
                val params = (LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ))
                params.setMargins(8, 0, 0, 0)
                newTypeChip.layoutParams = params
                binding.LLFixInfoType.addView(newTypeChip)
            }
        }
        binding.CLampType.text = fixture.lampType.toString()
        // Imposto info modalita fixture
        binding.LLFixInfoMode.removeAllViews()
        for ((k, v) in fixture.mode) {
            val newModeChip = Chip(this)
            newModeChip.text = ("$k -> $v")
            val params = (LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ))
            params.setMargins(0, 8, 0, 0)
            newModeChip.layoutParams = params
            binding.LLFixInfoMode.addView(newModeChip)
        }
        binding.TVMinZoomInfo.text = ("${fixture.minZoom} °")
        binding.TVMaxZoomInfo.text = ("${fixture.maxZoom} °")
        binding.TVMinDistance.text = ("$minDistance m")
        binding.TVMaxDistance.text = ("$maxDistance m")
        val params = (LinearLayout.LayoutParams(Resources.getSystem().displayMetrics.widthPixels - binding.LLFixInfoMainLayout.paddingStart*2, (Resources.getSystem().displayMetrics.widthPixels - binding.LLFixInfoMainLayout.paddingStart*2) / 16 * 9))
        binding.PCZoomInfo.layoutParams = params
        binding.PCZoomInfo.setZoom(fixture.getRadius(fixture.minZoom, maxDistanceChart), fixture.getRadius(fixture.maxZoom, maxDistanceChart))

        // Imposto Chip diametri
        binding.C5m.text = ("5m: ${fixture.getRadius(fixture.minZoom, 5F)}m")
        binding.C100m.text = ("100m: ${fixture.getRadius(fixture.minZoom, 100F)}m")

        if(fixture.minZoom != fixture.maxZoom) {
            selectedZoom = fixture.minZoom
            // imposto parametri Slider zoom
            binding.SBZoomSelection.valueTo = fixture.maxZoom
            binding.SBZoomSelection.valueFrom = fixture.minZoom
            // imposto parametri Slider distanza
            binding.SBLineSelection.valueTo = maxDistance
            binding.SBLineSelection.valueFrom = minDistance
            binding.SBLineSelection.stepSize = 5F
            // Imposto listener Slider
            binding.SBZoomSelection.setOnChangeListener { _, value ->
                selectedZoom = value
                binding.PCZoomInfo.setPersonalizedZoom(fixture.getRadius(value, maxDistanceChart))
                updateChip(fixture)
            }
            binding.SBLineSelection.setOnChangeListener { _, value ->
                selectedDistance = value
                updateChip(fixture)
            }
        } else {
            binding.SBZoomSelection.isEnabled = false
        }
    }

    private fun updateChip(myFix: Fixture) {
        binding.CZoomSelected.text = ("${selectedDistance.toInt()}m: ${myFix.getRadius(selectedZoom, selectedDistance)}m")
    }
}