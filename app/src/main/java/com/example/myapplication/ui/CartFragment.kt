package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.CartAdapter
import com.example.myapplication.databinding.FragmentCartBinding
import com.example.myapplication.viewmodel.MainViewModel

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.cartItems.observe(viewLifecycleOwner) { items ->
            val adapter = CartAdapter(items) { product ->
                viewModel.removeFromCart(product)
            }
            binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.cartRecyclerView.adapter = adapter
            
            binding.totalPriceTextView.text = "$${String.format("%.2f", viewModel.getTotalPrice())}"
        }

        binding.checkoutButton.setOnClickListener {
            if (viewModel.cartItems.value.isNullOrEmpty()) {
                android.widget.Toast.makeText(requireContext(), "Cart is empty", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                findNavController().navigate(R.id.action_cartFragment_to_checkoutFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
