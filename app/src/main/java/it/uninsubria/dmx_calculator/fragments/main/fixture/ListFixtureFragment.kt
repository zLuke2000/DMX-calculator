package it.uninsubria.dmx_calculator.fragments.main.fixture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import it.uninsubria.dmx_calculator.adapter.ListFixtureAdapter
import it.uninsubria.dmx_calculator.R
import it.uninsubria.dmx_calculator.databinding.FragmentListBinding
import it.uninsubria.dmx_calculator.firebase.Firestore
import it.uninsubria.dmx_calculator.models.Fixture

@Suppress("unused")
private const val TAG = "List_Fixture_Fragment"

class ListFixtureFragment: Fragment(){
    private val myDB: Firestore = Firestore()

    private var _binding: FragmentListBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    // Lista e adapter necessari per inizializzare la RecyclerView
    private lateinit var fixtureList: ArrayList<Fixture>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myDB.getAllFixture { resultSet ->
            if (resultSet.size > 0) {
                fixtureList = resultSet
                val adapter = context?.let { ListFixtureAdapter(it, R.layout.row_fixture, fixtureList) }
                val listView: ListView = binding.FixtureListView
                listView.adapter = adapter
            }
        }
    }
}