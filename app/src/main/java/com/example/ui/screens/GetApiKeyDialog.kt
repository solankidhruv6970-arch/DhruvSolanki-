package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.BuildConfig
import com.example.ui.theme.StudioBlue
import com.example.ui.theme.StudioGreen

@Composable
fun GetApiKeyDialog(
  currentCustomKey: String,
  onSaveKey: (String) -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  var keyInput by remember(currentCustomKey) { mutableStateOf(currentCustomKey) }
  var isKeyVisible by remember { mutableStateOf(false) }

  val hasEnvKey = BuildConfig.GEMINI_API_KEY.isNotBlank() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY"

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.surface,
      border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
      tonalElevation = 6.dp,
      modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(Icons.Default.VpnKey, contentDescription = null, tint = StudioBlue)
            Text(
              text = "Get & Manage API Key",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
          }
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp).testTag("close_api_key_dialog")) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }

        // Status Card
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(
              imageVector = if (hasEnvKey || currentCustomKey.isNotBlank()) Icons.Default.CheckCircle else Icons.Default.Info,
              contentDescription = null,
              tint = if (hasEnvKey || currentCustomKey.isNotBlank()) StudioGreen else StudioBlue,
              modifier = Modifier.size(20.dp)
            )
            Column {
              Text(
                text = if (hasEnvKey) {
                  "Active (AI Studio Secret Injected)"
                } else if (currentCustomKey.isNotBlank()) {
                  "Active (Custom User Key)"
                } else {
                  "Demo Simulation Mode Active"
                },
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Simulates authentic streaming responses offline, or connects to live Gemini models when configured.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
              )
            }
          }
        }

        // Input field for Custom Key
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = "Enter Custom Gemini API Key:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
          OutlinedTextField(
            value = keyInput,
            onValueChange = { keyInput = it },
            placeholder = { Text("AIzaSy...") },
            modifier = Modifier.fillMaxWidth().testTag("api_key_text_field"),
            singleLine = true,
            trailingIcon = {
              if (keyInput.isNotBlank()) {
                IconButton(onClick = { keyInput = "" }) {
                  Icon(Icons.Default.Clear, contentDescription = "Clear")
                }
              }
            }
          )
          Text(
            text = "Keys can be generated in Google AI Studio at aistudio.google.com/apikey",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
          )
        }

        // Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          TextButton(onClick = onDismiss) {
            Text("Cancel")
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              onSaveKey(keyInput.trim())
              Toast.makeText(context, "API Key saved successfully", Toast.LENGTH_SHORT).show()
              onDismiss()
            },
            modifier = Modifier.testTag("save_api_key_button")
          ) {
            Text("Save & Apply")
          }
        }
      }
    }
  }
}
