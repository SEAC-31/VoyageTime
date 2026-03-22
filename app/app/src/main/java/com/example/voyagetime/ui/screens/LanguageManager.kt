package com.example.voyagetime.ui.screens

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import java.util.Locale

/**
 * Manages the app language preference using SharedPreferences.
 * Stores the selected locale code ("en", "es", "ca") and provides
 * a helper to apply it when the app starts or the user changes the language.
 */
object LanguageManager {

    private const val PREFS_NAME = "voyagetime_prefs"
    private const val KEY_LANGUAGE = "app_language"
    const val LANG_EN = "en"
    const val LANG_ES = "es"
    const val LANG_CA = "ca"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** Returns the saved language code, defaulting to system locale if never set. */
    fun getSavedLanguage(context: Context): String {
        val systemLang = Locale.getDefault().language
        val default = if (systemLang in listOf(LANG_EN, LANG_ES, LANG_CA)) systemLang else LANG_EN
        return prefs(context).getString(KEY_LANGUAGE, default) ?: default
    }

    /** Persists the chosen language code. */
    fun saveLanguage(context: Context, langCode: String) {
        prefs(context).edit().putString(KEY_LANGUAGE, langCode).apply()
    }

    /**
     * Applies the saved locale to the given context.
     * Call this from MainActivity.attachBaseContext() and after saving a new language.
     */
    fun applyLanguage(context: Context): Context {
        val langCode = getSavedLanguage(context)
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            context
        }
    }
}