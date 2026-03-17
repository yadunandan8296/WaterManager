package com.watermanager.ui.tenants

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.watermanager.R
import com.watermanager.data.model.Tenant
import com.watermanager.databinding.FragmentTenantsBinding
import com.watermanager.viewmodel.TenantViewModel

class TenantsFragment : Fragment() {

    private var _binding: FragmentTenantsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TenantViewModel by activityViewModels()
    private lateinit var adapter: TenantAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTenantsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TenantAdapter(
            onEdit = { showEditDialog(it) },
            onDelete = { showDeleteConfirmation(it) },
            onChecked = { tenant, checked ->
                viewModel.update(tenant.copy(isSelected = checked))
            }
        )

        binding.recyclerTenants.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTenants.adapter = adapter

        viewModel.allTenants.observe(viewLifecycleOwner) { tenants ->
            adapter.submitList(tenants)
            binding.tvEmptyState.visibility =
                if (tenants.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.fabAddTenant.setOnClickListener { showAddDialog() }

        binding.btnSelectAll.setOnClickListener {
            val current = viewModel.allTenants.value ?: return@setOnClickListener
            val allSelected = current.all { it.isSelected }
            current.forEach { viewModel.update(it.copy(isSelected = !allSelected)) }
        }
    }

    private fun showAddDialog() {
        showTenantDialog(null) { name, phone ->
            if (validateInput(name, phone)) {
                viewModel.insert(Tenant(name = name, phone = phone))
            }
        }
    }

    private fun showEditDialog(tenant: Tenant) {
        showTenantDialog(tenant) { name, phone ->
            if (validateInput(name, phone)) {
                viewModel.update(tenant.copy(name = name, phone = phone))
            }
        }
    }

    private fun showTenantDialog(tenant: Tenant?, onSave: (String, String) -> Unit) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_tenant, null)
        val etName = dialogView.findViewById<EditText>(R.id.etTenantName)
        val etPhone = dialogView.findViewById<EditText>(R.id.etTenantPhone)

        tenant?.let {
            etName.setText(it.name)
            etPhone.setText(it.phone)
        }

        AlertDialog.Builder(requireContext())
            .setTitle(if (tenant == null) "Add Tenant" else "Edit Tenant")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                onSave(etName.text.toString().trim(), etPhone.text.toString().trim())
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirmation(tenant: Tenant) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Tenant")
            .setMessage("Remove ${tenant.name} from the list?")
            .setPositiveButton("Delete") { _, _ -> viewModel.delete(tenant) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun validateInput(name: String, phone: String): Boolean {
        return when {
            name.isEmpty() -> {
                Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
                false
            }
            phone.length != 10 || !phone.all { it.isDigit() } -> {
                Toast.makeText(requireContext(), "Enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
