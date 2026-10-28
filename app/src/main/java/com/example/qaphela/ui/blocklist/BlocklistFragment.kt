package com.example.qaphela.ui.blocklist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qaphela.databinding.FragmentBlocklistBinding
import com.example.qaphela.ui.MainViewModel
import kotlinx.coroutines.launch

class BlocklistFragment : Fragment() {

    private var _binding: FragmentBlocklistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var blocklistAdapter: BlocklistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBlocklistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        blocklistAdapter = BlocklistAdapter()
        binding.rvBlocklist.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = blocklistAdapter
        }

        binding.etBlocklistSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchBlocklist(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.blocklistItems.collect { items ->
                    if (items.isEmpty()) {
                        binding.tvBlocklistEmpty.visibility = View.VISIBLE
                        binding.rvBlocklist.visibility = View.GONE
                    } else {
                        binding.tvBlocklistEmpty.visibility = View.GONE
                        binding.rvBlocklist.visibility = View.VISIBLE
                        blocklistAdapter.submitList(items)
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
