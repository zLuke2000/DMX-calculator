package it.uninsubria.dmx_calculator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import it.uninsubria.dmx_calculator.R
import it.uninsubria.dmx_calculator.databinding.FragmentListFixtureBinding
import it.uninsubria.dmx_calculator.firebase.Firestore
import it.uninsubria.dmx_calculator.models.Fixture

class ListFixtureFragment: Fragment(){
    private val TAG = "List_Fixture_Fragment"
    private val myDB: Firestore = Firestore()

    private var _binding: FragmentListFixtureBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    // Lista e adapter necessari per inizializzare la RecyclerView
    private lateinit var fixtureList: List<Fixture>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentListFixtureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myDB.getAllFixture { resultSet ->
            if (resultSet != null) {
                fixtureList = resultSet
                val adapter: ArrayAdapter<String> = t commiArrayAdapter<String>(context, R.layout.row_fixture, fixtureList)
                val listView: ListView = binding.FixtureRecyclerView
                listView.adapter = adapter
            }
        }
    }
}