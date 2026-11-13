package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.playerPrefsDataStore: DataStore<Preferences> by
  preferencesDataStore(name = "multiview_preferences")

data class PlayerPreferences(
  val lastUrl: String = "",
  val lastScreenCount: Int = 4,
  val recentVideoIds: List<String> = emptyList(),
)

class PlayerPreferencesRepository(private val context: Context) {

  private object Keys {
    val LAST_URL = stringPreferencesKey("last_url")
    val LAST_SCREEN_COUNT = intPreferencesKey("last_screen_count")
    val RECENT_VIDEO_IDS = stringPreferencesKey("recent_video_ids")
  }

  val preferences: Flow<PlayerPreferences> =
    context.playerPrefsDataStore.data.map { prefs ->
      val recentRaw = prefs[Keys.RECENT_VIDEO_IDS].orEmpty()
      val recentList =
        recentRaw
          .split(",")
          .map { it.trim() }
          .filter { it.isNotEmpty() }
          .take(5)

      PlayerPreferences(
        lastUrl = prefs[Keys.LAST_URL].orEmpty(),
        lastScreenCount = (prefs[Keys.LAST_SCREEN_COUNT] ?: 4).coerceIn(1, 20),
        recentVideoIds = recentList,
      )
    }

  suspend fun saveLastSession(url: String, screenCount: Int, videoId: String) {
    context.playerPrefsDataStore.edit { prefs ->
      prefs[Keys.LAST_URL] = url
      prefs[Keys.LAST_SCREEN_COUNT] = screenCount.coerceIn(1, 20)
      val existing =
        prefs[Keys.RECENT_VIDEO_IDS]
          .orEmpty()
          .split(",")
          .map { it.trim() }
          .filter { it.isNotEmpty() && it != videoId }
      val updated = (listOf(videoId) + existing).take(5)
      prefs[Keys.RECENT_VIDEO_IDS] = updated.joinToString(",")
    }
  }
}
