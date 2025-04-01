package com.example.carlogger.ui.car.detail

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.carlogger.R
import com.example.carlogger.data.model.Car
import com.example.carlogger.databinding.FragmentCarDetailBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class CarDetailFragment : Fragment() {

    private var _binding: FragmentCarDetailBinding? = null
    private val binding get() = _binding!!

    private val args: CarDetailFragmentArgs by navArgs()
    private val viewModel: CarDetailViewModel by viewModels {
        SavedStateViewModelFactory(requireContext(), this, args.toBundle())
    }

    // Image picking result
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            viewModel.addImage(it, isPrimary = viewModel.carImages.value.isEmpty())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_delete -> {
                    showDeleteConfirmationDialog()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupListeners() {
        binding.fabEdit.setOnClickListener {
            findNavController().navigate(
                CarDetailFragmentDirections.actionCarDetailToAddEditCar(
                    carId = args.carId,
                    title = getString(R.string.edit_car)
                )
            )
        }

        binding.btnAddImage.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.btnAddRecord.setOnClickListener {
            findNavController().navigate(
                CarDetailFragmentDirections.actionCarDetailToAddEditMaintenanceRecord(
                    carId = args.carId,
                    recordId = 0L,
                    title = getString(R.string.add_maintenance_record)
                )
            )
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.car.collect { car ->
                        car?.let { updateCarInfo(it) }
                    }
                }

                launch {
                    viewModel.carImages.collect { images ->
                        // TODO: Setup images adapter
                    }
                }

                launch {
                    viewModel.maintenanceRecords.collect { records ->
                        // TODO: Setup maintenance records adapter
                    }
                }
            }
        }
    }

    private fun updateCarInfo(car: Car) {
        binding.collapsingToolbar.title = "${car.brand} ${car.model}"
        binding.tvBrand.text = car.brand
        binding.tvModel.text = car.model
        binding.tvYear.text = car.year.toString()
        binding.tvLicensePlate.text = car.licensePlate
        binding.tvColor.text = car.color

        car.purchaseDate?.let { date ->
            val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            binding.tvPurchaseDate.text = format.format(date)
        } ?: run {
            binding.tvPurchaseDate.text = "-"
        }

        binding.tvNotes.text = car.notes.takeIf { it.isNotBlank() } ?: "-"
    }

    private fun showDeleteConfirmationDialog() {
        val car = viewModel.car.value ?: return

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_car)
            .setMessage(getString(R.string.delete_car_confirmation, car.brand, car.model))
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteCar(car)
                findNavController().navigateUp()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}