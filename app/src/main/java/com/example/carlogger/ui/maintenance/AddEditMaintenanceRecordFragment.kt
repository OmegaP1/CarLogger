package com.example.carlogger.ui.maintenance

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.carlogger.R
import com.example.carlogger.databinding.FragmentAddEditMaintenanceRecordBinding
import com.example.carlogger.ui.ViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddEditMaintenanceRecordFragment : Fragment() {

    private var _binding: FragmentAddEditMaintenanceRecordBinding? = null
    private val binding get() = _binding!!

    private val args: AddEditMaintenanceRecordFragmentArgs by navArgs()
    private val viewModel: AddEditMaintenanceRecordViewModel by viewModels {
        SavedStateViewModelFactory(requireContext(), this, args.toBundle())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditMaintenanceRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupServiceTypeDropdown()
        setupDatePicker()
        setupListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.title = args.title
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupServiceTypeDropdown() {
        val serviceTypes = arrayOf(
            "Oil Change",
            "Tire Rotation",
            "Brake Service",
            "Air Filter",
            "Battery Replacement",
            "Transmission Service",
            "Coolant Flush",
            "Inspection",
            "Other"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            serviceTypes
        )

        binding.spinnerServiceType.setAdapter(adapter)
        binding.spinnerServiceType.setOnItemClickListener { _, _, position, _ ->
            viewModel.serviceType.value = serviceTypes[position]
        }
    }

    private fun setupDatePicker() {
        // Default to current date
        val calendar = Calendar.getInstance()
        viewModel.serviceDate.value = calendar.time
        binding.etServiceDate.setText(formatDate(calendar.time))

        binding.etServiceDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            if (validateForm()) {
                saveRecord()
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.record.collect { record ->
                        record?.let { populateFormWithRecord(it) }
                    }
                }

                launch {
                    viewModel.saveComplete.collect { isComplete ->
                        if (isComplete) {
                            findNavController().navigateUp()
                            viewModel.resetSaveComplete()
                        }
                    }
                }
            }
        }
    }

    private fun populateFormWithRecord(record: MaintenanceRecord) {
        binding.spinnerServiceType.setText(record.serviceType, false)
        binding.etServiceDate.setText(formatDate(record.date))
        binding.etOdometer.setText(record.odometer.toString())
        binding.etCost.setText(record.cost.toString())
        binding.etLocation.setText(record.location)
        binding.etDescription.setText(record.description)
    }

    private fun validateForm(): Boolean {
        var isValid = true

        if (binding.spinnerServiceType.text.isNullOrBlank()) {
            binding.tilServiceType.error = getString(R.string.required_field)
            isValid = false
        } else {
            binding.tilServiceType.error = null
        }

        if (binding.etServiceDate.text.isNullOrBlank()) {
            binding.tilServiceDate.error = getString(R.string.required_field)
            isValid = false
        } else {
            binding.tilServiceDate.error = null
        }

        if (binding.etOdometer.text.isNullOrBlank()) {
            binding.tilOdometer.error = getString(R.string.required_field)
            isValid = false
        } else {
            binding.tilOdometer.error = null
        }

        if (binding.etCost.text.isNullOrBlank()) {
            binding.tilCost.error = getString(R.string.required_field)
            isValid = false
        } else {
            binding.tilCost.error = null
        }

        return isValid
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        viewModel.serviceDate.value?.let {
            calendar.time = it
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            calendar.set(selectedYear, selectedMonth, selectedDay)
            viewModel.serviceDate.value = calendar.time
            binding.etServiceDate.setText(formatDate(calendar.time))
        }, year, month, day).show()
    }

    private fun formatDate(date: Date): String {
        val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return formatter.format(date)
    }

    private fun saveRecord() {
        viewModel.serviceType.value = binding.spinnerServiceType.text.toString().trim()
        viewModel.odometer.value = binding.etOdometer.text.toString().toIntOrNull() ?: 0
        viewModel.cost.value = binding.etCost.text.toString().toDoubleOrNull() ?: 0.0
        viewModel.location.value = binding.etLocation.text.toString().trim()
        viewModel.description.value = binding.etDescription.text.toString().trim()

        viewModel.saveRecord()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}