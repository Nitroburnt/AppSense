package com.example.appsense.ui.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appsense.data.source.local.db.UninstallLogDao
import com.example.appsense.data.source.local.db.UninstallLogEntity
import com.example.appsense.domain.model.AppInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val uninstallLogDao: UninstallLogDao
) : ViewModel() {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _darkTheme = MutableStateFlow(prefs.getBoolean(KEY_DARK_THEME, false))
    val darkTheme: StateFlow<Boolean> = _darkTheme.asStateFlow()

    private val _geminiApiKey = MutableStateFlow(prefs.getString(KEY_GEMINI_KEY, "").orEmpty())
    val geminiApiKey: StateFlow<String> = _geminiApiKey.asStateFlow()

    private val _openAiApiKey = MutableStateFlow(prefs.getString(KEY_OPENAI_KEY, "").orEmpty())
    val openAiApiKey: StateFlow<String> = _openAiApiKey.asStateFlow()

    val uninstallLogs = uninstallLogDao.getAllLogs()

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            KEY_DARK_THEME -> _darkTheme.value = prefs.getBoolean(key, false)
            KEY_GEMINI_KEY -> _geminiApiKey.value = prefs.getString(key, "").orEmpty()
            KEY_OPENAI_KEY -> _openAiApiKey.value = prefs.getString(key, "").orEmpty()
        }
    }

    init {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onCleared() {
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
        super.onCleared()
    }

    fun setDarkTheme(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_THEME, enabled).apply()
    }

    fun setGeminiApiKey(key: String) {
        prefs.edit().putString(KEY_GEMINI_KEY, key).apply()
    }

    fun setOpenAiApiKey(key: String) {
        prefs.edit().putString(KEY_OPENAI_KEY, key).apply()
    }

    fun logUninstall(app: AppInfo) {
        viewModelScope.launch {
            uninstallLogDao.insertLog(
                UninstallLogEntity(
                    packageName = app.packageName,
                    appName = app.appName,
                    uninstalledAt = System.currentTimeMillis()
                )
            )
        }
    }

    private companion object {
        const val PREFS_NAME = "appsense_prefs"
        const val KEY_DARK_THEME = "dark_theme"
        const val KEY_GEMINI_KEY = "gemini_api_key"
        const val KEY_OPENAI_KEY = "openai_api_key"
    }
}
