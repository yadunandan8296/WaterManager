package com.watermanager.ui.water

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.watermanager.R
import com.watermanager.databinding.FragmentWaterEntryBinding
import com.watermanager.utils.TimeUtils
import com.watermanager.viewmodel.WaterViewModel

class WaterEntryFragment : Fragment() {

    private var _binding: FragmentWaterEntryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WaterViewModel by activityViewModels()
    private var selectedHour = 0
    private var selectedMinute = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWaterEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Default to current time
        val (h, m) = TimeUtils.currentHourMinute()
        selectedHour = h
        selectedMinute = m
        binding.tvSelectedTime.text = TimeUtils.formatTime(h, m)

        binding.btnPickTime.setOnClickListener {
            TimePickerDialog(requireContext(), { _, hour, minute ->
                selectedHour = hour
                selectedMinute = minute
                binding.tvSelectedTime.text = TimeUtils.formatTime(hour, minute)
            }, selectedHour, selectedMinute, true).show()
        }

        binding.btnPreviewSms.setOnClickListener {
            val durationStr = binding.etDuration.text.toString().trim()
            if (durationStr.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter duration in minutes", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val duration = durationStr.toIntOrNull()
            if (duration == null || duration <= 0 || duration > 1440) {
                Toast.makeText(requireContext(), "Enter a valid duration (1–1440 minutes)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val startTime = TimeUtils.formatTime(selectedHour, selectedMinute)
            viewModel.prepareEntry(startTime, duration)
            findNavController().navigate(R.id.action_waterEntryFragment_to_smsPreviewFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
