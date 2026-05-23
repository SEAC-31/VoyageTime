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

    const val KEY_REMEMBER_LOGIN = "remember_login"
    const val KEY_REMEMBERED_EMAIL = "remembered_email"

    const val KEY_GALLERY_IMAGE_URIS = "gallery_image_uris"

    const val DEFAULT_USERNAME = ""
    const val DEFAULT_DATE_OF_BIRTH = ""
    const val DEFAULT_CURRENCY = "EUR"
    const val DEFAULT_SHOW_PRICES = true
    const val DEFAULT_DARK_MODE = false
    const val DEFAULT_TERMS_ACCEPTED = false
    const val DEFAULT_REMEMBER_LOGIN = false
    const val DEFAULT_REMEMBERED_EMAIL = ""

    private fun prefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
    }

    fun hasAcceptedTerms(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_TERMS_ACCEPTED, DEFAULT_TERMS_ACCEPTED)
    }

    fun saveTermsAccepted(context: Context, accepted: Boolean) {
        Log.i(TAG, "Terms accepted set to: $accepted")
        prefs(context).edit().putBoolean(KEY_TERMS_ACCEPTED, accepted).commit()
    }

    fun getRememberLogin(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_REMEMBER_LOGIN, DEFAULT_REMEMBER_LOGIN)
    }

    fun getRememberedEmail(context: Context): String {
        return prefs(context).getString(KEY_REMEMBERED_EMAIL, DEFAULT_REMEMBERED_EMAIL)
            ?: DEFAULT_REMEMBERED_EMAIL
    }

    fun saveRememberedLogin(context: Context, email: String) {
        val cleanEmail = email.trim()

        Log.i(TAG, "Remember login enabled for email=$cleanEmail")

        prefs(context).edit()
            .putBoolean(KEY_REMEMBER_LOGIN, true)
            .putString(KEY_REMEMBERED_EMAIL, cleanEmail)
            .apply()
    }

    fun clearRememberedLogin(context: Context) {
        Log.i(TAG, "Remember login disabled")

        prefs(context).edit()
            .putBoolean(KEY_REMEMBER_LOGIN, false)
            .remove(KEY_REMEMBERED_EMAIL)
            .apply()
    }

    fun getGalleryImageUris(context: Context): List<String> {
        return prefs(context)
            .getStringSet(KEY_GALLERY_IMAGE_URIS, emptySet())
            ?.toList()
            .orEmpty()
    }

    fun addGalleryImageUri(context: Context, uri: String) {
        val current = getGalleryImageUris(context).toMutableSet()

        if (current.add(uri)) {
            Log.i(TAG, "Gallery image added: $uri")
        }

        prefs(context).edit()
            .putStringSet(KEY_GALLERY_IMAGE_URIS, current)
            .apply()
    }

    fun removeGalleryImageUri(context: Context, uri: String) {
        val current = getGalleryImageUris(context).toMutableSet()

        if (current.remove(uri)) {
            Log.i(TAG, "Gallery image removed: $uri")
        }

        prefs(context).edit()
            .putStringSet(KEY_GALLERY_IMAGE_URIS, current)
            .apply()
    }

    fun getDarkMode(context: Context): Boolean {
        val value = prefs(context).getBoolean(KEY_DARK_MODE, DEFAULT_DARK_MODE)
        Log.d(TAG, "getDarkMode -> $value")
        return value
    }

    fun saveDarkMode(context: Context, enabled: Boolean) {
        Log.i(TAG, "Dark mode set to: $enabled")
        prefs(context).edit().putBoolean(KEY_DARK_MODE, enabled).commit()
    }

    fun getUsername(context: Context): String {
        return prefs(context).getString(KEY_USERNAME, DEFAULT_USERNAME) ?: DEFAULT_USERNAME
    }

    fun saveUsername(context: Context, value: String) {
        Log.i(TAG, "Username saved: $value")
        prefs(context).edit().putString(KEY_USERNAME, value).apply()
    }

    fun getDateOfBirth(context: Context): String {
        return prefs(context).getString(KEY_DATE_OF_BIRTH, DEFAULT_DATE_OF_BIRTH)
            ?: DEFAULT_DATE_OF_BIRTH
    }

    fun saveDateOfBirth(context: Context, value: String) {
        Log.i(TAG, "Date of birth saved: $value")
        prefs(context).edit().putString(KEY_DATE_OF_BIRTH, value).apply()
    }

    fun getCurrency(context: Context): String {
        return prefs(context).getString(KEY_CURRENCY, DEFAULT_CURRENCY) ?: DEFAULT_CURRENCY
    }

    fun saveCurrency(context: Context, value: String) {
        Log.i(TAG, "Currency saved: $value")
        prefs(context).edit().putString(KEY_CURRENCY, value).apply()
    }

    fun getShowPrices(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_SHOW_PRICES, DEFAULT_SHOW_PRICES)
    }

    fun saveShowPrices(context: Context, value: Boolean) {
        Log.i(TAG, "Show prices set to: $value")
        prefs(context).edit().putBoolean(KEY_SHOW_PRICES, value).apply()
    }
}