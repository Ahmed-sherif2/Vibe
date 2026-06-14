package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startAnimations()

        binding.getStartedButton.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
        }
    }

    private fun startAnimations() {
        // Load the new entrance animation for the VIBE logo
        val vibeEntrance = AnimationUtils.loadAnimation(requireContext(), R.anim.vibe_entrance)
        val pulseAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.pulse_animation)
        
        // Listen for the end of the entrance animation to start the continuous pulse
        vibeEntrance.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                binding.welcomeLogo.startAnimation(pulseAnim)
            }
        })

        // Apply entrance to the logo
        binding.welcomeLogo.startAnimation(vibeEntrance)

        // Fade in the tagline slightly after
        val taglineFade = AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_in)
        taglineFade.duration = 1000
        taglineFade.startOffset = 800
        binding.welcomeTagline.startAnimation(taglineFade)

        // Floating animations for background circles
        val floatingAnim1 = AnimationUtils.loadAnimation(requireContext(), R.anim.float_animation)
        binding.bgCircle1.startAnimation(floatingAnim1)

        val floatingAnim2 = AnimationUtils.loadAnimation(requireContext(), R.anim.float_animation_reverse)
        binding.bgCircle2.startAnimation(floatingAnim2)
        
        // Button entrance (slides up from bottom)
        val buttonSlideUp = AnimationUtils.loadAnimation(requireContext(), androidx.appcompat.R.anim.abc_slide_in_bottom)
        buttonSlideUp.startOffset = 1500
        buttonSlideUp.duration = 1000
        binding.getStartedButton.startAnimation(buttonSlideUp)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
