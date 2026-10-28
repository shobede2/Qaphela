package com.example.qaphela.ui

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.qaphela.data.local.AppDatabase
import com.example.qaphela.data.local.BlocklistEntity
import com.example.qaphela.data.local.ScanEntity
import com.example.qaphela.data.local.ScanResultEntity
import com.example.qaphela.data.local.UserEntity
import com.example.qaphela.data.local.UserPreferencesRepository
import com.example.qaphela.data.repository.BlocklistRepository
import com.example.qaphela.data.repository.ReportRepository
import com.example.qaphela.data.repository.ScanRepository
import com.example.qaphela.data.repository.UserRepository
import com.example.qaphela.domain.model.ChatMessage
import com.example.qaphela.domain.model.ScanFinding
import com.example.qaphela.domain.scanner.ChatbotEngine
import com.example.qaphela.utils.LocaleHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val userPrefs = UserPreferencesRepository(application)
    private val scanRepository = ScanRepository(db.scanDao(), db.blocklistDao(), userPrefs)
    private val blocklistRepository = BlocklistRepository(db.blocklistDao())
    private val reportRepository = ReportRepository(db.userReportDao())
    private val userRepository = UserRepository(db.userDao(), userPrefs)
    private val chatbotEngine = ChatbotEngine()

    val historyEnabled = userPrefs.historyEnabledFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        true
    )

    val onboardingCompleted = userPrefs.onboardingCompletedFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    val preferredLanguage = userPrefs.preferredLanguageFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "en"
    )

    val notificationsEnabled = userPrefs.notificationsEnabledFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        true
    )

    val elderModeEnabled = userPrefs.elderModeEnabledFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    val ttsSpeechRate = userPrefs.ttsSpeechRateFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        1.0f
    )

    val appThemeMode = userPrefs.appThemeModeFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "SYSTEM"
    )

    val loggedInUserId = userPrefs.loggedInUserIdFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        -1L
    )

    val loggedInUserName = userPrefs.loggedInUserNameFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "Guest"
    )

    val loggedInUserEmail = userPrefs.loggedInUserEmailFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "guest@qaphela.org.za"
    )

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "Hello! I am Qaphela's AI Scam Assistant. You can paste a suspicious link, SMS text, or ask me questions like 'Is this SASSA SMS real?' or 'I clicked a link, what do I do?'\n\nRemember: Qaphela provides advisory assistance and cannot guarantee 100% safety.",
                isUser = false
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _currentScanFinding = MutableStateFlow<ScanFinding?>(null)
    val currentScanFinding: StateFlow<ScanFinding?> = _currentScanFinding.asStateFlow()

    private val _currentScanId = MutableStateFlow<Long>(0L)
    val currentScanId: StateFlow<Long> = _currentScanId.asStateFlow()

    private val _reportResult = MutableStateFlow<Result<Long>?>(null)
    val reportResult: StateFlow<Result<Long>?> = _reportResult.asStateFlow()

    private val _authResult = MutableStateFlow<Result<UserEntity>?>(null)
    val authResult: StateFlow<Result<UserEntity>?> = _authResult.asStateFlow()

    private val _profileUpdateResult = MutableStateFlow<Result<Unit>?>(null)
    val profileUpdateResult: StateFlow<Result<Unit>?> = _profileUpdateResult.asStateFlow()

    val allScans: StateFlow<List<ScanEntity>> = scanRepository.getAllScans().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val recentScans: StateFlow<List<ScanEntity>> = scanRepository.getRecentScans(5).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _searchQuery = MutableStateFlow("")
    val searchBlocklistQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val blocklistItems: StateFlow<List<BlocklistEntity>> = _searchQuery
        .flatMapLatest { query ->
            blocklistRepository.searchBlocklist(query)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun sendChatMessage(userText: String) {
        val trimmed = userText.trim()
        if (trimmed.isEmpty()) return

        val userMsg = ChatMessage(text = trimmed, isUser = true)
        val currentList = _chatMessages.value.toMutableList()
        currentList.add(userMsg)
        _chatMessages.value = currentList

        viewModelScope.launch {
            val responseText = chatbotEngine.generateResponse(trimmed, blocklistItems.value)
            val aiMsg = ChatMessage(text = responseText, isUser = false)
            val updatedList = _chatMessages.value.toMutableList()
            updatedList.add(aiMsg)
            _chatMessages.value = updatedList
        }
    }

    fun registerUser(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            val res = userRepository.registerUser(fullName, email, password)
            _authResult.value = res
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            val res = userRepository.loginUser(email, password)
            _authResult.value = res
        }
    }

    fun updateProfile(fullName: String, email: String) {
        viewModelScope.launch {
            val res = userRepository.updateProfile(fullName, email)
            _profileUpdateResult.value = res
        }
    }

    fun clearProfileUpdateResult() {
        _profileUpdateResult.value = null
    }

    fun logoutUser() {
        viewModelScope.launch {
            userRepository.logoutUser()
        }
    }

    fun clearAuthResult() {
        _authResult.value = null
    }

    fun scanSms(messageText: String) {
        viewModelScope.launch {
            val (finding, scanId) = scanRepository.scanSms(messageText)
            _currentScanFinding.value = finding
            _currentScanId.value = scanId
        }
    }

    fun scanUrl(urlInput: String) {
        viewModelScope.launch {
            val (finding, scanId) = scanRepository.scanUrl(urlInput)
            _currentScanFinding.value = finding
            _currentScanId.value = scanId
        }
    }

    fun loadHistoricalScanResult(scanId: Long, onResultLoaded: (ScanEntity?, ScanResultEntity?) -> Unit) {
        viewModelScope.launch {
            val scan = scanRepository.getScanById(scanId)
            val result = scanRepository.getScanResultByScanId(scanId)
            onResultLoaded(scan, result)
        }
    }

    fun submitReport(indicator: String, description: String?) {
        viewModelScope.launch {
            val res = reportRepository.submitReport(indicator, description)
            _reportResult.value = res
        }
    }

    fun clearReportResult() {
        _reportResult.value = null
    }

    fun searchBlocklist(query: String) {
        _searchQuery.value = query
    }

    fun deleteScan(scanId: Long) {
        viewModelScope.launch {
            scanRepository.deleteScan(scanId)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            scanRepository.clearAllHistory()
        }
    }

    fun setHistoryEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPrefs.setHistoryEnabled(enabled)
        }
    }

    fun setOnboardingCompleted(completed: Boolean) {
        viewModelScope.launch {
            userPrefs.setOnboardingCompleted(completed)
        }
    }

    fun setPreferredLanguage(languageCode: String) {
        viewModelScope.launch {
            userPrefs.setPreferredLanguage(languageCode)
            LocaleHelper.applyLocale(languageCode)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPrefs.setNotificationsEnabled(enabled)
        }
    }

    fun setElderModeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPrefs.setElderModeEnabled(enabled)
        }
    }

    fun setTtsSpeechRate(rate: Float) {
        viewModelScope.launch {
            userPrefs.setTtsSpeechRate(rate)
        }
    }

    fun setAppThemeMode(themeMode: String) {
        viewModelScope.launch {
            userPrefs.setAppThemeMode(themeMode)
            when (themeMode) {
                "DARK" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                "LIGHT" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }
}
