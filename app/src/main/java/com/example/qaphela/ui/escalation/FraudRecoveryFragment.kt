package com.example.qaphela.ui.escalation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.qaphela.databinding.FragmentFraudRecoveryBinding
import com.example.qaphela.ui.MainViewModel
import java.util.Date

class FraudRecoveryFragment : Fragment() {

    private var _binding: FragmentFraudRecoveryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFraudRecoveryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activeFinding = viewModel.currentScanFinding.value
        val formattedSummary = buildString {
            append("=========================================\n")
            append("QAPHELA FRAUD INCIDENT SUMMARY\n")
            append("Generated: ").append(DateFormat.format("dd/MM/yyyy HH:mm", Date())).append("\n")
            append("=========================================\n")
            append("Scam Category: ").append(activeFinding?.scanType?.name ?: "SMS/URL Fraud").append("\n")
            append("Scanned Content: ").append(activeFinding?.content ?: "Suspicious Link/SMS").append("\n")
            append("Risk Finding: ").append(activeFinding?.riskLevel?.name ?: "HIGH_RISK").append("\n")
            append("Recommended Action: ").append(activeFinding?.recommendedAction ?: "Contact bank fraud helpline immediately.").append("\n")
            append("=========================================\n")
            append("Reported via Qaphela: Be Alert. Stay Safe.\n")
        }

        binding.tvIncidentSummaryBody.text = formattedSummary

        binding.btnCapitecCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:0860102043"))
            startActivity(intent)
        }

        binding.btnCapitecUssd.setOnClickListener {
            val ussdUri = Uri.parse("tel:" + Uri.encode("*120*3279#"))
            val intent = Intent(Intent.ACTION_DIAL, ussdUri)
            startActivity(intent)
        }

        binding.btnFnbCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:0875759406"))
            startActivity(intent)
        }

        binding.btnAbsaCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:0800111155"))
            startActivity(intent)
        }

        binding.btnCopyIncident.setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Qaphela Incident Summary", formattedSummary)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "Incident summary copied to clipboard!", Toast.LENGTH_SHORT).show()
        }

        binding.btnShareIncident.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Qaphela Incident Summary")
                putExtra(Intent.EXTRA_TEXT, formattedSummary)
            }
            startActivity(Intent.createChooser(shareIntent, "Share Incident Summary via"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
