package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ExecutionMetrics
import com.example.model.GenerationStatus
import com.example.ui.theme.*

@Composable
fun ResponseOutputCard(
  generationStatus: GenerationStatus,
  onRegenerate: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var thumbUp by remember { mutableStateOf<Boolean?>(null) }

  when (generationStatus) {
    is GenerationStatus.Idle -> {
      Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(32.dp)
          )
          Text(
            text = "Model output will appear here",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "Click 'Run' or press Ctrl+Enter to test the prompt against Gemini",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            fontSize = 11.sp
          )
        }
      }
    }

    is GenerationStatus.Streaming -> {
      Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBlue.copy(alpha = 0.6f)),
        tonalElevation = 2.dp
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = StudioBlue
              )
              Text(
                text = "Generating response...",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = StudioBlue
              )
            }
          }

          FormattedMarkdownText(
            text = generationStatus.partialText.ifBlank { "Thinking..." },
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }

    is GenerationStatus.Success -> {
      Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        tonalElevation = 2.dp
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Top bar: Model output badge + Action buttons (Copy, Thumbs, Regenerate)
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
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = StudioGreen,
                modifier = Modifier.size(16.dp)
              )
              Text(
                text = "Response",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              // Copy Button
              IconButton(
                onClick = {
                  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                  val clip = ClipData.newPlainText("Gemini Response", generationStatus.responseText)
                  clipboard.setPrimaryClip(clip)
                  Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.size(32.dp).testTag("copy_response_button")
              ) {
                Icon(
                  imageVector = Icons.Default.ContentCopy,
                  contentDescription = "Copy response",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(18.dp)
                )
              }

              // Thumbs Up
              IconButton(
                onClick = {
                  thumbUp = if (thumbUp == true) null else true
                  Toast.makeText(context, "Feedback recorded", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.size(32.dp).testTag("thumbs_up_button")
              ) {
                Icon(
                  imageVector = if (thumbUp == true) Icons.Default.ThumbUp else Icons.Outlined.ThumbUp,
                  contentDescription = "Thumbs up",
                  tint = if (thumbUp == true) StudioBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(18.dp)
                )
              }

              // Thumbs Down
              IconButton(
                onClick = {
                  thumbUp = if (thumbUp == false) null else false
                  Toast.makeText(context, "Feedback recorded", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.size(32.dp).testTag("thumbs_down_button")
              ) {
                Icon(
                  imageVector = if (thumbUp == false) Icons.Default.ThumbDown else Icons.Outlined.ThumbDown,
                  contentDescription = "Thumbs down",
                  tint = if (thumbUp == false) StudioRed else MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(18.dp)
                )
              }

              // Regenerate
              IconButton(
                onClick = onRegenerate,
                modifier = Modifier.size(32.dp).testTag("regenerate_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Refresh,
                  contentDescription = "Regenerate",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          }

          // Main Response Content with Markdown Rendering
          FormattedMarkdownText(
            text = generationStatus.responseText,
            modifier = Modifier.fillMaxWidth().testTag("response_text_view")
          )

          // Code Execution Result Box (if present)
          if (generationStatus.executedCode != null) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = CodeBgDark,
              border = androidx.compose.foundation.BorderStroke(1.dp, StudioPurple.copy(alpha = 0.5f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = null,
                    tint = StudioPurple,
                    modifier = Modifier.size(16.dp)
                  )
                  Text(
                    text = "Executed Code (Python)",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = StudioPurple
                  )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = generationStatus.executedCode,
                  style = CodeTextStyle,
                  color = Color(0xFFD4D4D4)
                )
                if (generationStatus.codeOutput != null) {
                  HorizontalDivider(
                    modifier = Modifier.padding(vertical = 6.dp),
                    color = StudioDarkBorder
                  )
                  Text(
                    text = ">>> Output:\n${generationStatus.codeOutput}",
                    style = CodeTextStyle,
                    color = StudioGreen
                  )
                }
              }
            }
          }

          // Google Search Grounding Sources (if present)
          if (generationStatus.searchSources.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.TravelExplore,
                  contentDescription = null,
                  tint = StudioGreen,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = "Grounding Sources",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                  color = StudioGreen
                )
              }
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                generationStatus.searchSources.forEach { source ->
                  SuggestionChip(
                    onClick = {
                      Toast.makeText(context, "Source: $source", Toast.LENGTH_SHORT).show()
                    },
                    label = {
                      Text(
                        text = source.replace("https://", "").take(28) + "...",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp
                      )
                    },
                    icon = {
                      Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(14.dp))
                    }
                  )
                }
              }
            }
          }

          HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))

          // Performance Metrics Badge (Token count, Latency, Speed)
          PerformanceMetricsRow(metrics = generationStatus.metrics)
        }
      }
    }

    is GenerationStatus.Error -> {
      Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error)
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(24.dp)
          )
          Column {
            Text(
              text = "Generation Error",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.error
            )
            Text(
              text = generationStatus.message,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onErrorContainer
            )
          }
        }
      }
    }
  }
}

@Composable
fun PerformanceMetricsRow(metrics: ExecutionMetrics) {
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
        imageVector = Icons.Default.Speed,
        contentDescription = null,
        tint = StudioBlue,
        modifier = Modifier.size(14.dp)
      )
      Text(
        text = "${metrics.totalTokens} tokens (${metrics.inputTokens} in · ${metrics.outputTokens} out)",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Text(
        text = "${metrics.latencySeconds}s",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Text(
        text = "${metrics.tokensPerSec} t/s",
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = StudioBlue
      )
      Surface(
        shape = RoundedCornerShape(4.dp),
        color = StudioGreen.copy(alpha = 0.2f)
      ) {
        Text(
          text = metrics.finishReason,
          style = MaterialTheme.typography.labelSmall,
          color = StudioGreen,
          modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
          fontSize = 10.sp
        )
      }
    }
  }
}

@Composable
fun FormattedMarkdownText(
  text: String,
  modifier: Modifier = Modifier
) {
  val lines = text.split("\n")
  var inCodeBlock = false
  val codeBlockBuilder = remember { StringBuilder() }

  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
    lines.forEach { line ->
      when {
        line.startsWith("```") -> {
          if (inCodeBlock) {
            // End of code block
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CodeBgDark,
              border = androidx.compose.foundation.BorderStroke(1.dp, StudioDarkBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = codeBlockBuilder.toString().trimEnd(),
                style = CodeTextStyle,
                color = Color(0xFFD4D4D4),
                modifier = Modifier.padding(10.dp)
              )
            }
            codeBlockBuilder.clear()
            inCodeBlock = false
          } else {
            inCodeBlock = true
          }
        }
        inCodeBlock -> {
          codeBlockBuilder.append(line).append("\n")
        }
        line.startsWith("### ") -> {
          Text(
            text = line.removePrefix("### "),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 6.dp)
          )
        }
        line.startsWith("## ") -> {
          Text(
            text = line.removePrefix("## "),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 8.dp)
          )
        }
        line.startsWith("# ") -> {
          Text(
            text = line.removePrefix("# "),
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 10.dp)
          )
        }
        line.startsWith("- ") || line.startsWith("* ") -> {
          Row(
            modifier = Modifier.padding(start = 8.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = "•",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
              color = StudioBlue
            )
            Text(
              text = line.substring(2),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }
        else -> {
          if (line.isNotBlank()) {
            Text(
              text = line,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurface
            )
          } else {
            Spacer(modifier = Modifier.height(4.dp))
          }
        }
      }
    }
  }
}
