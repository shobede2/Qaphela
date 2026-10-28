package com.example.qaphela.utils

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LocaleHelper {

    fun applyLocale(languageCode: String) {
        val appLocales = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocales)
    }

    fun getCurrentLanguageCode(): String {
        val locales = AppCompatDelegate.getApplicationLocales()
        return if (!locales.isEmpty) {
            locales.get(0)?.language ?: "en"
        } else {
            "en"
        }
    }
}
