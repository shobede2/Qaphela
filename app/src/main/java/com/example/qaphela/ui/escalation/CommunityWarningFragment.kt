package com.example.qaphela.ui.escalation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.qaphela.databinding.FragmentCommunityWarningBinding

class CommunityWarningFragment : Fragment() {

    private var _binding: FragmentCommunityWarningBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommunityWarningBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnShareCommunity.setOnClickListener {
            val area = binding.etCommunityArea.text?.toString()?.trim() ?: "Gauteng"
            val desc = binding.etCommunityDesc.text?.toString()?.trim() ?: ""

            if (desc.isEmpty()) {
                binding.tilCommunityDesc.error = "Please enter a short description of the scam."
                return@setOnClickListener
            } else {
                binding.tilCommunityDesc.error = null
            }

            // Automated PII Scrubber: Redacts phone numbers and emails
            val piiPhoneRegex = Regex("(?:\\+27|0)\\d{9}")
            val piiEmailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
            val cleansedDesc = desc.replace(piiPhoneRegex, "[REDACTED NUMBER]")
                .replace(piiEmailRegex, "[REDACTED EMAIL]")

            val warningMessage = buildString {
                append("📢 QAPHELA COMMUNITY SCAM WARNING [").append(area).append("]:\n")
                append(cleansedDesc).append("\n\n")
                append("DO NOT click links or pay unexpected fees. Stay alert! #QaphelaScamAlert")
            }

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Community Scam Warning")
                putExtra(Intent.EXTRA_TEXT, warningMessage)
            }
            startActivity(Intent.createChooser(shareIntent, "Share Community Warning via"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
