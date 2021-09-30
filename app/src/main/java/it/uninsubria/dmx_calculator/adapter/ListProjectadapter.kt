package it.uninsubria.dmx_calculator.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import it.uninsubria.dmx_calculator.databinding.RowProjectBinding
import it.uninsubria.dmx_calculator.fragments.main.ProjectInfoActivity
import it.uninsubria.dmx_calculator.models.Project

@Suppress("unused")
private const val TAG = "List_Project_Adapter"

class ListProjectadapter(context: Context, private var resources: Int, private var items: ArrayList<Project>) : ArrayAdapter<Project>(context, resources, items) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(resources, null)
        val binding = RowProjectBinding.bind(view)

        binding.TVProjectName
        binding.TVTotalFixtureNumber
        binding.TVTotalChannelNumber
        binding.TVTotalPowerConsumption
        binding.IVProject

        binding.CVRowProject.setOnClickListener {
            val intent = Intent(context, ProjectInfoActivity::class.java)
            intent.putExtra("selectedProject", items[position])
            context.startActivity(intent)
        }
        return view
    }
}