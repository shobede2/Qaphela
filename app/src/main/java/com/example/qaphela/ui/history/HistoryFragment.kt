package com.example.qaphela.ui.history

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qaphela.R
import com.example.qaphela.databinding.FragmentHistoryBinding
import com.example.qaphela.ui.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter(
            onItemClick = { scan ->
                val bundle = Bundle().apply { putLong("scanId", scan.scanId) }
                findNavController().navigate(R.id.action_history_to_scanResult, bundle)
            },
            onDeleteClick = { scan ->
                viewModel.deleteScan(scan.scanId)
                Toast.makeText(requireContext(), R.string.history_item_deleted, Toast.LENGTH_SHORT).show()
            }
        )

        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }

    private fun setupListeners() {
        binding.btnClearAllHistory.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.history_clear_confirm_title)
                .setMessage(R.string.history_clear_confirm_message)
                .setNegativeButton(R.string.btn_cancel, null)
                .setPositiveButton(R.string.btn_delete) { _, _ ->
                    viewModel.clearAllHistory()
                }
                .show()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allScans.collect { scans ->
                    if (scans.isEmpty()) {
                        binding.tvHistoryEmpty.visibility = View.VISIBLE
                        binding.rvHistory.visibility = View.GONE
                    } else {
                        binding.tvHistoryEmpty.visibility = View.GONE
                        binding.rvHistory.visibility = View.VISIBLE
                        historyAdapter.submitList(scans)
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
