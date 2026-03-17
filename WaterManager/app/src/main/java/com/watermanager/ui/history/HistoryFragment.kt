package com.watermanager.ui.history

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.watermanager.databinding.FragmentHistoryBinding
import com.watermanager.utils.SmsUtils
import com.watermanager.viewmodel.TenantViewModel
import com.watermanager.viewmodel.WaterViewModel
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val waterVm: WaterViewModel by activityViewModels()
    private val tenantVm: TenantViewModel by activityViewModels()
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = HistoryAdapter { log ->
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Log")
                .setMessage("Remove this water log entry?")
                .setPositiveButton("Delete") { _, _ -> waterVm.deleteLog(log) }
                .setNegativeButton("Cancel", null)
                .show()
        }

        binding.recyclerHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerHistory.adapter = adapter

        waterVm.allLogs.observe(viewLifecycleOwner) { logs ->
            adapter.submitList(logs)
            binding.tvEmptyHistory.visibility =
                if (logs.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.btnResendLast.setOnClickListener { resendLastMessage() }
    }

    private fun resendLastMessage() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(requireContext(), "SMS permission required", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            waterVm.loadLatestLog()
            val log = waterVm.currentLog.value
            if (log == null) {
                Toast.makeText(requireContext(), "No previous log found", Toast.LENGTH_SHORT).show()
                return@launch
            }
            val msg = SmsUtils.buildSmsMessage(log.startTime, log.durationMinutes, log.endTime)
            val selected = tenantVm.getAllOnce().filter { it.isSelected }
            if (selected.isEmpty()) {
                Toast.makeText(requireContext(), "No tenants selected", Toast.LENGTH_SHORT).show()
                return@launch
            }
            var ok = 0
            selected.forEach { if (SmsUtils.sendSms(it.phone, msg)) ok++ }
            Toast.makeText(requireContext(), "Resent to $ok tenant(s) ✓", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
