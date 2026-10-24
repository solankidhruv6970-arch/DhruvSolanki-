package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun LeftSidebarDrawer(
  currentProject: String,
  availableProjects: List<String>,
  onSelectProject: (String) -> Unit,
  onAddProject: (String) -> Unit,
  onNavigatePlayground: () -> Unit,
  onOpenGetApiKey: () -> Unit,
  onOpenSettings: () -> Unit,
  onClose: () -> Unit,
  isDarkMode: Boolean,
  onToggleTheme: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isProjectMenuOpen by remember { mutableStateOf(false) }
  var isNewProjectDialogOpen by remember { mutableStateOf(false) }
  var newProjectName by remember { mutableStateOf("") }

  Surface(
    modifier = modifier
      .fillMaxHeight()
      .widthIn(max = 300.dp),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 3.dp,
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Top Brand Logo & Close Icon
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(32.dp)
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
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
              )
            }
            Column {
              Text(
                text = "Google AI Studio",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Developer Platform",
                style = MaterialTheme.typography.bodySmall,
                color = StudioBlue,
                fontSize = 11.sp
              )
            }
          }
          IconButton(
            onClick = onClose,
            modifier = Modifier.size(32.dp).testTag("close_left_sidebar")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close Sidebar",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Project Switcher Dropdown
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
          modifier = Modifier.fillMaxWidth().testTag("project_switcher_button"),
          onClick = { isProjectMenuOpen = true }
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                tint = StudioBlue,
                modifier = Modifier.size(18.dp)
              )
              Column {
                Text(
                  text = "Project",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = currentProject,
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
            }
            Icon(
              imageVector = Icons.Default.UnfoldMore,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(18.dp)
            )
          }

          DropdownMenu(
            expanded = isProjectMenuOpen,
            onDismissRequest = { isProjectMenuOpen = false }
          ) {
            availableProjects.forEach { proj ->
              DropdownMenuItem(
                text = {
                  Text(
                    text = proj,
                    fontWeight = if (proj == currentProject) FontWeight.Bold else FontWeight.Normal,
                    color = if (proj == currentProject) StudioBlue else MaterialTheme.colorScheme.onSurface
                  )
                },
                onClick = {
                  onSelectProject(proj)
                  isProjectMenuOpen = false
                },
                leadingIcon = {
                  Icon(
                    imageVector = Icons.Default.FolderSpecial,
                    contentDescription = null,
                    tint = if (proj == currentProject) StudioBlue else MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              )
            }
            HorizontalDivider()
            DropdownMenuItem(
              text = { Text("+ New Project", color = StudioBlue) },
              onClick = {
                isProjectMenuOpen = false
                isNewProjectDialogOpen = true
              },
              leadingIcon = {
                Icon(Icons.Default.Add, contentDescription = null, tint = StudioBlue)
              }
            )
          }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

        // Navigation Links
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          SidebarNavLink(
            title = "Playground",
            subtitle = "Prompts & multimodal testing",
            icon = Icons.Default.Science,
            isSelected = true,
            onClick = {
              onNavigatePlayground()
              onClose()
            }
          )

          SidebarNavLink(
            title = "Prompt Library",
            subtitle = "Saved templates & few-shots",
            icon = Icons.Default.CollectionsBookmark,
            isSelected = false,
            onClick = {
              onNavigatePlayground()
              onClose()
            }
          )

          SidebarNavLink(
            title = "Tune Models",
            subtitle = "Fine-tuning pipelines & adapters",
            icon = Icons.Default.ModelTraining,
            isSelected = false,
            onClick = {
              onNavigatePlayground()
              onClose()
            }
          )

          SidebarNavLink(
            title = "Get API Key",
            subtitle = "Keys, quotas & credentials",
            icon = Icons.Default.VpnKey,
            isSelected = false,
            badge = "Essential",
            onClick = {
              onOpenGetApiKey()
              onClose()
            }
          )

          SidebarNavLink(
            title = "Documentation",
            subtitle = "API reference & code samples",
            icon = Icons.AutoMirrored.Filled.MenuBook,
            isSelected = false,
            onClick = {
              onNavigatePlayground()
              onClose()
            }
          )
        }
      }

      // Bottom Area: Settings & Theme Switcher
      Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 16.dp)
      ) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onToggleTheme() }
            .padding(horizontal = 8.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
              contentDescription = null,
              tint = StudioBlue,
              modifier = Modifier.size(20.dp)
            )
            Text(
              text = if (isDarkMode) "Dark Theme" else "Light Theme",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
          Switch(
            checked = isDarkMode,
            onCheckedChange = { onToggleTheme() },
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color.White,
              checkedTrackColor = StudioBlue
            ),
            modifier = Modifier.testTag("theme_switch")
          )
        }

        SidebarNavLink(
          title = "Settings",
          subtitle = "Preferences & defaults",
          icon = Icons.Default.Settings,
          isSelected = false,
          onClick = {
            onOpenSettings()
            onClose()
          }
        )
      }
    }
  }

  // New Project Dialog
  if (isNewProjectDialogOpen) {
    AlertDialog(
      onDismissRequest = { isNewProjectDialogOpen = false },
      title = { Text("Create New Project") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Enter a project name to organize your prompts:")
          OutlinedTextField(
            value = newProjectName,
            onValueChange = { newProjectName = it },
            placeholder = { Text("e.g. Video Summarizer") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("new_project_input")
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (newProjectName.isNotBlank()) {
              onAddProject(newProjectName)
              newProjectName = ""
              isNewProjectDialogOpen = false
            }
          }
        ) {
          Text("Create")
        }
      },
      dismissButton = {
        TextButton(onClick = { isNewProjectDialogOpen = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
private fun SidebarNavLink(
  title: String,
  subtitle: String,
  icon: ImageVector,
  isSelected: Boolean,
  badge: String? = null,
  onClick: () -> Unit
) {
  Surface(
    onClick = onClick,
    shape = RoundedCornerShape(8.dp),
    color = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,
    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, StudioBlue.copy(alpha = 0.5f)) else null,
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.weight(1f)
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = if (isSelected) StudioBlue else MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(20.dp)
        )
        Column {
          Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (isSelected) StudioBlue else MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
          )
        }
      }

      if (badge != null) {
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = StudioBlue.copy(alpha = 0.2f),
          border = androidx.compose.foundation.BorderStroke(1.dp, StudioBlue.copy(alpha = 0.4f))
        ) {
          Text(
            text = badge,
            style = MaterialTheme.typography.labelSmall,
            color = StudioBlue,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }
    }
  }
}
