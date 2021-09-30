package it.uninsubria.dmx_calculator.fragments.main.project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import it.uninsubria.dmx_calculator.databinding.FragmentListBinding
import it.uninsubria.dmx_calculator.firebase.Firestore

@Suppress("unused")
private const val TAG = "List_Project_Fragment"

class ListProjectFragment: Fragment() {
    private val myDB: Firestore = Firestore()

    private var _binding: FragmentListBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}