package com.example.qaphela.ui.scan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.qaphela.R
import com.example.qaphela.databinding.FragmentCheckLinkBinding
import com.example.qaphela.ui.MainViewModel

class CheckLinkFragment : Fragment() {

    private var _binding: FragmentCheckLinkBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckLinkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCheckLink.setOnClickListener {
            val urlContent = binding.etUrlContent.text?.toString()?.trim() ?: ""
            if (urlContent.isEmpty()) {
                binding.tilUrlInput.error = getString(R.string.error_empty_url)
            } else {
                binding.tilUrlInput.error = null
                viewModel.scanUrl(urlContent)
                findNavController().navigate(R.id.action_checkLink_to_scanResult)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
