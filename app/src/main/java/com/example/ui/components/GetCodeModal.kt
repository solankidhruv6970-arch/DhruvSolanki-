package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.CodeGenerator
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun GetCodeModal(
  selectedModel: GeminiModelOption,
  prompt: String,
  systemInstruction: String,
  temperature: Float,
  topP: Float,
  topK: Int,
  maxTokens: Int,
  searchGrounding: Boolean,
  codeExecution: Boolean,
  safetyThresholds: Map<SafetyCategory, SafetyThreshold>,
  selectedLanguage: ExportLanguage,
  onSelectLanguage: (ExportLanguage) -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val codeSnippet = remember(
    selectedLanguage,
    selectedModel,
    prompt,
    systemInstruction,
    temperature,
    topP,
    topK,
    maxTokens,
    searchGrounding,
    codeExecution
  ) {
    CodeGenerator.generateSnippet(
      language = selectedLanguage,
      model = selectedModel,
      prompt = prompt,
      systemInstruction = systemInstruction,
      temperature = temperature,
      topP = topP,
      topK = topK,
      maxTokens = maxTokens,
      searchGrounding = searchGrounding,
      codeExecution = codeExecution,
      safetyThresholds = safetyThresholds
    )
  }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .heightIn(max = 620.dp),
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.surface,
      border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
      tonalElevation = 6.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Modal Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Get code",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Export this prompt to your application codebase",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 11.sp
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp).testTag("close_get_code_modal")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Language Tabs
        ScrollableTabRow(
          selectedTabIndex = selectedLanguage.ordinal,
          edgePadding = 0.dp,
          containerColor = Color.Transparent,
          divider = { HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)) }
        ) {
          ExportLanguage.values().forEach { lang ->
            Tab(
              selected = selectedLanguage == lang,
              onClick = { onSelectLanguage(lang) },
              text = {
                Text(
                  text = lang.displayName,
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (selectedLanguage == lang) FontWeight.Bold else FontWeight.Normal
                  ),
                  color = if (selectedLanguage == lang) StudioBlue else MaterialTheme.colorScheme.onSurfaceVariant
                )
              },
              modifier = Modifier.testTag("export_tab_${lang.ext}")
            )
          }
        }

        // Code Display Area
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = CodeBgDark,
          border = androidx.compose.foundation.BorderStroke(1.dp, StudioDarkBorder),
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
        ) {
          Box(modifier = Modifier.fillMaxSize()) {
            val vScroll = rememberScrollState()
            val hScroll = rememberScrollState()

            Text(
              text = codeSnippet,
              style = CodeTextStyle,
              color = Color(0xFFE6EDF3),
              modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
                .verticalScroll(vScroll)
                .horizontalScroll(hScroll)
                .testTag("code_snippet_content")
            )

            // Floating copy icon inside code box
            IconButton(
              onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Code snippet", codeSnippet)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Code copied to clipboard!", Toast.LENGTH_SHORT).show()
              },
              modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(36.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                .testTag("modal_copy_code_button")
            ) {
              Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy code",
                tint = StudioBlue,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }

        // Bottom Actions: Copy snippet button + Done
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Uses official Google GenAI SDK",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
          )

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
              onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Code snippet", codeSnippet)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Code copied to clipboard!", Toast.LENGTH_SHORT).show()
              },
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(
                Icons.Default.ContentCopy,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = StudioBlue
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text("Copy Code")
            }

            Button(
              onClick = onDismiss,
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("Done")
            }
          }
        }
      }
    }
  }
}
