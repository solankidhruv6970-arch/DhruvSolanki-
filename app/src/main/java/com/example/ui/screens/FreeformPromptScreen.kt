package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun FreeformPromptScreen(
  prompt: String,
  onPromptChange: (String) -> Unit,
  systemInstruction: String,
  onSystemInstructionChange: (String) -> Unit,
  isSystemExpanded: Boolean,
  onToggleSystemExpanded: () -> Unit,
  onSelectSystemPreset: (String) -> Unit,
  attachments: List<MediaAttachment>,
  onAddAttachment: (MediaAttachment) -> Unit,
  onRemoveAttachment: (String) -> Unit,
  googleSearchGrounding: Boolean,
  onToggleSearchGrounding: () -> Unit,
  codeExecution: Boolean,
  onToggleCodeExecution: () -> Unit,
  generationStatus: GenerationStatus,
  onRunPrompt: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  val promptSuggestions = listOf(
    "Explain quantum superposition simply" to "Explain quantum superposition and quantum entanglement in intuitive, simple terms with real-world analogies.",
    "Write a Kotlin Compose animation" to "Write a production-ready Jetpack Compose composable function featuring an interactive breathing pulsing circle animation with spring physics.",
    "Extract structured JSON from text" to "Extract customer name, order ID, items list, and total amount into a strict JSON object from this receipt note: 'Customer Jane Doe (ID: #8921) bought 2x Blue Keyboards ($45 each) and 1x USB-C Cable ($15). Tax was $9. Total: $114.'",
    "Analyze algorithmic complexity" to "Compare the time and space complexity of MergeSort vs QuickSort, explaining best, average, and worst-case scenarios and in-place memory usage."
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. System Instructions Box
    SystemInstructionCard(
      systemInstruction = systemInstruction,
      onSystemInstructionChange = onSystemInstructionChange,
      isExpanded = isSystemExpanded,
      onToggleExpand = onToggleSystemExpanded,
      onSelectPreset = onSelectSystemPreset
    )

    // 2. Multimodal Input Media Bar
    MediaAttachmentBar(
      attachments = attachments,
      onAddAttachment = onAddAttachment,
      onRemoveAttachment = onRemoveAttachment
    )

    // 3. Prompt Input Box
    Surface(
      shape = RoundedCornerShape(10.dp),
      color = MaterialTheme.colorScheme.surface,
      border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
      tonalElevation = 1.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
              imageVector = Icons.Default.Edit,
              contentDescription = null,
              tint = StudioBlue,
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = "User prompt",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          if (prompt.isNotBlank()) {
            IconButton(
              onClick = { onPromptChange("") },
              modifier = Modifier.size(24.dp).testTag("clear_prompt_button")
            ) {
              Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Clear prompt",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }

        OutlinedTextField(
          value = prompt,
          onValueChange = onPromptChange,
          placeholder = {
            Text(
              text = "Write a prompt or type '@' to insert a file, schema, or system instruction...",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
          },
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp, max = 260.dp)
            .testTag("freeform_prompt_input"),
          textStyle = MaterialTheme.typography.bodyLarge,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = StudioBlue,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
          )
        )

        // Suggestion Chips
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = "Try an example prompt:",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            promptSuggestions.take(2).forEach { (label, text) ->
              AssistChip(
                onClick = { onPromptChange(text) },
                label = { Text(label, style = MaterialTheme.typography.labelSmall, fontSize = 11.sp) },
                modifier = Modifier.testTag("prompt_suggestion_${label.lowercase().take(10)}")
              )
            }
          }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))

        // Tools Configuration Toggles within the workspace
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Google Search Grounding badge/toggle
            FilterChip(
              selected = googleSearchGrounding,
              onClick = onToggleSearchGrounding,
              label = {
                Text(
                  text = "Search Grounding",
                  style = MaterialTheme.typography.labelSmall
                )
              },
              leadingIcon = {
                Icon(
                  Icons.Default.Language,
                  contentDescription = null,
                  modifier = Modifier.size(14.dp),
                  tint = if (googleSearchGrounding) StudioGreen else MaterialTheme.colorScheme.onSurfaceVariant
                )
              },
              modifier = Modifier.testTag("workspace_search_grounding_chip")
            )

            // Code Execution badge/toggle
            FilterChip(
              selected = codeExecution,
              onClick = onToggleCodeExecution,
              label = {
                Text(
                  text = "Code Execution",
                  style = MaterialTheme.typography.labelSmall
                )
              },
              leadingIcon = {
                Icon(
                  Icons.Default.Terminal,
                  contentDescription = null,
                  modifier = Modifier.size(14.dp),
                  tint = if (codeExecution) StudioPurple else MaterialTheme.colorScheme.onSurfaceVariant
                )
              },
              modifier = Modifier.testTag("workspace_code_execution_chip")
            )
          }

          // Character / token count preview
          Text(
            text = "~${(prompt.length / 4).coerceAtLeast(0)} tokens",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    // 4. Output Stream Area
    ResponseOutputCard(
      generationStatus = generationStatus,
      onRegenerate = onRunPrompt
    )
  }
}
