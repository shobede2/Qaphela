package com.example.qaphela.ui.escalation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.qaphela.databinding.FragmentEmergencyPhysicalBinding

class EmergencyPhysicalFragment : Fragment() {

    private var _binding: FragmentEmergencyPhysicalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmergencyPhysicalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCallSaps.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:10111"))
            startActivity(intent)
        }

        binding.btnCall112.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"))
            startActivity(intent)
        }

        binding.btnSendDeafSms.setOnClickListener {
            val smsIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"))
            smsIntent.putExtra("sms_body", "EMERGENCY ALERT: I need emergency assistance. Situation: Physical Threat / Coercion. I am deaf/speech-impaired and cannot answer voice calls. Please send SMS or police assistance.")
            startActivity(smsIntent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
