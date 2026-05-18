package com.example.myapplication.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.BrandAdapter
import com.example.myapplication.adapter.OrderAdapter
import com.example.myapplication.adapter.ProductAdapter
import com.example.myapplication.data.DataRepository
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.viewmodel.MainViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    private val takePicturePreview = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        if (bitmap != null) {
            viewModel.updateProfileImage(bitmap)
        }
    }

    private val requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            takePicturePreview.launch()
        } else {
            Toast.makeText(requireContext(), "Camera permission is required to take a photo", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        setupBrands()
        setupFeaturedProducts()
        setupOrderHistory()
        observeUser()
    }

    private fun setupClickListeners() {
        binding.searchBar.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }

        binding.cartButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_cartFragment)
        }

        binding.locationPickerButton.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:0,0?q=clothing+stores")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        binding.profileImageCard.setOnClickListener {
            checkCameraPermission()
        }
    }

    private fun setupBrands() {
        val brands = DataRepository.brands
        val adapter = BrandAdapter(brands) { brand ->
            val bundle = Bundle().apply {
                putString("brandName", brand.name)
            }
            try {
                findNavController().navigate(R.id.brandProductsFragment, bundle)
            } catch (e: Exception) {
            }
        }
        binding.brandsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.brandsRecyclerView.adapter = adapter
    }

    private fun setupFeaturedProducts() {
        val products = DataRepository.products.shuffled().take(6)
        val adapter = ProductAdapter(products) { product ->
            viewModel.addToCart(product)
            Toast.makeText(requireContext(), "${product.name} added to cart", Toast.LENGTH_SHORT).show()
        }
        binding.featuredProductsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.featuredProductsRecyclerView.adapter = adapter
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                takePicturePreview.launch()
            }
            else -> {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun observeUser() {
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user?.profileImage != null) {
                binding.profileImage.setPadding(0, 0, 0, 0)
                binding.profileImage.setImageBitmap(user.profileImage)
                binding.profileImage.imageTintList = null
            } else {
                binding.profileImage.setPadding(20, 20, 20, 20)
                binding.profileImage.setImageResource(android.R.drawable.ic_menu_camera)
            }
        }
    }

    private fun setupOrderHistory() {
        binding.orderHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        viewModel.orders.observe(viewLifecycleOwner) { orders ->
            binding.orderHistoryRecyclerView.adapter = OrderAdapter(orders)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
