package com.example

import com.example.domain.YouTubeUrlParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class YouTubeUrlParserTest {

  @Test
  fun `extracts id from standard watch url`() {
    assertEquals(
      "dQw4w9WgXcQ",
      YouTubeUrlParser.extractVideoId("https://www.youtube.com/watch?v=dQw4w9WgXcQ&t=15s"),
    )
  }

  @Test
  fun `extracts id from short youtu be url`() {
    assertEquals(
      "dQw4w9WgXcQ",
      YouTubeUrlParser.extractVideoId("https://youtu.be/dQw4w9WgXcQ?si=test123"),
    )
  }

  @Test
  fun `extracts id from shorts and live urls`() {
    assertEquals(
      "abcDEF_12-3",
      YouTubeUrlParser.extractVideoId("https://www.youtube.com/shorts/abcDEF_12-3?feature=share"),
    )
    assertEquals(
      "abcDEF_12-3",
      YouTubeUrlParser.extractVideoId("https://www.youtube.com/live/abcDEF_12-3"),
    )
  }

  @Test
  fun `extracts id from embed url and direct id`() {
    assertEquals(
      "dQw4w9WgXcQ",
      YouTubeUrlParser.extractVideoId("https://www.youtube.com/embed/dQw4w9WgXcQ"),
    )
    assertEquals(
      "dQw4w9WgXcQ",
      YouTubeUrlParser.extractVideoId("  dQw4w9WgXcQ  "),
    )
  }

  @Test
  fun `returns null for invalid urls`() {
    assertNull(YouTubeUrlParser.extractVideoId(""))
    assertNull(YouTubeUrlParser.extractVideoId("https://www.youtube.com/"))
    assertNull(YouTubeUrlParser.extractVideoId("not a video link"))
  }

  @Test
  fun `builds embed url with autoplay loop playlist and mute`() {
    val embedUrl = YouTubeUrlParser.buildEmbedUrl(videoId = "dQw4w9WgXcQ", isMuted = true)
    assertTrue(embedUrl.contains("https://www.youtube.com/embed/dQw4w9WgXcQ"))
    assertTrue(embedUrl.contains("autoplay=1"))
    assertTrue(embedUrl.contains("mute=1"))
    assertTrue(embedUrl.contains("loop=1"))
    assertTrue(embedUrl.contains("playlist=dQw4w9WgXcQ"))
    assertTrue(embedUrl.contains("rel=0"))
  }

  @Test
  fun `builds unmuted embed html when mute is toggled off`() {
    val unmutedUrl = YouTubeUrlParser.buildEmbedUrl(videoId = "dQw4w9WgXcQ", isMuted = false)
    val unmutedHtml = YouTubeUrlParser.buildEmbedHtml(videoId = "dQw4w9WgXcQ", isMuted = false)
    assertTrue(unmutedUrl.contains("mute=0"))
    assertTrue(unmutedHtml.contains("mute=0"))
  }
}
