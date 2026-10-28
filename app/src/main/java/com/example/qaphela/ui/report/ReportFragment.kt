package com.example.qaphela.ui.report

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
import com.example.qaphela.databinding.FragmentReportBinding
import com.example.qaphela.ui.MainViewModel
import kotlinx.coroutines.launch

class ReportFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSubmitReport.setOnClickListener {
            val indicator = binding.etReportIndicator.text?.toString()?.trim() ?: ""
            val description = binding.etReportDesc.text?.toString()?.trim()

            if (indicator.isEmpty()) {
                binding.tilReportIndicator.error = getString(R.string.error_empty_report)
            } else {
                binding.tilReportIndicator.error = null
                viewModel.submitReport(indicator, description)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.reportResult.collect { result ->
                result?.let {
                    if (it.isSuccess) {
                        Toast.makeText(requireContext(), R.string.report_success_message, Toast.LENGTH_LONG).show()
                        viewModel.clearReportResult()
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(requireContext(), it.exceptionOrNull()?.message ?: "Submission error", Toast.LENGTH_SHORT).show()
                        viewModel.clearReportResult()
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
