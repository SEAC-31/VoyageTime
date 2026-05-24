package com.example.voyagetime.ui.screens

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object PreferencesManager {

    private const val TAG = "PreferencesManager"
    private const val PREFS_FILE = "voyagetime_user_prefs"

    const val KEY_USERNAME = "username"
    const val KEY_DATE_OF_BIRTH = "date_of_birth"
    const val KEY_DARK_MODE = "dark_mode"
    const val KEY_CURRENCY = "currency"
    const val KEY_SHOW_PRICES = "show_prices"
    const val KEY_TERMS_ACCEPTED = "terms_accepted"
    private const val KEY_GALLERY_URIS = "gallery_uris"
    private const val KEY_REMEMBER_LOGIN = "remember_login"
    private const val KEY_REMEMBERED_EMAIL = "remembered_email"

    const val DEFAULT_USERNAME = ""
    const val DEFAULT_DATE_OF_BIRTH = ""
    const val DEFAULT_CURRENCY = "EUR"
    const val DEFAULT_SHOW_PRICES = true
    const val DEFAULT_DARK_MODE = false
    const val DEFAULT_TERMS_ACCEPTED = false

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)

    // ── Terms ──────────────────────────────────────────────────────────────

    fun hasAcceptedTerms(context: Context): Boolean =
        prefs(context).getBoolean(KEY_TERMS_ACCEPTED, DEFAULT_TERMS_ACCEPTED)

    fun saveTermsAccepted(context: Context, accepted: Boolean) {
        Log.i(TAG, "Terms accepted set to: $accepted")
        prefs(context).edit().putBoolean(KEY_TERMS_ACCEPTED, accepted).commit()
    }

    // ── Dark mode ──────────────────────────────────────────────────────────

    fun getDarkMode(context: Context): Boolean {
        val value = prefs(context).getBoolean(KEY_DARK_MODE, DEFAULT_DARK_MODE)
        Log.d(TAG, "getDarkMode -> $value")
        return value
    }

    fun saveDarkMode(context: Context, enabled: Boolean) {
        Log.i(TAG, "Dark mode set to: $enabled")
        prefs(context).edit().putBoolean(KEY_DARK_MODE, enabled).commit()
    }

    // ── Profile ────────────────────────────────────────────────────────────

    fun getUsername(context: Context): String =
        prefs(context).getString(KEY_USERNAME, DEFAULT_USERNAME) ?: DEFAULT_USERNAME

    fun saveUsername(context: Context, value: String) {
        Log.i(TAG, "Username saved: $value")
        prefs(context).edit().putString(KEY_USERNAME, value).apply()
    }

    fun getDateOfBirth(context: Context): String =
        prefs(context).getString(KEY_DATE_OF_BIRTH, DEFAULT_DATE_OF_BIRTH) ?: DEFAULT_DATE_OF_BIRTH

    fun saveDateOfBirth(context: Context, value: String) {
        Log.i(TAG, "Date of birth saved: $value")
        prefs(context).edit().putString(KEY_DATE_OF_BIRTH, value).apply()
    }

    // ── Currency & prices ──────────────────────────────────────────────────

    fun getCurrency(context: Context): String =
        prefs(context).getString(KEY_CURRENCY, DEFAULT_CURRENCY) ?: DEFAULT_CURRENCY

    fun saveCurrency(context: Context, value: String) {
        Log.i(TAG, "Currency saved: $value")
        prefs(context).edit().putString(KEY_CURRENCY, value).apply()
    }

    fun getShowPrices(context: Context): Boolean =
        prefs(context).getBoolean(KEY_SHOW_PRICES, DEFAULT_SHOW_PRICES)

    fun saveShowPrices(context: Context, value: Boolean) {
        Log.i(TAG, "Show prices set to: $value")
        prefs(context).edit().putBoolean(KEY_SHOW_PRICES, value).apply()
    }

    // ── Gallery global (T3.1 / T3.2 — Sharon) ─────────────────────────────

    fun getGalleryImageUris(context: Context): List<String> {
        val raw = prefs(context).getString(KEY_GALLERY_URIS, "") ?: ""
        return if (raw.isBlank()) emptyList() else raw.split("|||")
    }

    fun addGalleryImageUri(context: Context, uri: String) {
        val current = getGalleryImageUris(context).toMutableList()
        if (!current.contains(uri)) {
            current.add(uri)
            prefs(context).edit().putString(KEY_GALLERY_URIS, current.joinToString("|||")).apply()
            Log.i(TAG, "Gallery URI added: $uri")
        }
    }

    fun removeGalleryImageUri(context: Context, uri: String) {
        val current = getGalleryImageUris(context).toMutableList()
        current.remove(uri)
        prefs(context).edit().putString(KEY_GALLERY_URIS, current.joinToString("|||")).apply()
        Log.i(TAG, "Gallery URI removed: $uri")
    }

    // ── Gallery per-trip (T3.3 — Joan) ────────────────────────────────────

    private fun tripGalleryKey(tripId: String) = "gallery_trip_$tripId"

    fun getTripGalleryImageUris(context: Context, tripId: String): List<String> {
        val raw = prefs(context).getString(tripGalleryKey(tripId), "") ?: ""
        return if (raw.isBlank()) emptyList() else raw.split("|||")
    }

    fun addTripGalleryImageUri(context: Context, tripId: String, uri: String) {
        val current = getTripGalleryImageUris(context, tripId).toMutableList()
        if (!current.contains(uri)) {
            current.add(uri)
            prefs(context).edit()
                .putString(tripGalleryKey(tripId), current.joinToString("|||"))
                .apply()
            Log.i(TAG, "Trip gallery URI added for tripId=$tripId: $uri")
        }
    }

    fun removeTripGalleryImageUri(context: Context, tripId: String, uri: String) {
        val current = getTripGalleryImageUris(context, tripId).toMutableList()
        current.remove(uri)
        prefs(context).edit()
            .putString(tripGalleryKey(tripId), current.joinToString("|||"))
            .apply()
        Log.i(TAG, "Trip gallery URI removed for tripId=$tripId: $uri")
    }

    // ── Remember login (LoginScreen — Sharon) ──────────────────────────────

    fun getRememberLogin(context: Context): Boolean =
        prefs(context).getBoolean(KEY_REMEMBER_LOGIN, false)

    fun getRememberedEmail(context: Context): String =
        prefs(context).getString(KEY_REMEMBERED_EMAIL, "") ?: ""

    fun saveRememberedLogin(context: Context, email: String) {
        Log.i(TAG, "Remembered login saved for: $email")
        prefs(context).edit()
            .putBoolean(KEY_REMEMBER_LOGIN, true)
            .putString(KEY_REMEMBERED_EMAIL, email)
            .commit()
    }

    fun clearRememberedLogin(context: Context) {
        Log.i(TAG, "Remembered login cleared")
        prefs(context).edit()
            .putBoolean(KEY_REMEMBER_LOGIN, false)
            .remove(KEY_REMEMBERED_EMAIL)
            .commit()
    }
}