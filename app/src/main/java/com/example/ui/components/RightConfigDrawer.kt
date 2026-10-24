package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun RightConfigDrawer(
  selectedModel: GeminiModelOption,
  onSelectModel: (GeminiModelOption) -> Unit,
  temperature: Float,
  onTemperatureChanged: (Float) -> Unit,
  topP: Float,
  onTopPChanged: (Float) -> Unit,
  topK: Int,
  onTopKChanged: (Int) -> Unit,
  maxOutputTokens: Int,
  onMaxOutputTokensChanged: (Int) -> Unit,
  googleSearchGrounding: Boolean,
  onToggleSearchGrounding: () -> Unit,
  codeExecution: Boolean,
  onToggleCodeExecution: () -> Unit,
  safetyThresholds: Map<SafetyCategory, SafetyThreshold>,
  onUpdateSafety: (SafetyCategory, SafetyThreshold) -> Unit,
  onResetDefaults: () -> Unit,
  onClose: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()
  var isSafetyExpanded by remember { mutableStateOf(true) }

  Surface(
    modifier = modifier
      .fillMaxHeight()
      .widthIn(max = 340.dp),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 3.dp,
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .verticalScroll(scrollState),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Header: Run Settings + Close
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Tune,
            contentDescription = null,
            tint = StudioBlue,
            modifier = Modifier.size(20.dp)
          )
          Text(
            text = "Run settings",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }
        IconButton(
          onClick = onClose,
          modifier = Modifier.size(32.dp).testTag("close_parameters_panel")
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close settings",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

      // Model Selector Card
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = "Model",
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.onSurface
        )

        AvailableGeminiModels.forEach { model ->
          val isSelected = model.id == selectedModel.id
          Surface(
            onClick = { onSelectModel(model) },
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (isSelected) StudioBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            ),
            modifier = Modifier.fillMaxWidth().testTag("config_model_${model.id}")
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = model.displayName,
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                  color = if (isSelected) StudioBlue else MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = model.speedTier,
                  style = MaterialTheme.typography.labelSmall,
                  color = if (isSelected) StudioBlue else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "${model.contextWindow} · ${model.description}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
              )
            }
          }
        }
      }

      HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

      // Temperature Slider (0.0 to 2.0)
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Temperature",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
          )
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.padding(2.dp)
          ) {
            Text(
              text = String.format("%.2f", temperature),
              style = MaterialTheme.typography.labelMedium,
              color = StudioBlue,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
        Slider(
          value = temperature,
          onValueChange = onTemperatureChanged,
          valueRange = 0.0f..2.0f,
          colors = SliderDefaults.colors(
            thumbColor = StudioBlue,
            activeTrackColor = StudioBlue,
            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
          ),
          modifier = Modifier.testTag("temperature_slider")
        )
        Text(
          text = "Controls randomness. Lower values are more deterministic; higher values allow creative diversity.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 11.sp
        )
      }

      // Top-P Slider (0.0 to 1.0)
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Top-P (Nucleus sampling)",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
          )
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
          ) {
            Text(
              text = String.format("%.2f", topP),
              style = MaterialTheme.typography.labelMedium,
              color = StudioBlue,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
        Slider(
          value = topP,
          onValueChange = onTopPChanged,
          valueRange = 0.0f..1.0f,
          colors = SliderDefaults.colors(
            thumbColor = StudioBlue,
            activeTrackColor = StudioBlue,
            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
          ),
          modifier = Modifier.testTag("top_p_slider")
        )
      }

      // Top-K Slider (1 to 64)
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Top-K",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
          )
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
          ) {
            Text(
              text = topK.toString(),
              style = MaterialTheme.typography.labelMedium,
              color = StudioBlue,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
        Slider(
          value = topK.toFloat(),
          onValueChange = { onTopKChanged(it.toInt()) },
          valueRange = 1f..64f,
          colors = SliderDefaults.colors(
            thumbColor = StudioBlue,
            activeTrackColor = StudioBlue,
            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
          ),
          modifier = Modifier.testTag("top_k_slider")
        )
      }

      // Max Output Tokens Slider (1 to 8192)
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Max output tokens",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
          )
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
          ) {
            Text(
              text = maxOutputTokens.toString(),
              style = MaterialTheme.typography.labelMedium,
              color = StudioBlue,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
        Slider(
          value = maxOutputTokens.toFloat(),
          onValueChange = { onMaxOutputTokensChanged(it.toInt()) },
          valueRange = 64f..8192f,
          colors = SliderDefaults.colors(
            thumbColor = StudioBlue,
            activeTrackColor = StudioBlue,
            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
          ),
          modifier = Modifier.testTag("max_tokens_slider")
        )
      }

      HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

      // Tools Toggles
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          text = "Tools",
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.onSurface
        )

        // Google Search Grounding Toggle
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = if (googleSearchGrounding) StudioGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
              )
              Column {
                Text(
                  text = "Google Search Grounding",
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "Connect to live web sources",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 11.sp
                )
              }
            }
            Switch(
              checked = googleSearchGrounding,
              onCheckedChange = { onToggleSearchGrounding() },
              colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = StudioGreen
              ),
              modifier = Modifier.testTag("google_search_grounding_switch")
            )
          }
        }

        // Code Execution Toggle
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              Icon(
                imageVector = Icons.Default.Terminal,
                contentDescription = null,
                tint = if (codeExecution) StudioPurple else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
              )
              Column {
                Text(
                  text = "Code Execution",
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "Run Python in secure sandbox",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 11.sp
                )
              }
            }
            Switch(
              checked = codeExecution,
              onCheckedChange = { onToggleCodeExecution() },
              colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = StudioPurple
              ),
              modifier = Modifier.testTag("code_execution_switch")
            )
          }
        }
      }

      HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

      // Safety Settings Collapsible Section
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = null,
              tint = StudioBlue,
              modifier = Modifier.size(18.dp)
            )
            Text(
              text = "Safety settings",
              style = MaterialTheme.typography.labelLarge,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
          IconButton(
            onClick = { isSafetyExpanded = !isSafetyExpanded },
            modifier = Modifier.size(28.dp)
          ) {
            Icon(
              imageVector = if (isSafetyExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
              contentDescription = "Expand safety",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        AnimatedVisibility(visible = isSafetyExpanded) {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SafetyCategory.values().forEach { category ->
              val currentThreshold = safetyThresholds[category] ?: SafetyThreshold.BLOCK_SOME
              var isCategoryDropdownOpen by remember { mutableStateOf(false) }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = category.displayName,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                  )

                  Box {
                    TextButton(
                      onClick = { isCategoryDropdownOpen = true },
                      contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                      Text(
                        text = currentThreshold.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = StudioBlue
                      )
                      Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = StudioBlue
                      )
                    }

                    DropdownMenu(
                      expanded = isCategoryDropdownOpen,
                      onDismissRequest = { isCategoryDropdownOpen = false }
                    ) {
                      SafetyThreshold.values().forEach { thresh ->
                        DropdownMenuItem(
                          text = {
                            Text(
                              text = thresh.label,
                              fontWeight = if (thresh == currentThreshold) FontWeight.Bold else FontWeight.Normal
                            )
                          },
                          onClick = {
                            onUpdateSafety(category, thresh)
                            isCategoryDropdownOpen = false
                          }
                        )
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

      HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

      // Reset Button
      OutlinedButton(
        onClick = onResetDefaults,
        modifier = Modifier.fillMaxWidth().testTag("reset_parameters_button"),
        shape = RoundedCornerShape(8.dp)
      ) {
        Icon(
          Icons.Default.RestartAlt,
          contentDescription = null,
          modifier = Modifier.size(16.dp),
          tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Reset to defaults",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}
