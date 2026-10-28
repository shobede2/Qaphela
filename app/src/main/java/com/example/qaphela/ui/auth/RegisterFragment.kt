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
import com.example.qaphela.databinding.FragmentRegisterBinding
import com.example.qaphela.ui.MainViewModel
import com.example.qaphela.utils.PasswordHasher
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegisterSubmit.setOnClickListener {
            val name = binding.etRegisterName.text?.toString()?.trim() ?: ""
            val email = binding.etRegisterEmail.text?.toString()?.trim() ?: ""
            val password = binding.etRegisterPassword.text?.toString() ?: ""
            val confirmPassword = binding.etRegisterConfirmPassword.text?.toString() ?: ""

            if (name.isEmpty()) {
                binding.tilRegisterName.error = "Please enter your full name."
                return@setOnClickListener
            } else {
                binding.tilRegisterName.error = null
            }

            if (email.isEmpty()) {
                binding.tilRegisterEmail.error = "Please enter your email address."
                return@setOnClickListener
            } else {
                binding.tilRegisterEmail.error = null
            }

            val (isValidPassword, passwordError) = PasswordHasher.validatePasswordComplexity(password)
            if (!isValidPassword) {
                binding.tilRegisterPassword.error = passwordError
                return@setOnClickListener
            } else {
                binding.tilRegisterPassword.error = null
            }

            if (password != confirmPassword) {
                binding.tilRegisterConfirmPassword.error = "Passwords do not match."
                return@setOnClickListener
            } else {
                binding.tilRegisterConfirmPassword.error = null
            }

            viewModel.registerUser(name, email, password)
        }

        binding.btnGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.btnRegisterGuest.setOnClickListener {
            viewModel.setOnboardingCompleted(true)
            findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.authResult.collect { result ->
                result?.let {
                    if (it.isSuccess) {
                        val user = it.getOrNull()
                        Toast.makeText(requireContext(), "Account created! Welcome, ${user?.fullName}!", Toast.LENGTH_LONG).show()
                        viewModel.clearAuthResult()
                        viewModel.setOnboardingCompleted(true)
                        findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                    } else {
                        Toast.makeText(requireContext(), it.exceptionOrNull()?.message ?: "Registration failed.", Toast.LENGTH_LONG).show()
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
