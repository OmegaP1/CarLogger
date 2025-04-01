package com.example.carlogger.ui.car.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yourname.carlogger.R
import com.yourname.carlogger.data.model.Car
import com.yourname.carlogger.databinding.FragmentCarListBinding
import com.yourname.carlogger.ui.ViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CarListFragment : Fragment(), CarAdapter.CarItemListener {

    private var _binding: FragmentCarListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CarListViewModel by viewModels { ViewModelFactory(requireContext()) }
    private lateinit var carAdapter: CarAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        observeViewModel()
        setupListeners()
    }

    private fun setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_settings -> {
                    // Navigate to settings
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        carAdapter = CarAdapter(this)
        binding.carsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = carAdapter
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.carsWithImages.collect { carsWithImages ->
                    carAdapter.submitList(carsWithImages)
                    updateEmptyState(carsWithImages.isEmpty())
                }
            }
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyState.isVisible = isEmpty
        binding.carsRecyclerView.isVisible = !isEmpty
    }

    private fun setupListeners() {
        binding.fabAddCar.setOnClickListener {
            navigateToAddCar()
        }

        binding.btnAddFirstCar.setOnClickListener {
            navigateToAddCar()
        }
    }

    private fun navigateToAddCar() {
        findNavController().navigate(
            CarListFragmentDirections.actionCarListToAddEditCar(0L, getString(R.string.add_car))
        )
    }

    override fun onCarClicked(carWithImage: CarWithImage) {
        findNavController().navigate(
            CarListFragmentDirections.actionCarListToCarDetail(carWithImage.car.carId)
        )
    }

    override fun onCarMenuClicked(carWithImage: CarWithImage, itemView: View) {
        val popupMenu = android.widget.PopupMenu(requireContext(), itemView)
        popupMenu.menuInflater.inflate(R.menu.menu_car_item, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    findNavController().navigate(
                        CarListFragmentDirections.actionCarListToAddEditCar(
                            carWithImage.car.carId,
                            getString(R.string.edit_car)
                        )
                    )
                    true
                }
                R.id.action_delete -> {
                    showDeleteConfirmationDialog(carWithImage.car)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun showDeleteConfirmationDialog(car: Car) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_car)
            .setMessage(getString(R.string.delete_car_confirmation, car.brand, car.model))
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteCar(car)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}