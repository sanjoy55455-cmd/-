package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.ui.components.YouTubeTileCard
import com.example.ui.theme.AmberNotice
import com.example.ui.theme.BroadcastCrimson
import com.example.ui.theme.BroadcastCrimsonContainer
import com.example.ui.theme.InputWell
import com.example.ui.theme.LoopEmerald
import com.example.ui.theme.LoopEmeraldContainer
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.OutlineSubtle
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceElevated
import kotlinx.coroutines.launch

private val SCREEN_COUNT_PRESETS = listOf(1, 2, 4, 6, 9, 12, 16, 20)

@Composable
fun MultiViewScreen(
  viewModel: MultiViewViewModel = viewModel(),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()
  val clipboardManager = LocalClipboardManager.current
  val keyboardController = LocalSoftwareKeyboardController.current

  val invalidUrlText = stringResource(R.string.invalid_url_error)
  val clipboardEmptyText = stringResource(R.string.clipboard_empty)

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = ObsidianBg,
    contentWindowInsets = WindowInsets.safeDrawing,
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
  ) { innerPadding ->
    BoxWithConstraints(
      modifier =
        Modifier.fillMaxSize()
          .drawBehind {
            drawRect(color = ObsidianBg)
            drawCircle(
              brush =
                Brush.radialGradient(
                  colors = listOf(BroadcastCrimson.copy(alpha = 0.14f), Color.Transparent),
                  center = Offset(size.width * 0.15f, 0f),
                  radius = size.width * 0.85f,
                ),
              center = Offset(size.width * 0.15f, 0f),
              radius = size.width * 0.85f,
            )
            drawCircle(
              brush =
                Brush.radialGradient(
                  colors = listOf(LoopEmerald.copy(alpha = 0.08f), Color.Transparent),
                  center = Offset(size.width * 0.9f, size.height * 0.2f),
                  radius = size.width * 0.7f,
                ),
              center = Offset(size.width * 0.9f, size.height * 0.2f),
              radius = size.width * 0.7f,
            )
          }
          .padding(innerPadding),
    ) {
      val availableWidth = maxWidth
      val autoColumns =
        when {
          uiState.activeScreenCount <= 1 -> 1
          availableWidth >= 840.dp -> if (uiState.activeScreenCount >= 8) 4 else 3
          availableWidth >= 520.dp -> 2
          else -> 2
        }
      val effectiveColumns =
        (uiState.gridColumnsOverride ?: autoColumns)
          .coerceIn(1, maxOf(1, uiState.activeScreenCount))

      val scrollState = rememberScrollState()

      Column(
        modifier =
          Modifier.fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Column(
          modifier = Modifier.fillMaxWidth().widthIn(max = 1120.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
          StudioHeaderBar(
            activeScreenCount = uiState.activeScreenCount,
            isPlaying = uiState.activeVideoId != null,
            controlsExpanded = uiState.controlsExpanded,
            onToggleControls = { viewModel.toggleControlsExpanded() },
          )

          AnimatedVisibility(
            visible = uiState.controlsExpanded || uiState.activeVideoId == null,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
          ) {
            ControlDeckCard(
              uiState = uiState,
              onUrlChanged = viewModel::onUrlChanged,
              onPasteClick = {
                val clip = clipboardManager.getText()?.text
                if (clip.isNullOrBlank()) {
                  scope.launch { snackbarHostState.showSnackbar(clipboardEmptyText) }
                } else {
                  viewModel.onPasteUrl(clip)
                }
              },
              onClearClick = viewModel::onClearUrl,
              onScreenCountChanged = viewModel::onScreenCountChanged,
              onScreenCountTextChanged = viewModel::onScreenCountTextChanged,
              onStartClick = {
                keyboardController?.hide()
                viewModel.startMultiView(invalidUrlText)
              },
              onToggleMuteAll = viewModel::toggleMuteAll,
              onSelectRecent = { videoId ->
                keyboardController?.hide()
                viewModel.selectRecentVideo(videoId)
              },
            )
          }

          val activeVideoId = uiState.activeVideoId
          if (activeVideoId != null && uiState.activeScreenCount > 0) {
            ActivePlaybackToolbar(
              activeVideoId = activeVideoId,
              isMuted = uiState.isMuted,
              gridColumnsOverride = uiState.gridColumnsOverride,
              isWideScreen = availableWidth >= 600.dp,
              onToggleMuteAll = viewModel::toggleMuteAll,
              onReloadAll = viewModel::reloadAll,
              onStopAll = viewModel::stopMultiView,
              onSelectColumns = viewModel::setGridColumns,
            )

            VideoWallGrid(
              videoId = activeVideoId,
              screenCount = uiState.activeScreenCount,
              columns = effectiveColumns,
              globalReloadNonce = uiState.globalReloadNonce,
              slotReloadNonces = uiState.slotReloadNonces,
              isMuted = uiState.isMuted,
              onReloadSlot = viewModel::reloadSlot,
            )
          } else {
            StudioEmptyStateCard()
          }

          Spacer(modifier = Modifier.height(16.dp))
        }
      }
    }
  }
}

