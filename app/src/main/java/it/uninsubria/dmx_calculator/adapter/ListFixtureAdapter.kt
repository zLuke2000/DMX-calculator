package it.uninsubria.dmx_calculator.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import it.uninsubria.dmx_calculator.R
import it.uninsubria.dmx_calculator.databinding.RowFixtureBinding
import it.uninsubria.dmx_calculator.fragments.main.FixtureInfoActivity
import it.uninsubria.dmx_calculator.models.Fixture


@Suppress("unused")
private const val TAG = "List_Fixture_Adapter"

class ListFixtureAdapter(context: Context, private var resources: Int, private var items: ArrayList<Fixture>) : ArrayAdapter<Fixture>(context, resources, items) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(resources, null)
        val binding = RowFixtureBinding.bind(view)

        binding.TVFixtureName.text = items[position].name
        binding.TVFixtureManufacturer.text = items[position].manufacturer
        binding.TVFixtureLampType.text = ("(${items[position].lampType})")
        binding.TVFixtureType.text = items[position].projectorType.toString()
        binding.TVFixturePowerConsumption.text = ("${context.getString(R.string.nfPowerConsumption)}: ${items[position].powerConsumption} W")

        binding.CVRowFixture.setOnClickListener {
            val intent = Intent(context, FixtureInfoActivity::class.java)
            intent.putExtra("selectedFixture", items[position])
            context.startActivity(intent)
        }
        return view
    }
}