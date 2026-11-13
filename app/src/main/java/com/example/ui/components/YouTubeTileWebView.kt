package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Color as AndroidColor
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.R
import com.example.domain.YouTubeUrlParser
import com.example.ui.theme.LoopEmerald
import com.example.ui.theme.LoopEmeraldContainer
import com.example.ui.theme.OutlineSubtle
import com.example.ui.theme.StudioSurface

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubeTileCard(
  slotIndex: Int,
  videoId: String,
  reloadKey: Int,
  isMuted: Boolean,
  onReloadSlot: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current
  var isLoading by remember(videoId, reloadKey, isMuted) { mutableStateOf(true) }

  val webView = remember(context) {
    WebView(context).apply {
      layoutParams =
        ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT,
        )
      setBackgroundColor(AndroidColor.BLACK)
      setLayerType(View.LAYER_TYPE_HARDWARE, null)
      isVerticalScrollBarEnabled = false
      isHorizontalScrollBarEnabled = false
      settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
        mediaPlaybackRequiresUserGesture = false
        loadWithOverviewMode = true
        useWideViewPort = true
        mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
      }
      webChromeClient = WebChromeClient()
      webViewClient =
        object : WebViewClient() {
          override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?,
          ): Boolean {
            val host = request?.url?.host?.lowercase().orEmpty()
            return !(host.contains("youtube.com") ||
              host.contains("youtube-nocookie.com") ||
              host.contains("youtu.be") ||
              host.contains("googlevideo.com"))
          }

          override fun onPageFinished(view: WebView?, url: String?) {
            isLoading = false
          }
        }
    }
  }

  var loadedSignature by remember { mutableStateOf("") }
  val currentSignature = "$videoId:$reloadKey:$isMuted"

  LaunchedEffect(currentSignature) {
    if (loadedSignature != currentSignature) {
      isLoading = true
      loadedSignature = currentSignature
      val html = YouTubeUrlParser.buildEmbedHtml(videoId = videoId, isMuted = isMuted)
      webView.loadDataWithBaseURL(
        "https://www.youtube.com",
        html,
        "text/html",
        "UTF-8",
        null,
      )
    }
  }

  DisposableEffect(lifecycleOwner, webView) {
    val observer = LifecycleEventObserver { _, event ->
      when (event) {
        Lifecycle.Event.ON_RESUME -> webView.onResume()
        Lifecycle.Event.ON_PAUSE -> webView.onPause()
        else -> Unit
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
      lifecycleOwner.lifecycle.removeObserver(observer)
      webView.stopLoading()
      webView.loadUrl("about:blank")
      webView.clearHistory()
      webView.removeAllViews()
      webView.destroy()
    }
  }

  Surface(
    modifier = modifier.testTag("video_tile_$slotIndex"),
    shape = RoundedCornerShape(14.dp),
    color = StudioSurface,
    border = BorderStroke(1.dp, OutlineSubtle),
    tonalElevation = 2.dp,
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Row(
        modifier =
          Modifier.fillMaxWidth()
            .padding(start = 10.dp, end = 2.dp, top = 2.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "#${slotIndex + 1}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
          )
          Spacer(modifier = Modifier.width(8.dp))
          Surface(
            color = LoopEmeraldContainer,
            shape = RoundedCornerShape(50),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Box(
                modifier =
                  Modifier.size(6.dp)
                    .clip(CircleShape)
                    .background(LoopEmerald),
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = stringResource(R.string.loop_badge),
                style = MaterialTheme.typography.labelSmall,
                color = LoopEmerald,
              )
            }
          }
        }

        IconButton(
          onClick = onReloadSlot,
          modifier =
            Modifier.minimumInteractiveComponentSize()
              .testTag("reload_tile_${slotIndex}_button"),
        ) {
          Icon(
            imageVector = Icons.Rounded.Refresh,
            contentDescription = stringResource(R.string.reload_screen),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp),
          )
        }
      }

      Box(
        modifier =
          Modifier.fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(Color.Black),
        contentAlignment = Alignment.Center,
      ) {
        AndroidView(
          factory = { webView },
          modifier = Modifier.fillMaxSize(),
        )

        androidx.compose.animation.AnimatedVisibility(
          visible = isLoading,
          enter = fadeIn(),
          exit = fadeOut(),
        ) {
          Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.72f)),
            contentAlignment = Alignment.Center,
          ) {
            CircularProgressIndicator(
              modifier = Modifier.size(28.dp),
              color = MaterialTheme.colorScheme.primary,
              strokeWidth = 2.5.dp,
            )
          }
        }
      }
    }
  }
}
