package it.uninsubria.dmx_calculator.fragments.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import it.uninsubria.dmx_calculator.R
import it.uninsubria.dmx_calculator.databinding.FragmentDipSwitchBinding

private const val TAG = "DIP-SWITCH_Fragment"

class DipSwitchFragment : Fragment() {

    private var _binding: FragmentDipSwitchBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    // DIP-SWITCH FRAGMENT SECTION
    private var currentNumber: Int = 1
    private val numberLimitTop: Int = 512
    private val numberLimitBottom: Int = 0
    private var numberArray: ArrayList<TextView> = ArrayList()
    private var valueArray: ArrayList<TextView> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentDipSwitchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        valueArray.add(binding.valueBase1)
        valueArray.add(binding.valueBase10)
        valueArray.add(binding.valueBase100)

        numberArray.add(binding.TVNumber1)
        numberArray.add(binding.TVNumber2)
        numberArray.add(binding.TVNumber3)
        numberArray.add(binding.TVNumber4)
        numberArray.add(binding.TVNumber5)
        numberArray.add(binding.TVNumber6)
        numberArray.add(binding.TVNumber7)
        numberArray.add(binding.TVNumber8)
        numberArray.add(binding.TVNumber9)
        numberArray.add(binding.TVNumber10)

        for(i in 0 until numberArray.size) {
            numberArray[i].text = ("${i+1}")
        }

        binding.upBase1.setOnClickListener{change(+1)}
        binding.upBase10.setOnClickListener{change(+10)}
        binding.upBase100.setOnClickListener{change(+100)}
        binding.downBase1.setOnClickListener{change(-1)}
        binding.downBase10.setOnClickListener{change(-10)}
        binding.downBase100.setOnClickListener{change(-100)}

        change(0)
    }

    private fun change(myInt: Int) {
        if(myInt > 0) {
            if(currentNumber < numberLimitTop - myInt + 1) {
                currentNumber += myInt
            }
        } else {
            if(currentNumber > numberLimitBottom - myInt) {
                currentNumber += myInt
            }
        }
        updateTextView()
        updateDipSwitch()
    }

    private fun updateTextView() {
        // UPDATE TEXT VIEW
        when(currentNumber.toString().length) {
            1 -> {
                valueArray[0].text = "${currentNumber.toString()[0]}"
                valueArray[1].text = "0"
                valueArray[2].text = "0"
            }
            2 -> {
                valueArray[0].text = "${currentNumber.toString()[1]}"
                valueArray[1].text = "${currentNumber.toString()[0]}"
                valueArray[2].text = "0"
            }
            3 -> {
                valueArray[0].text = "${currentNumber.toString()[2]}"
                valueArray[1].text = "${currentNumber.toString()[1]}"
                valueArray[2].text = "${currentNumber.toString()[0]}"
            }
            else -> Log.e(TAG, "EXCEPTION")
        }
    }

    private fun updateDipSwitch() {
        var binString: String = Integer.toBinaryString(currentNumber)

        for(i in 1..(10 - binString.length)) {
            binString = "0$binString"
        }
        for(num in 0 until numberArray.size) {
            if (binString[num] == '1') {
                context?.let { numberArray[numberArray.size - (num + 1)].setTextColor(ContextCompat.getColor(it, R.color.teal_200)) }

            } else {
                context?.let { numberArray[numberArray.size - (num + 1)].setTextColor(ContextCompat.getColor(it, R.color.gray)) }
            }
        }
    }
}