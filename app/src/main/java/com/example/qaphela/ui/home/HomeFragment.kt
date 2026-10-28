package com.example.qaphela.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qaphela.R
import com.example.qaphela.databinding.FragmentHomeBinding
import com.example.qaphela.ui.MainViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var recentAdapter: RecentScanAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        recentAdapter = RecentScanAdapter { scan ->
            val bundle = Bundle().apply {
                putLong("scanId", scan.scanId)
            }
            findNavController().navigate(R.id.scanResultFragment, bundle)
        }

        binding.rvRecentScans.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recentAdapter
        }
    }

    private fun setupClickListeners() {
        binding.cardGetHelpFastHome.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_getHelpFast)
        }

        binding.cardAskChatbot.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_chatbot)
        }

        binding.cardScanSms.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_scanSms)
        }

        binding.cardCheckLink.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_checkLink)
        }

        binding.cardHistory.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_history)
        }

        binding.cardBlocklist.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_blocklist)
        }

        binding.btnReportScamHome.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_report)
        }

        binding.btnHelpHome.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_help)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.loggedInUserName.collect { name ->
                        binding.tvGreeting.text = "Hello, $name!"
                    }
                }

                launch {
                    viewModel.recentScans.collect { scans ->
                        if (scans.isEmpty()) {
                            binding.tvNoRecentActivity.visibility = View.VISIBLE
                            binding.rvRecentScans.visibility = View.GONE
                        } else {
                            binding.tvNoRecentActivity.visibility = View.GONE
                            binding.rvRecentScans.visibility = View.VISIBLE
                            recentAdapter.submitList(scans)
                        }
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
