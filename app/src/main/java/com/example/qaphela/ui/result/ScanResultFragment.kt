package com.example.qaphela.ui.result

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qaphela.R
import com.example.qaphela.databinding.FragmentScanResultBinding
import com.example.qaphela.domain.model.DetectedIndicator
import com.example.qaphela.domain.model.RiskLevel
import com.example.qaphela.domain.model.ScanFinding
import com.example.qaphela.domain.model.ScanType
import com.example.qaphela.ui.MainViewModel
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale

class ScanResultFragment : Fragment(), TextToSpeech.OnInitListener {

    private var _binding: FragmentScanResultBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var indicatorAdapter: IndicatorAdapter
    private var ttsEngine: TextToSpeech? = null
    private var ttsReady = false
    private var activeFinding: ScanFinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ttsEngine = TextToSpeech(requireContext(), this)

        indicatorAdapter = IndicatorAdapter()
        binding.rvDetectedIndicators.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = indicatorAdapter
        }

        val scanId = arguments?.getLong("scanId", 0L) ?: 0L
        if (scanId > 0L) {
            viewModel.loadHistoricalScanResult(scanId) { scan, result ->
                if (scan != null && result != null) {
                    val risk = try { RiskLevel.valueOf(scan.riskLevel) } catch (e: Exception) { RiskLevel.UNABLE }
                    val type = try { ScanType.valueOf(scan.scanType) } catch (e: Exception) { ScanType.SMS }
                    val finding = ScanFinding(
                        scanType = type,
                        content = scan.content,
                        riskLevel = risk,
                        indicators = listOf(
                            DetectedIndicator(
                                category = result.threatCategory,
                                title = result.threatCategory,
                                description = result.explanation,
                                isHighRisk = (risk == RiskLevel.HIGH_RISK)
                            )
                        ),
                        explanation = result.explanation,
                        recommendedAction = result.recommendedAction,
                        timestamp = scan.scanTime
                    )
                    displayFinding(finding)
                }
            }
        } else {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.currentScanFinding.collect { finding ->
                    finding?.let { displayFinding(it) }
                }
            }
        }

        binding.btnListenTts.setOnClickListener {
            speakFindingSummary()
        }

        binding.btnGetHelpFastResult.setOnClickListener {
            findNavController().navigate(R.id.action_scanResult_to_getHelpFast)
        }

        binding.btnReportSuspicious.setOnClickListener {
            findNavController().navigate(R.id.action_scanResult_to_report)
        }

        binding.btnScanAgain.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnReturnHome.setOnClickListener {
            findNavController().navigate(R.id.action_scanResult_to_home)
        }
    }

    private fun displayFinding(finding: ScanFinding) {
        activeFinding = finding
        binding.tvResultScanType.text = finding.scanType.name
        binding.tvResultTimestamp.text = DateFormat.format("dd MMM yyyy, HH:mm", Date(finding.timestamp))
        binding.tvResultScannedContent.text = finding.content
        binding.tvRecommendedAction.text = finding.recommendedAction

        val context = requireContext()
        val (titleText, headerColor, iconRes) = when (finding.riskLevel) {
            RiskLevel.HIGH_RISK -> Triple(
                getString(R.string.risk_high),
                ContextCompat.getColor(context, R.color.risk_high),
                R.drawable.ic_warning
            )
            RiskLevel.SUSPICIOUS -> Triple(
                getString(R.string.risk_suspicious),
                ContextCompat.getColor(context, R.color.risk_suspicious),
                R.drawable.ic_warning
            )
            RiskLevel.NO_INDICATORS -> Triple(
                getString(R.string.risk_no_indicators),
                ContextCompat.getColor(context, R.color.risk_low),
                R.drawable.ic_check_circle
            )
            RiskLevel.UNABLE -> Triple(
                getString(R.string.risk_unable),
                ContextCompat.getColor(context, R.color.risk_unknown),
                R.drawable.ic_info
            )
        }

        binding.cardRiskHeader.setCardBackgroundColor(headerColor)
        binding.imgRiskIcon.setImageResource(iconRes)
        binding.tvRiskLevelTitle.text = titleText
        binding.tvRiskExplanation.text = finding.explanation

        indicatorAdapter.submitList(finding.indicators)
    }

    private fun speakFindingSummary() {
        val finding = activeFinding ?: return
        if (!ttsReady) {
            Toast.makeText(requireContext(), "Text to speech engine initializing...", Toast.LENGTH_SHORT).show()
            return
        }

        val rate = viewModel.ttsSpeechRate.value
        ttsEngine?.setSpeechRate(rate)

        val speechText = "Scan Result: ${finding.riskLevel.name}. ${finding.explanation}. Recommended Action: ${finding.recommendedAction}"
        ttsEngine?.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, "QAPHELA_TTS_ID")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            ttsEngine?.language = Locale.ENGLISH
            ttsReady = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ttsEngine?.stop()
        ttsEngine?.shutdown()
        _binding = null
    }
}
