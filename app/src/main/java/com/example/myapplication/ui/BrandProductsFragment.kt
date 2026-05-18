package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.adapter.ProductAdapter
import com.example.myapplication.data.DataRepository
import com.example.myapplication.databinding.FragmentBrandProductsBinding
import com.example.myapplication.viewmodel.MainViewModel

class BrandProductsFragment : Fragment() {

    private var _binding: FragmentBrandProductsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBrandProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val brandName = arguments?.getString("brandName") ?: ""
        val products = DataRepository.getProductsByBrand(brandName)

        val adapter = ProductAdapter(products) { product ->
            viewModel.addToCart(product)
            Toast.makeText(requireContext(), "${product.name} added to cart", Toast.LENGTH_SHORT).show()
        }

        binding.productsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.productsRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
