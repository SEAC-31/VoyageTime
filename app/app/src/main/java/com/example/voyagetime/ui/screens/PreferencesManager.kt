package com.example.voyagetime.ui.screens

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

/**
 * Centralized manager for all persisted user preferences.
 * Uses SharedPreferences with a dedicated file so it does not
 * conflict with LanguageManager (which uses "voyagetime_prefs").
 *
 * Stored keys:
 *   - username        (String)
 *   - date_of_birth   (String, free text)
 *   - dark_mode       (Boolean)
 *   - app_language    → managed by LanguageManager, mirrored here for reading
 *   - currency        (String: "EUR", "USD", "GBP", "JPY")
 *   - show_prices     (Boolean)
 */
object PreferencesManager {

    private const val TAG = "PreferencesManager"
    private const val PREFS_FILE = "voyagetime_user_prefs"

    // Keys
    const val KEY_USERNAME = "username"
    const val KEY_DATE_OF_BIRTH = "date_of_birth"
    const val KEY_DARK_MODE = "dark_mode"
    const val KEY_CURRENCY = "currency"
    const val KEY_SHOW_PRICES = "show_prices"

    // Defaults
    const val DEFAULT_USERNAME = ""
    const val DEFAULT_DATE_OF_BIRTH = ""
    const val DEFAULT_CURRENCY = "EUR"
    const val DEFAULT_SHOW_PRICES = true
    const val DEFAULT_DARK_MODE = false

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)

    // ── DARK MODE ─────────────────────────────────────────────
    fun getDarkMode(context: Context): Boolean {
        val value = prefs(context).getBoolean(KEY_DARK_MODE, DEFAULT_DARK_MODE)
        Log.d(TAG, "getDarkMode -> $value")
        return value
    }

    fun saveDarkMode(context: Context, enabled: Boolean) {
        Log.i(TAG, "Dark mode set to: $enabled")
        prefs(context).edit().putBoolean(KEY_DARK_MODE, enabled).commit() // commit() not apply() — synchronous
    }

    // ── USERNAME ──────────────────────────────────────────────
    fun getUsername(context: Context): String =
        prefs(context).getString(KEY_USERNAME, DEFAULT_USERNAME) ?: DEFAULT_USERNAME

    fun saveUsername(context: Context, value: String) {
        Log.i(TAG, "Username saved: $value")
        prefs(context).edit().putString(KEY_USERNAME, value).apply()
    }

    // ── DATE OF BIRTH ─────────────────────────────────────────
    fun getDateOfBirth(context: Context): String =
        prefs(context).getString(KEY_DATE_OF_BIRTH, DEFAULT_DATE_OF_BIRTH) ?: DEFAULT_DATE_OF_BIRTH

    fun saveDateOfBirth(context: Context, value: String) {
        Log.i(TAG, "Date of birth saved: $value")
        prefs(context).edit().putString(KEY_DATE_OF_BIRTH, value).apply()
    }

    // ── CURRENCY ──────────────────────────────────────────────
    fun getCurrency(context: Context): String =
        prefs(context).getString(KEY_CURRENCY, DEFAULT_CURRENCY) ?: DEFAULT_CURRENCY

    fun saveCurrency(context: Context, value: String) {
        Log.i(TAG, "Currency saved: $value")
        prefs(context).edit().putString(KEY_CURRENCY, value).apply()
    }

    // ── SHOW PRICES ───────────────────────────────────────────
    fun getShowPrices(context: Context): Boolean =
        prefs(context).getBoolean(KEY_SHOW_PRICES, DEFAULT_SHOW_PRICES)

    fun saveShowPrices(context: Context, value: Boolean) {
        Log.i(TAG, "Show prices set to: $value")
        prefs(context).edit().putBoolean(KEY_SHOW_PRICES, value).apply()
    }
}