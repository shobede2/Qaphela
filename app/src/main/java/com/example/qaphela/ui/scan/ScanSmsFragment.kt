package com.example.qaphela.ui.scan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.qaphela.R
import com.example.qaphela.databinding.FragmentScanSmsBinding
import com.example.qaphela.ui.MainViewModel

class ScanSmsFragment : Fragment() {

    private var _binding: FragmentScanSmsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanSmsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnClearSms.setOnClickListener {
            binding.etSmsContent.setText("")
            binding.tilSmsInput.error = null
        }

        binding.btnScanSms.setOnClickListener {
            val content = binding.etSmsContent.text?.toString()?.trim() ?: ""
            if (content.isEmpty()) {
                binding.tilSmsInput.error = getString(R.string.error_empty_sms)
            } else {
                binding.tilSmsInput.error = null
                viewModel.scanSms(content)
                findNavController().navigate(R.id.action_scanSms_to_scanResult)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
