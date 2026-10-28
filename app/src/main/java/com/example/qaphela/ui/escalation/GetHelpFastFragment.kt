package com.example.qaphela.ui.escalation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.qaphela.R
import com.example.qaphela.databinding.FragmentGetHelpFastBinding

class GetHelpFastFragment : Fragment() {

    private var _binding: FragmentGetHelpFastBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGetHelpFastBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardPhysicalDanger.setOnClickListener {
            findNavController().navigate(R.id.action_getHelpFast_to_emergencyPhysical)
        }

        binding.cardFinancialFraud.setOnClickListener {
            findNavController().navigate(R.id.action_getHelpFast_to_fraudRecovery)
        }

        binding.cardWarnCommunity.setOnClickListener {
            findNavController().navigate(R.id.action_getHelpFast_to_communityWarning)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