@Composable
private fun StudioHeaderBar(
  activeScreenCount: Int,
  isPlaying: Boolean,
  controlsExpanded: Boolean,
  onToggleControls: () -> Unit,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Row(
      modifier = Modifier.weight(1f),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Surface(
        modifier = Modifier.size(44.dp),
        shape = RoundedCornerShape(12.dp),
        color = BroadcastCrimson,
        shadowElevation = 6.dp,
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Rounded.Repeat,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp),
          )
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = stringResource(R.string.player_title),
          style = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.onBackground,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = stringResource(R.string.player_subtitle),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }
    }

    Spacer(modifier = Modifier.width(8.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
      Surface(
        shape = RoundedCornerShape(50),
        color = if (isPlaying) LoopEmeraldContainer else StudioSurfaceElevated,
        border =
          BorderStroke(
            width = 1.dp,
            color = if (isPlaying) LoopEmerald.copy(alpha = 0.5f) else OutlineSubtle,
          ),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Box(
            modifier =
              Modifier.size(7.dp)
                .clip(CircleShape)
                .background(
                  if (isPlaying) LoopEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                ),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text =
              if (isPlaying) {
                stringResource(R.string.status_playing_format, activeScreenCount)
              } else {
                stringResource(R.string.status_ready)
              },
            style = MaterialTheme.typography.labelMedium,
            color = if (isPlaying) LoopEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }

      if (isPlaying) {
        Spacer(modifier = Modifier.width(4.dp))
        IconButton(
          onClick = onToggleControls,
          modifier =
            Modifier.minimumInteractiveComponentSize()
              .testTag("toggle_controls_button"),
        ) {
          Icon(
            imageVector =
              if (controlsExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
            contentDescription =
              stringResource(
                if (controlsExpanded) {
                  R.string.toggle_controls_collapse
                } else {
                  R.string.toggle_controls_expand
                },
              ),
            tint = MaterialTheme.colorScheme.onBackground,
          )
        }
      }
    }
  }
}

@Composable
private fun ControlDeckCard(
  uiState: MultiViewUiState,
  onUrlChanged: (String) -> Unit,
  onPasteClick: () -> Unit,
  onClearClick: () -> Unit,
  onScreenCountChanged: (Int) -> Unit,
  onScreenCountTextChanged: (String) -> Unit,
  onStartClick: () -> Unit,
  onToggleMuteAll: () -> Unit,
  onSelectRecent: (String) -> Unit,
) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(20.dp),
    color = StudioSurface,
    border = BorderStroke(1.dp, OutlineSubtle),
    tonalElevation = 4.dp,
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      OutlinedTextField(
        value = uiState.urlInput,
        onValueChange = onUrlChanged,
        modifier = Modifier.fillMaxWidth().testTag("video_url_input"),
        singleLine = true,
        label = { Text(text = stringResource(R.string.url_input_label)) },
        placeholder = { Text(text = stringResource(R.string.url_input_hint)) },
        leadingIcon = {
          Icon(
            imageVector = Icons.Rounded.Link,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        },
        trailingIcon = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            if (uiState.urlInput.isNotEmpty()) {
              IconButton(
                onClick = onClearClick,
                modifier = Modifier.minimumInteractiveComponentSize().testTag("clear_url_button"),
              ) {
                Icon(
                  imageVector = Icons.Rounded.Close,
                  contentDescription = stringResource(R.string.clear_link),
                )
              }
            }
            IconButton(
              onClick = onPasteClick,
              modifier = Modifier.minimumInteractiveComponentSize().testTag("paste_url_button"),
            ) {
              Icon(
                imageVector = Icons.Rounded.ContentPaste,
                contentDescription = stringResource(R.string.paste_link),
                tint = MaterialTheme.colorScheme.primary,
              )
            }
          }
        },
        isError = uiState.errorMessage != null,
        keyboardOptions =
          KeyboardOptions(
            keyboardType = KeyboardType.Uri,
            imeAction = ImeAction.Go,
          ),
        keyboardActions = KeyboardActions(onGo = { onStartClick() }),
        shape = RoundedCornerShape(14.dp),
        colors =
          OutlinedTextFieldDefaults.colors(
            focusedContainerColor = InputWell,
            unfocusedContainerColor = InputWell,
            errorContainerColor = InputWell,
            focusedBorderColor = BroadcastCrimson,
            unfocusedBorderColor = OutlineSubtle,
          ),
      )

      AnimatedVisibility(visible = uiState.errorMessage != null) {
        Surface(
          modifier = Modifier.fillMaxWidth().testTag("url_error_banner"),
          shape = RoundedCornerShape(12.dp),
          color = BroadcastCrimsonContainer,
          border = BorderStroke(1.dp, BroadcastCrimson.copy(alpha = 0.6f)),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(
              imageVector = Icons.Rounded.ErrorOutline,
              contentDescription = null,
              tint = Color(0xFFFF8A93),
              modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = uiState.errorMessage.orEmpty(),
              style = MaterialTheme.typography.bodyMedium,
              color = Color(0xFFFFD9DC),
            )
          }
        }
      }

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Rounded.GridView,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = stringResource(R.string.screen_count_label),
              style = MaterialTheme.typography.titleSmall,
              color = MaterialTheme.colorScheme.onSurface,
            )
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            FilledTonalIconButton(
              onClick = { onScreenCountChanged(uiState.screenCount - 1) },
              enabled = uiState.screenCount > 1,
              modifier =
                Modifier.minimumInteractiveComponentSize()
                  .testTag("decrease_screen_count_button"),
              colors =
                IconButtonDefaults.filledTonalIconButtonColors(
                  containerColor = InputWell,
                  contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            ) {
              Icon(
                imageVector = Icons.Rounded.Remove,
                contentDescription = stringResource(R.string.decrease_count),
              )
            }

            OutlinedTextField(
              value = uiState.screenCountInput,
              onValueChange = onScreenCountTextChanged,
              modifier = Modifier.width(68.dp).testTag("screen_count_input"),
              singleLine = true,
              textStyle =
                MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center),
              keyboardOptions =
                KeyboardOptions(
                  keyboardType = KeyboardType.Number,
                  imeAction = ImeAction.Done,
                ),
              shape = RoundedCornerShape(12.dp),
              colors =
                OutlinedTextFieldDefaults.colors(
                  focusedContainerColor = InputWell,
                  unfocusedContainerColor = InputWell,
                  focusedBorderColor = BroadcastCrimson,
                  unfocusedBorderColor = OutlineSubtle,
                ),
            )

            FilledTonalIconButton(
              onClick = { onScreenCountChanged(uiState.screenCount + 1) },
              enabled = uiState.screenCount < 20,
              modifier =
                Modifier.minimumInteractiveComponentSize()
                  .testTag("increase_screen_count_button"),
              colors =
                IconButtonDefaults.filledTonalIconButtonColors(
                  containerColor = InputWell,
                  contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            ) {
              Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = stringResource(R.string.increase_count),
              )
            }
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          SCREEN_COUNT_PRESETS.forEach { preset ->
            val selected = uiState.screenCount == preset
            FilterChip(
              selected = selected,
              onClick = { onScreenCountChanged(preset) },
              modifier = Modifier.testTag("preset_chip_$preset"),
              label = {
                Text(
                  text = "${preset}x",
                  style = MaterialTheme.typography.labelMedium,
                )
              },
              colors =
                FilterChipDefaults.filterChipColors(
                  containerColor = InputWell,
                  selectedContainerColor = BroadcastCrimson,
                  labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                  selectedLabelColor = Color.White,
                ),
              border =
                FilterChipDefaults.filterChipBorder(
                  enabled = true,
                  selected = selected,
                  borderColor = OutlineSubtle,
                  selectedBorderColor = BroadcastCrimson,
                ),
            )
          }
        }

        AnimatedVisibility(visible = uiState.screenCount > 8) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(
              imageVector = Icons.Rounded.Info,
              contentDescription = null,
              tint = AmberNotice,
              modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = stringResource(R.string.high_count_tip),
              style = MaterialTheme.typography.bodyMedium,
              color = AmberNotice,
            )
          }
        }
      }

      FilledTonalButton(
        onClick = onToggleMuteAll,
        modifier =
          Modifier.fillMaxWidth()
            .height(48.dp)
            .testTag("controls_global_mute_toggle"),
        shape = RoundedCornerShape(14.dp),
        colors =
          ButtonDefaults.filledTonalButtonColors(
            containerColor = if (uiState.isMuted) InputWell else LoopEmeraldContainer,
            contentColor =
              if (uiState.isMuted) MaterialTheme.colorScheme.onSurface else LoopEmerald,
          ),
      ) {
        Icon(
          imageVector =
            if (uiState.isMuted) {
              Icons.AutoMirrored.Rounded.VolumeOff
            } else {
              Icons.AutoMirrored.Rounded.VolumeUp
            },
          contentDescription = stringResource(R.string.global_audio_label),
          modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text =
            stringResource(
              if (uiState.isMuted) R.string.unmute_all_action else R.string.mute_all_action,
            ),
          style = MaterialTheme.typography.labelLarge,
        )
      }

      Button(
        onClick = onStartClick,
        modifier =
          Modifier.fillMaxWidth()
            .height(52.dp)
            .testTag("start_multiview_button"),
        shape = RoundedCornerShape(14.dp),
        colors =
          ButtonDefaults.buttonColors(
            containerColor = BroadcastCrimson,
            contentColor = Color.White,
          ),
      ) {
        Icon(
          imageVector = Icons.Rounded.PlayArrow,
          contentDescription = null,
          modifier = Modifier.size(22.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = stringResource(R.string.start_videos),
          style = MaterialTheme.typography.labelLarge,
        )
      }

      if (uiState.recentVideoIds.isNotEmpty()) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Rounded.History,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = stringResource(R.string.recent_links),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            uiState.recentVideoIds.forEach { videoId ->
              SuggestionChip(
                onClick = { onSelectRecent(videoId) },
                modifier = Modifier.testTag("recent_chip_$videoId"),
                label = {
                  Text(
                    text = videoId,
                    style = MaterialTheme.typography.labelMedium,
                  )
                },
                colors =
                  SuggestionChipDefaults.suggestionChipColors(
                    containerColor = StudioSurfaceElevated,
                    labelColor = MaterialTheme.colorScheme.onSurface,
                  ),
                border = BorderStroke(1.dp, OutlineSubtle),
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ActivePlaybackToolbar(
  activeVideoId: String,
  isMuted: Boolean,
  gridColumnsOverride: Int?,
  isWideScreen: Boolean,
  onToggleMuteAll: () -> Unit,
  onReloadAll: () -> Unit,
  onStopAll: () -> Unit,
  onSelectColumns: (Int?) -> Unit,
) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    color = StudioSurface,
    border = BorderStroke(1.dp, OutlineSubtle),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        FilledTonalButton(
          onClick = onToggleMuteAll,
          modifier = Modifier.testTag("toggle_mute_button"),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
          colors =
            ButtonDefaults.filledTonalButtonColors(
              containerColor = if (isMuted) InputWell else LoopEmeraldContainer,
              contentColor =
                if (isMuted) MaterialTheme.colorScheme.onSurface else LoopEmerald,
            ),
        ) {
          Icon(
            imageVector =
              if (isMuted) {
                Icons.AutoMirrored.Rounded.VolumeOff
              } else {
                Icons.AutoMirrored.Rounded.VolumeUp
              },
            contentDescription = null,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text =
              stringResource(
                if (isMuted) R.string.unmute_all else R.string.mute_all,
              ),
            style = MaterialTheme.typography.labelLarge,
          )
        }

        FilledTonalButton(
          onClick = onReloadAll,
          modifier = Modifier.testTag("reload_all_button"),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
          colors =
            ButtonDefaults.filledTonalButtonColors(
              containerColor = InputWell,
              contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        ) {
          Icon(
            imageVector = Icons.Rounded.Refresh,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = stringResource(R.string.reload_all),
            style = MaterialTheme.typography.labelLarge,
          )
        }

        OutlinedButton(
          onClick = onStopAll,
          modifier = Modifier.testTag("stop_all_button"),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
          border = BorderStroke(1.dp, BroadcastCrimson.copy(alpha = 0.6f)),
          colors =
            ButtonDefaults.outlinedButtonColors(
              contentColor = Color(0xFFFF8A93),
            ),
        ) {
          Icon(
            imageVector = Icons.Rounded.Stop,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = stringResource(R.string.stop_videos),
            style = MaterialTheme.typography.labelLarge,
          )
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          text = "ID: $activeVideoId",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )

        Row(
          modifier = Modifier.horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          val columnChoices: List<Int?> =
            if (isWideScreen) listOf(null, 1, 2, 3, 4) else listOf(null, 1, 2, 3)

          columnChoices.forEach { option ->
            val selected = gridColumnsOverride == option
            FilterChip(
              selected = selected,
              onClick = { onSelectColumns(option) },
              modifier = Modifier.testTag("columns_chip_${option ?: "auto"}"),
              label = {
                Text(
                  text =
                    if (option == null) {
                      stringResource(R.string.columns_auto)
                    } else {
                      "${option}x"
                    },
                  style = MaterialTheme.typography.labelMedium,
                )
              },
              colors =
                FilterChipDefaults.filterChipColors(
                  containerColor = InputWell,
                  selectedContainerColor = StudioSurfaceElevated,
                  labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                  selectedLabelColor = LoopEmerald,
                ),
              border =
                FilterChipDefaults.filterChipBorder(
                  enabled = true,
                  selected = selected,
                  borderColor = OutlineSubtle,
                  selectedBorderColor = LoopEmerald.copy(alpha = 0.7f),
                ),
            )
          }
        }
      }
    }
  }
}

