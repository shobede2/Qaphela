package com.example.qaphela.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.qaphela.R
import com.example.qaphela.databinding.FragmentLoginBinding
import com.example.qaphela.ui.MainViewModel
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLoginSubmit.setOnClickListener {
            val email = binding.etLoginEmail.text?.toString()?.trim() ?: ""
            val password = binding.etLoginPassword.text?.toString() ?: ""

            if (email.isEmpty()) {
                binding.tilLoginEmail.error = "Please enter your email address."
                return@setOnClickListener
            } else {
                binding.tilLoginEmail.error = null
            }

            if (password.isEmpty()) {
                binding.tilLoginPassword.error = "Please enter your password."
                return@setOnClickListener
            } else {
                binding.tilLoginPassword.error = null
            }

            viewModel.loginUser(email, password)
        }

        binding.btnGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.btnLoginGuest.setOnClickListener {
            viewModel.setOnboardingCompleted(true)
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.authResult.collect { result ->
                result?.let {
                    if (it.isSuccess) {
                        val user = it.getOrNull()
                        Toast.makeText(requireContext(), "Welcome back, ${user?.fullName ?: "User"}!", Toast.LENGTH_SHORT).show()
                        viewModel.clearAuthResult()
                        viewModel.setOnboardingCompleted(true)
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    } else {
                        Toast.makeText(requireContext(), it.exceptionOrNull()?.message ?: "Login failed.", Toast.LENGTH_LONG).show()
                        viewModel.clearAuthResult()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
