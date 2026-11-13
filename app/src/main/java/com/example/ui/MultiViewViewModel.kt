package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.PlayerPreferencesRepository
import com.example.domain.YouTubeUrlParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MultiViewUiState(
  val urlInput: String = "",
  val screenCount: Int = 4,
  val screenCountInput: String = "4",
  val activeVideoId: String? = null,
  val activeScreenCount: Int = 0,
  val isMuted: Boolean = true,
  val gridColumnsOverride: Int? = null,
  val globalReloadNonce: Int = 0,
  val slotReloadNonces: Map<Int, Int> = emptyMap(),
  val errorMessage: String? = null,
  val recentVideoIds: List<String> = emptyList(),
  val controlsExpanded: Boolean = true,
)

class MultiViewViewModel(application: Application) : AndroidViewModel(application) {
  private val repository = PlayerPreferencesRepository(application.applicationContext)

  private val _uiState = MutableStateFlow(MultiViewUiState())
  val uiState: StateFlow<MultiViewUiState> = _uiState.asStateFlow()

  init {
    viewModelScope.launch {
      val initialPrefs = repository.preferences.first()
      _uiState.update { current ->
        current.copy(
          urlInput = if (current.urlInput.isEmpty()) initialPrefs.lastUrl else current.urlInput,
          screenCount = initialPrefs.lastScreenCount,
          screenCountInput = initialPrefs.lastScreenCount.toString(),
          recentVideoIds = initialPrefs.recentVideoIds,
        )
      }
      repository.preferences.collect { prefs ->
        _uiState.update { it.copy(recentVideoIds = prefs.recentVideoIds) }
      }
    }
  }

  fun onUrlChanged(newValue: String) {
    _uiState.update { it.copy(urlInput = newValue, errorMessage = null) }
  }

  fun onPasteUrl(pastedText: String) {
    _uiState.update { it.copy(urlInput = pastedText.trim(), errorMessage = null) }
  }

  fun onClearUrl() {
    _uiState.update { it.copy(urlInput = "", errorMessage = null) }
  }

  fun onScreenCountChanged(count: Int) {
    val clamped = count.coerceIn(1, 20)
    _uiState.update {
      it.copy(
        screenCount = clamped,
        screenCountInput = clamped.toString(),
      )
    }
  }

  fun onScreenCountTextChanged(text: String) {
    val digitsOnly = text.filter { it.isDigit() }.take(2)
    if (digitsOnly.isEmpty()) {
      _uiState.update { it.copy(screenCountInput = "") }
      return
    }
    val parsed = digitsOnly.toIntOrNull()?.coerceIn(1, 20) ?: _uiState.value.screenCount
    _uiState.update {
      it.copy(
        screenCount = parsed,
        screenCountInput = parsed.toString(),
      )
    }
  }

  fun startMultiView(invalidUrlErrorText: String) {
    val state = _uiState.value
    val videoId = YouTubeUrlParser.extractVideoId(state.urlInput)
    if (videoId == null) {
      _uiState.update { it.copy(errorMessage = invalidUrlErrorText) }
      return
    }

    val finalCount = state.screenCount.coerceIn(1, 20)
    _uiState.update {
      it.copy(
        screenCount = finalCount,
        screenCountInput = finalCount.toString(),
        activeVideoId = videoId,
        activeScreenCount = finalCount,
        globalReloadNonce = it.globalReloadNonce + 1,
        slotReloadNonces = emptyMap(),
        errorMessage = null,
      )
    }

    viewModelScope.launch {
      repository.saveLastSession(
        url = state.urlInput.trim(),
        screenCount = finalCount,
        videoId = videoId,
      )
    }
  }

  fun selectRecentVideo(videoId: String) {
    val fullUrl = "https://youtu.be/$videoId"
    val finalCount = _uiState.value.screenCount.coerceIn(1, 20)
    _uiState.update {
      it.copy(
        urlInput = fullUrl,
        errorMessage = null,
        activeVideoId = videoId,
        activeScreenCount = finalCount,
        globalReloadNonce = it.globalReloadNonce + 1,
        slotReloadNonces = emptyMap(),
      )
    }
    viewModelScope.launch {
      repository.saveLastSession(
        url = fullUrl,
        screenCount = finalCount,
        videoId = videoId,
      )
    }
  }

  fun stopMultiView() {
    _uiState.update {
      it.copy(
        activeVideoId = null,
        activeScreenCount = 0,
        slotReloadNonces = emptyMap(),
        controlsExpanded = true,
      )
    }
  }

  fun reloadAll() {
    _uiState.update {
      it.copy(
        globalReloadNonce = it.globalReloadNonce + 1,
        slotReloadNonces = emptyMap(),
      )
    }
  }

  fun reloadSlot(index: Int) {
    _uiState.update { current ->
      val updatedMap = current.slotReloadNonces.toMutableMap()
      updatedMap[index] = (updatedMap[index] ?: 0) + 1
      current.copy(slotReloadNonces = updatedMap)
    }
  }

  fun toggleMuteAll() {
    _uiState.update { it.copy(isMuted = !it.isMuted) }
  }

  fun setGridColumns(columns: Int?) {
    _uiState.update { it.copy(gridColumnsOverride = columns) }
  }

  fun toggleControlsExpanded() {
    _uiState.update { it.copy(controlsExpanded = !it.controlsExpanded) }
  }
}
