package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "enabled" else "disabled"
            Toast.makeText(requireContext(), "Notifications $status", Toast.LENGTH_SHORT).show()
        }

        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            val mode = if (isChecked) "Dark Mode" else "Light Mode"
            Toast.makeText(requireContext(), "$mode set", Toast.LENGTH_SHORT).show()
        }

        binding.languageCard.setOnClickListener {
            Toast.makeText(requireContext(), "Language selection coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
