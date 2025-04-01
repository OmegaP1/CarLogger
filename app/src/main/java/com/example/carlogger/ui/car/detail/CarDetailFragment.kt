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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.carlogger.R
import com.example.carlogger.data.model.Car
import com.example.carlogger.data.model.CarImage
import com.example.carlogger.data.model.MaintenanceRecord
import com.example.carlogger.databinding.FragmentCarDetailBinding
import com.example.carlogger.ui.SavedStateViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class CarDetailFragment : Fragment(), MaintenanceRecordsAdapter.MaintenanceRecordListener, CarImagesAdapter.CarImageListener {

    private var _binding: FragmentCarDetailBinding? = null
    private val binding get() = _binding!!

    private val args: CarDetailFragmentArgs by navArgs()
    private val viewModel: CarDetailViewModel by viewModels {
        SavedStateViewModelFactory(requireContext(), this, args.toBundle())
    }

    private lateinit var maintenanceAdapter: MaintenanceRecordsAdapter
    private lateinit var imagesAdapter: CarImagesAdapter

    // Image picking result
    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
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
        setupRecyclerViews()
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

    private fun setupRecyclerViews() {
        // Setup Maintenance Records RecyclerView
        maintenanceAdapter = MaintenanceRecordsAdapter(this)
        binding.rvMaintenanceRecords.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = maintenanceAdapter
        }

        // Setup Images RecyclerView
        imagesAdapter = CarImagesAdapter(this)
        binding.rvImages.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = imagesAdapter
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
                        imagesAdapter.submitList(images)
                        updateImagesInViewPager(images)
                    }
                }

                launch {
                    viewModel.maintenanceRecords.collect { records ->
                        maintenanceAdapter.submitList(records)
                    }
                }
            }
        }
    }

    private fun updateImagesInViewPager(images: List<CarImage>) {
        // If we have images, set up a ViewPager adapter for them
        if (images.isNotEmpty()) {
            val adapter = CarImagePagerAdapter(images)
            binding.viewpagerCarImages.adapter = adapter
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

    // MaintenanceRecordsAdapter.MaintenanceRecordListener implementation
    override fun onRecordClicked(record: MaintenanceRecord) {
        // Navigate to edit maintenance record
        findNavController().navigate(
            CarDetailFragmentDirections.actionCarDetailToAddEditMaintenanceRecord(
                carId = args.carId,
                recordId = record.recordId,
                title = getString(R.string.edit_maintenance_record)
            )
        )
    }

    override fun onRecordMenuClicked(record: MaintenanceRecord, itemView: View) {
        val popupMenu = android.widget.PopupMenu(requireContext(), itemView)
        popupMenu.menuInflater.inflate(R.menu.menu_maintenance_item, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    onRecordClicked(record)
                    true
                }

                R.id.action_delete -> {
                    showDeleteRecordConfirmationDialog(record)
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }

    private fun showDeleteRecordConfirmationDialog(record: MaintenanceRecord) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete)
            .setMessage(R.string.delete_maintenance_record_confirmation)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteMaintenanceRecord(record)
            }
            .show()
    }

    // CarImagesAdapter.CarImageListener implementation
    override fun onImageClicked(carImage: CarImage, position: Int) {
        // Focus ViewPager on the clicked image
        binding.viewpagerCarImages.currentItem = position
    }

    override fun onImageOptionsClicked(carImage: CarImage) {
        // This is handled in the adapter
    }

    override fun onSetPrimaryClicked(carImage: CarImage) {
        viewModel.setImageAsPrimary(carImage)
    }

    override fun onDeleteImageClicked(carImage: CarImage) {
        showDeleteImageConfirmationDialog(carImage)
    }

    private fun showDeleteImageConfirmationDialog(carImage: CarImage) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete)
            .setMessage(R.string.delete_image_confirmation)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteImage(carImage)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}