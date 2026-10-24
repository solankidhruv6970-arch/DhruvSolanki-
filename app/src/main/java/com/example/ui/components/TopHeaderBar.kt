package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AvailableGeminiModels
import com.example.model.GeminiModelOption
import com.example.model.GenerationStatus
import com.example.model.PromptType
import com.example.ui.theme.*

@Composable
fun TopHeaderBar(
  currentPromptType: PromptType,
  onSelectPromptType: (PromptType) -> Unit,
  selectedModel: GeminiModelOption,
  onSelectModel: (GeminiModelOption) -> Unit,
  generationStatus: GenerationStatus,
  onRunClicked: () -> Unit,
  onStopClicked: () -> Unit,
  onGetCodeClicked: () -> Unit,
  onToggleLeftDrawer: () -> Unit,
  onToggleRightPanel: () -> Unit,
  isRightPanelOpen: Boolean,
  isDarkMode: Boolean,
  onToggleTheme: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isModelDropdownOpen by remember { mutableStateOf(false) }
  val isRunning = generationStatus is GenerationStatus.Streaming

  Surface(
    modifier = modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 2.dp,
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // Top row: Brand + Run + Get Code + Settings + Nav
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // Left side: Sidebar Toggle + Brand Logo
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          IconButton(
            onClick = onToggleLeftDrawer,
            modifier = Modifier
              .size(40.dp)
              .testTag("left_sidebar_toggle")
          ) {
            Icon(
              imageVector = Icons.Default.Menu,
              contentDescription = "Toggle Sidebar",
              tint = MaterialTheme.colorScheme.onSurface
            )
          }

          // Studio Sparkle Logo & Name
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .clickable { onToggleLeftDrawer() }
              .padding(horizontal = 4.dp, vertical = 4.dp)
          ) {
            Box(
              modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(
                  Brush.linearGradient(
                    colors = listOf(StudioCyan, StudioBlue, StudioPurple)
                  )
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "AI Studio Logo",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Google AI Studio",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 16.sp
              )
              Text(
                text = "Playground",
                style = MaterialTheme.typography.labelSmall,
                color = StudioBlue,
                fontSize = 10.sp
              )
            }
          }
        }

        // Right side: Get Code + Run Action + Model Badge + Sliders
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Model Selector Pill (Interactive Dropdown)
          Box {
            Surface(
              onClick = { isModelDropdownOpen = true },
              shape = RoundedCornerShape(16.dp),
              color = MaterialTheme.colorScheme.surfaceVariant,
              border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
              modifier = Modifier.testTag("model_selector_badge")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Text(
                  text = selectedModel.displayName.replace("Gemini ", ""),
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                  imageVector = Icons.Default.ArrowDropDown,
                  contentDescription = "Select Model",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(18.dp)
                )
              }
            }

            DropdownMenu(
              expanded = isModelDropdownOpen,
              onDismissRequest = { isModelDropdownOpen = false },
              modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
              AvailableGeminiModels.forEach { modelOption ->
                DropdownMenuItem(
                  text = {
                    Column {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                          text = modelOption.displayName,
                          fontWeight = if (modelOption.id == selectedModel.id) FontWeight.Bold else FontWeight.Normal,
                          color = if (modelOption.id == selectedModel.id) StudioBlue else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                          text = modelOption.speedTier,
                          style = MaterialTheme.typography.labelSmall,
                          color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                      }
                      Text(
                        text = modelOption.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                  },
                  onClick = {
                    onSelectModel(modelOption)
                    isModelDropdownOpen = false
                  },
                  leadingIcon = {
                    if (modelOption.id == selectedModel.id) {
                      Icon(Icons.Default.Check, contentDescription = "Selected", tint = StudioBlue)
                    }
                  }
                )
              }
            }
          }

          // Get Code Button
          OutlinedButton(
            onClick = onGetCodeClicked,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.testTag("get_code_button")
          ) {
            Icon(
              imageVector = Icons.Default.Code,
              contentDescription = "Get Code",
              modifier = Modifier.size(16.dp),
              tint = StudioBlue
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Get code",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          // Run / Stop Action Button
          if (isRunning) {
            Button(
              onClick = onStopClicked,
              colors = ButtonDefaults.buttonColors(
                containerColor = StudioRed,
                contentColor = Color.White
              ),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier.testTag("stop_run_button")
            ) {
              CircularProgressIndicator(
                modifier = Modifier.size(14.dp),
                color = Color.White,
                strokeWidth = 2.dp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Stop",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
              )
            }
          } else {
            Button(
              onClick = onRunClicked,
              colors = ButtonDefaults.buttonColors(
                containerColor = StudioBlueDark,
                contentColor = Color.White
              ),
              contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier.testTag("run_prompt_button")
            ) {
              Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Run Prompt",
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Run",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
              )
            }
          }

          // Parameters Settings Toggle
          IconButton(
            onClick = onToggleRightPanel,
            modifier = Modifier
              .size(38.dp)
              .testTag("toggle_parameters_panel")
          ) {
            Icon(
              imageVector = if (isRightPanelOpen) Icons.Default.Tune else Icons.Outlined.Tune,
              contentDescription = "Toggle Parameters",
              tint = if (isRightPanelOpen) StudioBlue else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // Prompt Type Selector Tabs row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
          .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        PromptType.values().forEach { promptType ->
          val isSelected = currentPromptType == promptType
          val icon = when (promptType) {
            PromptType.FREEFORM -> Icons.Default.EditNote
            PromptType.CHAT -> Icons.Default.Forum
            PromptType.STRUCTURED -> Icons.Default.TableChart
          }

          Surface(
            onClick = { onSelectPromptType(promptType) },
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, StudioBlue.copy(alpha = 0.5f)) else null,
            modifier = Modifier.testTag("prompt_type_tab_${promptType.name.lowercase()}")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                imageVector = icon,
                contentDescription = promptType.title,
                modifier = Modifier.size(16.dp),
                tint = if (isSelected) StudioBlue else MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = "${promptType.title} prompt",
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }
  }
}
