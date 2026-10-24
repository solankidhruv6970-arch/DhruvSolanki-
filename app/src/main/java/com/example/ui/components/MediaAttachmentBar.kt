package com.example.ui.components

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MediaAttachment
import com.example.model.MediaType
import com.example.ui.theme.*
import java.util.UUID

@Composable
fun MediaAttachmentBar(
  attachments: List<MediaAttachment>,
  onAddAttachment: (MediaAttachment) -> Unit,
  onRemoveAttachment: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var isAttachMenuOpen by remember { mutableStateOf(false) }

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    // Buttons row: + Add Media, and quick sample chips
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Box {
        FilledTonalButton(
          onClick = { isAttachMenuOpen = true },
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          modifier = Modifier.testTag("add_media_button")
        ) {
          Icon(
            imageVector = Icons.Default.AddPhotoAlternate,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = StudioBlue
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Add media",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
          )
        }

        DropdownMenu(
          expanded = isAttachMenuOpen,
          onDismissRequest = { isAttachMenuOpen = false }
        ) {
          DropdownMenuItem(
            text = { Text("Image (diagram_architecture.png)") },
            onClick = {
              onAddAttachment(
                MediaAttachment(
                  id = UUID.randomUUID().toString(),
                  name = "architecture_diagram.png",
                  type = MediaType.IMAGE,
                  uriOrUrl = "sample://architecture_diagram.png",
                  sizeText = "480 KB",
                  colorSeed = 0xFF1976D2
                )
              )
              isAttachMenuOpen = false
            },
            leadingIcon = { Icon(Icons.Default.Image, contentDescription = null, tint = StudioCyan) }
          )

          DropdownMenuItem(
            text = { Text("Audio recording (customer_call.wav)") },
            onClick = {
              onAddAttachment(
                MediaAttachment(
                  id = UUID.randomUUID().toString(),
                  name = "customer_call.wav",
                  type = MediaType.AUDIO,
                  uriOrUrl = "sample://customer_call.wav",
                  sizeText = "1.8 MB",
                  colorSeed = 0xFF7B1FA2
                )
              )
              isAttachMenuOpen = false
            },
            leadingIcon = { Icon(Icons.Default.Mic, contentDescription = null, tint = StudioPurple) }
          )

          DropdownMenuItem(
            text = { Text("PDF Document (q3_quarterly_report.pdf)") },
            onClick = {
              onAddAttachment(
                MediaAttachment(
                  id = UUID.randomUUID().toString(),
                  name = "q3_quarterly_report.pdf",
                  type = MediaType.PDF,
                  uriOrUrl = "sample://q3_quarterly_report.pdf",
                  sizeText = "2.4 MB",
                  colorSeed = 0xFFC62828
                )
              )
              isAttachMenuOpen = false
            },
            leadingIcon = { Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = StudioRed) }
          )

          DropdownMenuItem(
            text = { Text("Video clip (app_demo_screen.mp4)") },
            onClick = {
              onAddAttachment(
                MediaAttachment(
                  id = UUID.randomUUID().toString(),
                  name = "app_demo_screen.mp4",
                  type = MediaType.VIDEO,
                  uriOrUrl = "sample://app_demo_screen.mp4",
                  sizeText = "8.6 MB",
                  colorSeed = 0xFF2E7D32
                )
              )
              isAttachMenuOpen = false
            },
            leadingIcon = { Icon(Icons.Default.Videocam, contentDescription = null, tint = StudioGreen) }
          )
        }
      }

      // Quick helper label
      Text(
        text = if (attachments.isEmpty()) "Attach images, audio, video, or PDFs" else "${attachments.size} file(s) attached",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 11.sp
      )
    }

    // Display list of attached media thumbnails
    AnimatedVisibility(visible = attachments.isNotEmpty()) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        attachments.forEach { item ->
          AttachmentThumbnailCard(
            item = item,
            onRemove = { onRemoveAttachment(item.id) }
          )
        }
      }
    }
  }
}

@Composable
private fun AttachmentThumbnailCard(
  item: MediaAttachment,
  onRemove: () -> Unit
) {
  val icon: ImageVector = when (item.type) {
    MediaType.IMAGE -> Icons.Default.Image
    MediaType.AUDIO -> Icons.Default.Audiotrack
    MediaType.VIDEO -> Icons.Default.Videocam
    MediaType.PDF -> Icons.Default.PictureAsPdf
  }

  val accentColor = when (item.type) {
    MediaType.IMAGE -> StudioCyan
    MediaType.AUDIO -> StudioPurple
    MediaType.VIDEO -> StudioGreen
    MediaType.PDF -> StudioRed
  }

  Surface(
    shape = RoundedCornerShape(8.dp),
    color = MaterialTheme.colorScheme.surfaceVariant,
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
    modifier = Modifier.testTag("attachment_thumbnail_${item.id}")
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Box(
        modifier = Modifier
          .size(28.dp)
          .clip(RoundedCornerShape(4.dp))
          .background(Color(item.colorSeed)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(16.dp)
        )
      }

      Column {
        Text(
          text = item.name,
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1
        )
        Text(
          text = "${item.type.label} · ${item.sizeText}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 10.sp
        )
      }

      IconButton(
        onClick = onRemove,
        modifier = Modifier.size(22.dp).testTag("remove_attachment_${item.id}")
      ) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "Remove attachment",
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(14.dp)
        )
      }
    }
  }
}
