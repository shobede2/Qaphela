package com.example.qaphela.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.qaphela.R
import com.example.qaphela.databinding.FragmentOnboardingBinding
import com.example.qaphela.ui.MainViewModel

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGetStarted.setOnClickListener {
            findNavController().navigate(R.id.action_onboarding_to_register)
        }

        binding.btnOnboardingLogin.setOnClickListener {
            findNavController().navigate(R.id.action_onboarding_to_login)
        }

        binding.btnContinueGuest.setOnClickListener {
            viewModel.setOnboardingCompleted(true)
            findNavController().navigate(R.id.action_onboarding_to_home)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
