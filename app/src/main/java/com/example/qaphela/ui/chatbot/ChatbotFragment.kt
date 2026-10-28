package com.example.qaphela.ui.chatbot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qaphela.databinding.FragmentChatbotBinding
import com.example.qaphela.ui.MainViewModel
import kotlinx.coroutines.launch

class ChatbotFragment : Fragment() {

    private var _binding: FragmentChatbotBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatbotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        observeChatMessages()
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter()
        binding.rvChatMessages.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
        }
    }

    private fun setupListeners() {
        binding.btnSendChat.setOnClickListener {
            val text = binding.etChatInput.text?.toString()?.trim() ?: ""
            if (text.isNotEmpty()) {
                binding.etChatInput.setText("")
                viewModel.sendChatMessage(text)
            }
        }
    }

    private fun observeChatMessages() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.chatMessages.collect { messages ->
                    chatAdapter.submitList(messages) {
                        if (messages.isNotEmpty()) {
                            binding.rvChatMessages.smoothScrollToPosition(messages.size - 1)
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
