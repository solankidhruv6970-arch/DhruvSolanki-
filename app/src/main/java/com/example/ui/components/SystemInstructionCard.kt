package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StudioBlue
import com.example.ui.theme.StudioCyan

@Composable
fun SystemInstructionCard(
  systemInstruction: String,
  onSystemInstructionChange: (String) -> Unit,
  isExpanded: Boolean,
  onToggleExpand: () -> Unit,
  onSelectPreset: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val presets = listOf(
    "Helpful Assistant" to "You are a helpful, accurate, and friendly AI assistant. Give structured, concise, and verifiable explanations.",
    "Code Architect" to "You are an elite Software Architect. Write high-quality, production-ready code with idiomatic design patterns, error handling, and complexity analysis.",
    "JSON Extractor" to "You are a strict data extraction model. Always output only valid JSON without explanatory markdown or surrounding text.",
    "Creative Writer" to "You are an award-winning creative author. Craft vivid sensory details, compelling prose, and evocative metaphors."
  )

  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(10.dp),
    color = MaterialTheme.colorScheme.surface,
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
    tonalElevation = 1.dp
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(6.dp))
          .clickable { onToggleExpand() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Psychology,
            contentDescription = null,
            tint = StudioCyan,
            modifier = Modifier.size(20.dp)
          )
          Column {
            Text(
              text = "System instructions",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface
            )
            if (!isExpanded) {
              Text(
                text = if (systemInstruction.isBlank()) "Optional instructions to guide model behavior" else systemInstruction.take(60) + "...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
              )
            }
          }
        }

        IconButton(
          onClick = onToggleExpand,
          modifier = Modifier.size(28.dp).testTag("toggle_system_instruction")
        ) {
          Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = "Toggle system instructions",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      AnimatedVisibility(visible = isExpanded) {
        Column(
          modifier = Modifier.padding(top = 10.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = systemInstruction,
            onValueChange = onSystemInstructionChange,
            modifier = Modifier
              .fillMaxWidth()
              .heightIn(min = 90.dp, max = 180.dp)
              .testTag("system_instruction_input"),
            placeholder = {
              Text(
                text = "e.g. You are a senior engineer who writes concise Python code...",
                style = MaterialTheme.typography.bodyMedium
              )
            },
            textStyle = MaterialTheme.typography.bodyMedium,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = StudioBlue,
              unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
          )

          // Presets chip row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Presets:",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            presets.forEach { (label, text) ->
              AssistChip(
                onClick = { onSelectPreset(text) },
                label = { Text(label, style = MaterialTheme.typography.labelSmall, fontSize = 11.sp) },
                modifier = Modifier.testTag("system_preset_${label.lowercase().replace(" ", "_")}")
              )
            }
          }
        }
      }
    }
  }
}
