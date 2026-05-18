package com.example.myapplication.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentAccountBinding
import com.example.myapplication.viewmodel.MainViewModel

class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    private val takePicturePreview = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        if (bitmap != null) {
            viewModel.updateProfileImage(bitmap)
            Toast.makeText(requireContext(), "Uploading profile picture...", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            takePicturePreview.launch()
        } else {
            Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUser()

        binding.editProfileButton.setOnClickListener {
            checkCameraPermission()
        }

        binding.myOrdersButton.setOnClickListener {
            // Updated to go to home/orders if needed
            findNavController().navigate(R.id.homeFragment)
        }

        binding.logoutButton.setOnClickListener {
            viewModel.logout()
            findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun observeUser() {
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.userName.text = it.name
                binding.userEmail.text = it.email
                
                if (!it.profileImageUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(it.profileImageUrl)
                        .placeholder(android.R.drawable.ic_menu_myplaces)
                        .error(android.R.drawable.ic_menu_myplaces)
                        .into(binding.profileImage)
                } else if (it.profileImage != null) {
                    binding.profileImage.setImageBitmap(it.profileImage)
                }
            }
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
