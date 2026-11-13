package com.example.domain

object YouTubeUrlParser {
  private val VIDEO_ID_REGEX = Regex("^[A-Za-z0-9_-]{11}$")
  private val URL_PATTERNS = listOf(
    Regex("""(?:youtu\.be/|youtube(?:-nocookie)?\.com/(?:embed/|v/|shorts/|live/|u/\w/))([A-Za-z0-9_-]{11})"""),
    Regex("""[?&]v=([A-Za-z0-9_-]{11})"""),
  )

  fun extractVideoId(input: String): String? {
    val trimmed = input.trim()
    if (trimmed.isEmpty()) return null
    if (VIDEO_ID_REGEX.matches(trimmed)) return trimmed

    for (pattern in URL_PATTERNS) {
      val match = pattern.find(trimmed)
      val id = match?.groupValues?.getOrNull(1)
      if (!id.isNullOrEmpty() && VIDEO_ID_REGEX.matches(id)) {
        return id
      }
    }
    return null
  }

  fun buildEmbedUrl(videoId: String, isMuted: Boolean): String {
    val muteFlag = if (isMuted) 1 else 0
    return "https://www.youtube.com/embed/$videoId" +
      "?autoplay=1&mute=$muteFlag&loop=1&playlist=$videoId&rel=0&playsinline=1&enablejsapi=1&origin=https://www.youtube.com"
  }

  fun buildEmbedHtml(videoId: String, isMuted: Boolean): String {
    val embedUrl = buildEmbedUrl(videoId = videoId, isMuted = isMuted)
    return """
      <!DOCTYPE html>
      <html>
      <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
        <style>
          * { margin: 0; padding: 0; box-sizing: border-box; }
          html, body { width: 100%; height: 100%; background-color: #000000; overflow: hidden; }
          iframe { width: 100%; height: 100%; border: none; display: block; }
        </style>
      </head>
      <body>
        <iframe
          id="ytplayer"
          src="$embedUrl"
          allow="autoplay; encrypted-media; picture-in-picture; fullscreen"
          referrerpolicy="strict-origin-when-cross-origin"
          allowfullscreen>
        </iframe>
        <script>
          function setPlayerMuted(muted) {
            var frame = document.getElementById('ytplayer');
            if (frame && frame.contentWindow) {
              frame.contentWindow.postMessage(JSON.stringify({
                event: 'command',
                func: muted ? 'mute' : 'unMute',
                args: []
              }), '*');
            }
          }
        </script>
      </body>
      </html>
    """.trimIndent()
  }
}
