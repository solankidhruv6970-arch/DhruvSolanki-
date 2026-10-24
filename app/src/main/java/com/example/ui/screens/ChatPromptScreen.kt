package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.model.ChatTurn
import com.example.model.GenerationStatus
import com.example.model.MediaAttachment
import com.example.ui.components.FormattedMarkdownText
import com.example.ui.components.MediaAttachmentBar
import com.example.ui.components.SystemInstructionCard
import com.example.ui.theme.*

@Composable
fun ChatPromptScreen(
  chatTurns: List<ChatTurn>,
  currentMessage: String,
  onMessageChange: (String) -> Unit,
  onSendMessage: () -> Unit,
  onAddTurn: (String) -> Unit,
  onUpdateTurnText: (String, String) -> Unit,
  onDeleteTurn: (String) -> Unit,
  onClearChat: () -> Unit,
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
  generationStatus: GenerationStatus,
  modifier: Modifier = Modifier
) {
  val listState = rememberLazyListState()

  // Scroll to bottom when new turn arrives
  LaunchedEffect(chatTurns.size, (chatTurns.lastOrNull()?.text?.length ?: 0)) {
    if (chatTurns.isNotEmpty()) {
      listState.animateScrollToItem(chatTurns.size + 1)
    }
  }

  Column(
    modifier = modifier.fillMaxSize(),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    // Scrollable Turns Area
    LazyColumn(
      state = listState,
      modifier = Modifier
        .weight(1f)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
      contentPadding = PaddingValues(vertical = 14.dp)
    ) {
      // 1. System Instruction Box at top
      item {
        SystemInstructionCard(
          systemInstruction = systemInstruction,
          onSystemInstructionChange = onSystemInstructionChange,
          isExpanded = isSystemExpanded,
          onToggleExpand = onToggleSystemExpanded,
          onSelectPreset = onSelectSystemPreset
        )
      }

      // Action Row for Chat Turns: Clear Conversation + Add Turn Buttons
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "${chatTurns.size} turns in session",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedButton(
              onClick = { onAddTurn("user") },
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.testTag("add_user_turn_button")
            ) {
              Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("+ User", style = MaterialTheme.typography.labelSmall)
            }

            OutlinedButton(
              onClick = { onAddTurn("model") },
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.testTag("add_model_turn_button")
            ) {
              Icon(Icons.Default.SmartToy, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("+ Model", style = MaterialTheme.typography.labelSmall)
            }

            if (chatTurns.isNotEmpty()) {
              IconButton(
                onClick = onClearChat,
                modifier = Modifier.size(28.dp).testTag("clear_chat_button")
              ) {
                Icon(
                  Icons.Default.DeleteSweep,
                  contentDescription = "Clear chat",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          }
        }
      }

      // 2. Chat Turns List
      items(chatTurns, key = { it.id }) { turn ->
        ChatTurnCard(
          turn = turn,
          onUpdateText = { newText -> onUpdateTurnText(turn.id, newText) },
          onDelete = { onDeleteTurn(turn.id) }
        )
      }

      if (chatTurns.isEmpty()) {
        item {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                Icons.Default.Forum,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
              )
              Text(
                text = "No conversation turns yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Type a message below or click '+ User' / '+ Model' to build multi-turn context",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }

    // Bottom Input Bar
    Surface(
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 4.dp,
      border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Attachment bar if any attached
        MediaAttachmentBar(
          attachments = attachments,
          onAddAttachment = onAddAttachment,
          onRemoveAttachment = onRemoveAttachment
        )

        // Tools toggle row in chat
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          FilterChip(
            selected = googleSearchGrounding,
            onClick = onToggleSearchGrounding,
            label = { Text("Search Grounding", style = MaterialTheme.typography.labelSmall) },
            leadingIcon = {
              Icon(
                Icons.Default.Language,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = if (googleSearchGrounding) StudioGreen else MaterialTheme.colorScheme.onSurfaceVariant
              )
            },
            modifier = Modifier.testTag("chat_search_grounding_chip")
          )

          Text(
            text = "Press Send or Run to test",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
          )
        }

        // Message text input row
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = currentMessage,
            onValueChange = onMessageChange,
            placeholder = { Text("Type a prompt or message...", style = MaterialTheme.typography.bodyMedium) },
            modifier = Modifier
              .weight(1f)
              .testTag("chat_message_input"),
            textStyle = MaterialTheme.typography.bodyMedium,
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = StudioBlue,
              unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            ),
            trailingIcon = {
              if (currentMessage.isNotBlank()) {
                IconButton(onClick = { onMessageChange("") }) {
                  Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                }
              }
            }
          )

          val isGenerating = generationStatus is GenerationStatus.Streaming
          IconButton(
            onClick = onSendMessage,
            enabled = currentMessage.isNotBlank() || attachments.isNotEmpty(),
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(if (currentMessage.isNotBlank()) StudioBlueDark else MaterialTheme.colorScheme.surfaceVariant)
              .testTag("send_chat_message_button")
          ) {
            if (isGenerating) {
              CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = Color.White,
                strokeWidth = 2.dp
              )
            } else {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = if (currentMessage.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ChatTurnCard(
  turn: ChatTurn,
  onUpdateText: (String) -> Unit,
  onDelete: () -> Unit
) {
  val isUser = turn.role.equals("user", ignoreCase = true)
  var isEditing by remember { mutableStateOf(false) }
  var editText by remember(turn.text) { mutableStateOf(turn.text) }

  Surface(
    shape = RoundedCornerShape(10.dp),
    color = if (isUser) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface,
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (isUser) StudioBlue.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    ),
    modifier = Modifier.fillMaxWidth().testTag("chat_turn_${turn.id}")
  ) {
    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      // Role Header & Action Icons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(if (isUser) StudioBlueDark else StudioPurple),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isUser) Icons.Default.Person else Icons.Default.SmartToy,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(14.dp)
            )
          }

          Text(
            text = if (isUser) "User" else "Model",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = if (isUser) StudioBlue else StudioPurple
          )

          if (turn.isStreaming) {
            CircularProgressIndicator(
              modifier = Modifier.size(12.dp),
              strokeWidth = 1.5.dp,
              color = StudioBlue
            )
          }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          // Edit turn button
          IconButton(
            onClick = {
              if (isEditing) {
                onUpdateText(editText)
              }
              isEditing = !isEditing
            },
            modifier = Modifier.size(26.dp).testTag("edit_turn_${turn.id}")
          ) {
            Icon(
              imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
              contentDescription = "Edit turn",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(14.dp)
            )
          }

          // Delete turn button
          IconButton(
            onClick = onDelete,
            modifier = Modifier.size(26.dp).testTag("delete_turn_${turn.id}")
          ) {
            Icon(
              imageVector = Icons.Default.DeleteOutline,
              contentDescription = "Delete turn",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }

      // Attachments preview if present on this turn
      if (turn.attachments.isNotEmpty()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          turn.attachments.forEach { att ->
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = MaterialTheme.colorScheme.surfaceVariant,
              border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(Icons.Default.Attachment, contentDescription = null, modifier = Modifier.size(12.dp))
                Text(att.name, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
              }
            }
          }
        }
      }

      // Text Content or Edit Input
      if (isEditing) {
        OutlinedTextField(
          value = editText,
          onValueChange = { editText = it },
          modifier = Modifier.fillMaxWidth(),
          textStyle = MaterialTheme.typography.bodyMedium
        )
      } else {
        FormattedMarkdownText(
          text = turn.text.ifBlank { if (turn.isStreaming) "Generating response..." else "(Empty response)" },
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
  }
}
