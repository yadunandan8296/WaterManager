package com.watermanager.ui.sms

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.watermanager.databinding.FragmentSmsPreviewBinding
import com.watermanager.utils.SmsUtils
import com.watermanager.viewmodel.TenantViewModel
import com.watermanager.viewmodel.WaterViewModel
import kotlinx.coroutines.launch

class SmsPreviewFragment : Fragment() {

    private var _binding: FragmentSmsPreviewBinding? = null
    private val binding get() = _binding!!
    private val waterVm: WaterViewModel by activityViewModels()
    private val tenantVm: TenantViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSmsPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        waterVm.currentLog.observe(viewLifecycleOwner) { log ->
            if (log == null) return@observe
            val msg = SmsUtils.buildSmsMessage(log.startTime, log.durationMinutes, log.endTime)
            binding.tvSmsPreview.text = msg
            binding.tvStartTime.text = "Start: ${log.startTime}"
            binding.tvDuration.text = "Duration: ${log.durationMinutes} min"
            binding.tvEndTime.text = "Available till: ${log.endTime}"
        }

        binding.btnSendSms.setOnClickListener {
            sendToSelectedTenants()
        }
    }

    private fun sendToSelectedTenants() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(requireContext(), "SMS permission not granted. Enable it in Settings.", Toast.LENGTH_LONG).show()
            return
        }

        val log = waterVm.currentLog.value
        if (log == null) {
            Toast.makeText(requireContext(), "No water entry found", Toast.LENGTH_SHORT).show()
            return
        }

        val message = SmsUtils.buildSmsMessage(log.startTime, log.durationMinutes, log.endTime)

        lifecycleScope.launch {
            val selected = tenantVm.getAllOnce().filter { it.isSelected }
            if (selected.isEmpty()) {
                Toast.makeText(requireContext(), "No tenants selected. Go to Tenants tab and select.", Toast.LENGTH_LONG).show()
                return@launch
            }

            binding.btnSendSms.isEnabled = false
            binding.btnSendSms.text = "Sending..."

            var successCount = 0
            val failedNames = mutableListOf<String>()

            selected.forEach { tenant ->
                val ok = SmsUtils.sendSms(tenant.phone, message)
                if (ok) successCount++ else failedNames.add(tenant.name)
            }

            waterVm.saveLog(successCount)

            binding.btnSendSms.isEnabled = true
            binding.btnSendSms.text = "Send SMS"

            if (failedNames.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "✅ SMS sent to all $successCount tenants!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Sent to $successCount. Failed: ${failedNames.joinToString(", ")}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
