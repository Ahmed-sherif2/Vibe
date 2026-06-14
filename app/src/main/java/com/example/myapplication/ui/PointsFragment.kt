package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.myapplication.databinding.FragmentPointsBinding
import com.example.myapplication.viewmodel.MainViewModel
import java.util.Locale

class PointsFragment : Fragment() {
    private var _binding: FragmentPointsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPointsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        observePoints()
        
        binding.redeemPointsButton.setOnClickListener {
            val user = viewModel.currentUser.value
            if (user != null && user.points >= 100) {
                viewModel.redeemPoints(100) { success ->
                    if (success) {
                        Toast.makeText(requireContext(), "Success! $1.00 discount coupon sent to your email.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to redeem points.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "You need at least 100 points to redeem.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observePoints() {
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.pointsValue.text = String.format(Locale.getDefault(), "%,d", it.points)
                val discount = it.points / 100.0
                binding.discountValue.text = String.format(Locale.getDefault(), "≈ $%.2f discount", discount)
                
                // Disable button if not enough points
                binding.redeemPointsButton.isEnabled = it.points >= 100
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
