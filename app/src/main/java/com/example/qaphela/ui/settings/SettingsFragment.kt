package com.example.qaphela.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.qaphela.R
import com.example.qaphela.databinding.FragmentSettingsBinding
import com.example.qaphela.ui.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeSettings()
        setupListeners()
    }

    private fun observeSettings() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.loggedInUserName.collect { userName ->
                        binding.etProfileName.setText(userName)
                        updateAccountUi()
                    }
                }

                launch {
                    viewModel.loggedInUserEmail.collect { email ->
                        binding.etProfileEmail.setText(email)
                        updateAccountUi()
                    }
                }

                launch {
                    viewModel.appThemeMode.collect { mode ->
                        when (mode) {
                            "DARK" -> binding.rbThemeDark.isChecked = true
                            "LIGHT" -> binding.rbThemeLight.isChecked = true
                            else -> binding.rbThemeSystem.isChecked = true
                        }
                    }
                }

                launch {
                    viewModel.preferredLanguage.collect { langCode ->
                        when (langCode) {
                            "zu" -> binding.rbLangIsizulu.isChecked = true
                            "xh" -> binding.rbLangIsixhosa.isChecked = true
                            "af" -> binding.rbLangAfrikaans.isChecked = true
                            else -> binding.rbLangEnglish.isChecked = true
                        }
                    }
                }

                launch {
                    viewModel.ttsSpeechRate.collect { rate ->
                        binding.sliderSpeechRate.value = rate
                        val labelText = when {
                            rate <= 0.75f -> "Speed: Slow (${rate}x)"
                            rate >= 1.25f -> "Speed: Fast (${rate}x)"
                            else -> "Speed: Normal (${rate}x)"
                        }
                        binding.tvSpeechRateValue.text = labelText
                    }
                }

                launch {
                    viewModel.elderModeEnabled.collect { enabled ->
                        binding.switchElderMode.isChecked = enabled
                    }
                }

                launch {
                    viewModel.historyEnabled.collect { enabled ->
                        binding.switchHistory.isChecked = enabled
                    }
                }

                launch {
                    viewModel.notificationsEnabled.collect { enabled ->
                        binding.switchNotifications.isChecked = enabled
                    }
                }
            }
        }
    }

    private fun updateAccountUi() {
        val userId = viewModel.loggedInUserId.value
        if (userId > 0) {
            binding.btnAccountAction.text = "Log Out"
        } else {
            binding.btnAccountAction.text = "Log In / Register"
        }
    }

    private fun setupListeners() {
        binding.btnSaveProfile.setOnClickListener {
            val newName = binding.etProfileName.text?.toString()?.trim() ?: ""
            val newEmail = binding.etProfileEmail.text?.toString()?.trim() ?: ""
            viewModel.updateProfile(newName, newEmail)
            Toast.makeText(requireContext(), "Profile changes saved!", Toast.LENGTH_SHORT).show()
        }

        binding.btnAccountAction.setOnClickListener {
            if (viewModel.loggedInUserId.value > 0) {
                viewModel.logoutUser()
                Toast.makeText(requireContext(), "Logged out successfully.", Toast.LENGTH_SHORT).show()
            } else {
                findNavController().navigate(R.id.action_settings_to_login)
            }
        }

        binding.rgThemeMode.setOnCheckedChangeListener { _, checkedId ->
            val mode = when (checkedId) {
                R.id.rb_theme_dark -> "DARK"
                R.id.rb_theme_light -> "LIGHT"
                else -> "SYSTEM"
            }
            viewModel.setAppThemeMode(mode)
        }

        binding.rgLanguage.setOnCheckedChangeListener { _, checkedId ->
            val langCode = when (checkedId) {
                R.id.rb_lang_isizulu -> "zu"
                R.id.rb_lang_isixhosa -> "xh"
                R.id.rb_lang_afrikaans -> "af"
                else -> "en"
            }
            viewModel.setPreferredLanguage(langCode)
        }

        binding.sliderSpeechRate.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                viewModel.setTtsSpeechRate(value)
            }
        }

        binding.switchElderMode.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setElderModeEnabled(isChecked)
        }

        binding.switchHistory.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setHistoryEnabled(isChecked)
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setNotificationsEnabled(isChecked)
        }

        binding.tvLinkPrivacy.setOnClickListener {
            findNavController().navigate(R.id.action_settings_to_privacy)
        }

        binding.tvLinkHelp.setOnClickListener {
            findNavController().navigate(R.id.action_settings_to_help)
        }

        binding.tvLinkAbout.setOnClickListener {
            findNavController().navigate(R.id.action_settings_to_about)
        }

        binding.btnClearLocalData.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.settings_clear_data)
                .setMessage(R.string.history_clear_confirm_message)
                .setNegativeButton(R.string.btn_cancel, null)
                .setPositiveButton(R.string.btn_delete) { _, _ ->
                    viewModel.clearAllHistory()
                    Toast.makeText(requireContext(), R.string.settings_clear_data_success, Toast.LENGTH_SHORT).show()
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