@Composable
private fun VideoWallGrid(
  videoId: String,
  screenCount: Int,
  columns: Int,
  globalReloadNonce: Int,
  slotReloadNonces: Map<Int, Int>,
  isMuted: Boolean,
  onReloadSlot: (Int) -> Unit,
) {
  val indices = (0 until screenCount).toList()
  val rows = indices.chunked(columns)

  Column(
    modifier = Modifier.fillMaxWidth().testTag("video_wall_grid"),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    rows.forEach { rowIndices ->
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        rowIndices.forEach { slotIndex ->
          key(videoId, slotIndex) {
            val slotNonce = globalReloadNonce + (slotReloadNonces[slotIndex] ?: 0)
            YouTubeTileCard(
              slotIndex = slotIndex,
              videoId = videoId,
              reloadKey = slotNonce,
              isMuted = isMuted,
              onReloadSlot = { onReloadSlot(slotIndex) },
              modifier = Modifier.weight(1f),
            )
          }
        }
        repeat(columns - rowIndices.size) {
          Spacer(modifier = Modifier.weight(1f))
        }
      }
    }
  }
}

@Composable
private fun StudioEmptyStateCard() {
  Surface(
    modifier = Modifier.fillMaxWidth().testTag("empty_state_card"),
    shape = RoundedCornerShape(20.dp),
    color = StudioSurface,
    border = BorderStroke(1.dp, OutlineSubtle),
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Box(
        modifier =
          Modifier.fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
      ) {
        Image(
          painter = painterResource(id = R.drawable.img_studio_empty_state),
          contentDescription = stringResource(R.string.empty_state_image_desc),
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop,
        )
        Box(
          modifier =
            Modifier.fillMaxSize()
              .background(
                Brush.verticalGradient(
                  colors = listOf(Color.Transparent, StudioSurface.copy(alpha = 0.92f)),
                ),
              ),
        )
      }

      Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Text(
          text = stringResource(R.string.empty_state_title),
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
          text = stringResource(R.string.empty_state_desc),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          QuickStepRow(text = stringResource(R.string.step_1))
          QuickStepRow(text = stringResource(R.string.step_2))
          QuickStepRow(text = stringResource(R.string.step_3))
        }
      }
    }
  }
}

@Composable
private fun QuickStepRow(text: String) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = InputWell,
    border = BorderStroke(1.dp, OutlineSubtle),
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurface,
      modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
    )
  }
}
