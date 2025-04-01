package com.example.carlogger.ui.car.add_edit

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.carlogger.R
import com.example.carlogger.databinding.FragmentAddEditCarBinding
import com.example.carlogger.ui.SavedStateViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddEditCarFragment : Fragment() {

    private var _binding: FragmentAddEditCarBinding? = null
    private val binding get() = _binding!!

    private val args: AddEditCarFragmentArgs by navArgs()
    private val viewModel: AddEditCarViewModel by viewModels {
        SavedStateViewModelFactory(requireContext(), this, args.toBundle())
    }

    private var selectedImageUri: Uri? = null

    // Image picking result
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.imgCarPreview.load(it) {
                crossfade(true)
            }
            binding.imgCarPreview.visibility = View.VISIBLE
            binding.layoutAddImage.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditCarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupColorDropdown()
        setupListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.title = args.title
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupColorDropdown() {
        val colors = resources.getStringArray(R.array.car_colors)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, colors)
        binding.dropdownColor.setAdapter(adapter)

        binding.dropdownColor.setOnItemClickListener { _, _, position, _ ->
            viewModel.color.value = colors[position]
        }
    }

    private fun setupListeners() {
        binding.layoutAddImage.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.imgCarPreview.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.etPurchaseDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnSave.setOnClickListener {
            if (validateForm()) {
                saveCarDetails()
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.car.collect { car ->
                        car?.let { populateFormWithCar(it) }
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

                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.loadingView.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
                        binding.loadingView.loadingText.text = getString(R.string.saving_image)
                    }
                }
            }
        }
    }

    private fun populateFormWithCar(car: com.example.carlogger.data.model.Car) {
        binding.etBrand.setText(car.brand)
        binding.etModel.setText(car.model)
        binding.etYear.setText(car.year.toString())
        binding.etLicensePlate.setText(car.licensePlate)
        binding.dropdownColor.setText(car.color, false)
        binding.etHorsepower.setText(car.horsepower.toString())
        binding.etNotes.setText(car.notes)

        car.purchaseDate?.let { date ->
            binding.etPurchaseDate.setText(formatDate(date))
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        if (binding.etBrand.text.isNullOrBlank()) {
            binding.tilBrand.error = getString(R.string.required_field)
            isValid = false
        } else {
            binding.tilBrand.error = null
        }

        if (binding.etModel.text.isNullOrBlank()) {
            binding.tilModel.error = getString(R.string.required_field)
            isValid = false
        } else {
            binding.tilModel.error = null
        }

        val yearText = binding.etYear.text.toString()
        if (yearText.isBlank()) {
            binding.tilYear.error = getString(R.string.required_field)
            isValid = false
        } else {
            val year = yearText.toIntOrNull()
            if (year == null || year < 1900 || year > Calendar.getInstance().get(Calendar.YEAR) + 1) {
                binding.tilYear.error = getString(R.string.invalid_year)
                isValid = false
            } else {
                binding.tilYear.error = null
            }
        }

        if (binding.etLicensePlate.text.isNullOrBlank()) {
            binding.tilLicensePlate.error = getString(R.string.required_field)
            isValid = false
        } else {
            binding.tilLicensePlate.error = null
        }

        return isValid
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        // If we already have a date, use it
        viewModel.purchaseDate.value?.let {
            calendar.time = it
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            calendar.set(selectedYear, selectedMonth, selectedDay)
            viewModel.purchaseDate.value = calendar.time
            binding.etPurchaseDate.setText(formatDate(calendar.time))
        }, year, month, day).show()
    }

    private fun formatDate(date: Date): String {
        val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return formatter.format(date)
    }

    private fun saveCarDetails() {
        viewModel.brand.value = binding.etBrand.text.toString().trim()
        viewModel.model.value = binding.etModel.text.toString().trim()
        viewModel.year.value = binding.etYear.text.toString().toIntOrNull() ?: 0
        viewModel.licensePlate.value = binding.etLicensePlate.text.toString().trim()
        viewModel.color.value = binding.dropdownColor.text.toString().trim()
        viewModel.horsepower.value = binding.etHorsepower.text.toString().toIntOrNull() ?: 0
        viewModel.notes.value = binding.etNotes.text.toString().trim()

        // Save car with the selected image
        viewModel.saveCar(selectedImageUri)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}