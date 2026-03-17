package com.watermanager.ui.tenants

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.watermanager.data.model.Tenant
import com.watermanager.databinding.ItemTenantBinding

class TenantAdapter(
    private val onEdit: (Tenant) -> Unit,
    private val onDelete: (Tenant) -> Unit,
    private val onChecked: (Tenant, Boolean) -> Unit
) : ListAdapter<Tenant, TenantAdapter.TenantViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Tenant>() {
            override fun areItemsTheSame(old: Tenant, new: Tenant) = old.id == new.id
            override fun areContentsTheSame(old: Tenant, new: Tenant) = old == new
        }
    }

    inner class TenantViewHolder(private val binding: ItemTenantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tenant: Tenant) {
            binding.tvTenantName.text = tenant.name
            binding.tvTenantPhone.text = tenant.phone

            // Prevent listener firing during bind
            binding.checkboxTenant.setOnCheckedChangeListener(null)
            binding.checkboxTenant.isChecked = tenant.isSelected
            binding.checkboxTenant.setOnCheckedChangeListener { _, checked ->
                onChecked(tenant, checked)
            }

            binding.btnEdit.setOnClickListener { onEdit(tenant) }
            binding.btnDelete.setOnClickListener { onDelete(tenant) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TenantViewHolder {
        val binding = ItemTenantBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TenantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TenantViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
