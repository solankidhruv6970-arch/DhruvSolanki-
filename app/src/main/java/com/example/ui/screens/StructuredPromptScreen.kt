package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GenerationStatus
import com.example.model.StructuredExample
import com.example.ui.components.FormattedMarkdownText
import com.example.ui.components.SystemInstructionCard
import com.example.ui.theme.*

@Composable
fun StructuredPromptScreen(
  examples: List<StructuredExample>,
  inputLabel: String,
  outputLabel: String,
  onUpdateLabels: (String, String) -> Unit,
  onAddExample: () -> Unit,
  onUpdateExample: (String, String, String) -> Unit,
  onDeleteExample: (String) -> Unit,
  testInput: String,
  onTestInputChange: (String) -> Unit,
  testOutput: String,
  onRunTest: () -> Unit,
  systemInstruction: String,
  onSystemInstructionChange: (String) -> Unit,
  isSystemExpanded: Boolean,
  onToggleSystemExpanded: () -> Unit,
  onSelectSystemPreset: (String) -> Unit,
  generationStatus: GenerationStatus,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()
  var isEditingLabels by remember { mutableStateOf(false) }
  var tempInputLabel by remember(inputLabel) { mutableStateOf(inputLabel) }
  var tempOutputLabel by remember(outputLabel) { mutableStateOf(outputLabel) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. System Instruction Box
    SystemInstructionCard(
      systemInstruction = systemInstruction,
      onSystemInstructionChange = onSystemInstructionChange,
      isExpanded = isSystemExpanded,
      onToggleExpand = onToggleSystemExpanded,
      onSelectPreset = onSelectSystemPreset
    )

    // 2. Few-shot grid header & explanation
    Surface(
      shape = RoundedCornerShape(10.dp),
      color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
      border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
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
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.TableChart,
              contentDescription = null,
              tint = StudioBlue,
              modifier = Modifier.size(20.dp)
            )
            Column {
              Text(
                text = "Few-shot Examples Grid",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Calibrate the model by providing pairs of inputs and expected outputs",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
              )
            }
          }

          TextButton(
            onClick = {
              if (isEditingLabels) {
                onUpdateLabels(tempInputLabel, tempOutputLabel)
              }
              isEditingLabels = !isEditingLabels
            },
            modifier = Modifier.testTag("edit_column_labels_button")
          ) {
            Icon(
              if (isEditingLabels) Icons.Default.Check else Icons.Default.Tune,
              contentDescription = null,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(if (isEditingLabels) "Save Labels" else "Edit Columns", style = MaterialTheme.typography.labelSmall)
          }
        }

        if (isEditingLabels) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedTextField(
              value = tempInputLabel,
              onValueChange = { tempInputLabel = it },
              label = { Text("Input Column Label") },
              modifier = Modifier.weight(1f),
              singleLine = true,
              textStyle = MaterialTheme.typography.bodySmall
            )
            OutlinedTextField(
              value = tempOutputLabel,
              onValueChange = { tempOutputLabel = it },
              label = { Text("Output Column Label") },
              modifier = Modifier.weight(1f),
              singleLine = true,
              textStyle = MaterialTheme.typography.bodySmall
            )
          }
        }
      }
    }

    // 3. Examples Rows List
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      examples.forEachIndexed { index, example ->
        ExampleRowCard(
          index = index + 1,
          example = example,
          inputLabel = inputLabel,
          outputLabel = outputLabel,
          onUpdate = { input, output -> onUpdateExample(example.id, input, output) },
          onDelete = { onDeleteExample(example.id) }
        )
      }

      // Add Example Button
      Button(
        onClick = onAddExample,
        modifier = Modifier.fillMaxWidth().testTag("add_example_button"),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant,
          contentColor = StudioBlue
        )
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Add Example Row",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
        )
      }
    }

    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

    // 4. Test Row (Few-Shot Evaluation)
    Surface(
      shape = RoundedCornerShape(10.dp),
      color = MaterialTheme.colorScheme.surface,
      border = androidx.compose.foundation.BorderStroke(1.5.dp, StudioBlue.copy(alpha = 0.6f)),
      tonalElevation = 2.dp,
      modifier = Modifier.fillMaxWidth().testTag("structured_test_row")
    ) {
      Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
              imageVector = Icons.Default.PlayCircle,
              contentDescription = null,
              tint = StudioBlue,
              modifier = Modifier.size(18.dp)
            )
            Text(
              text = "Test Row (Execute few-shot prompt)",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          val isRunning = generationStatus is GenerationStatus.Streaming
          Button(
            onClick = onRunTest,
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            colors = ButtonDefaults.buttonColors(containerColor = StudioBlueDark),
            modifier = Modifier.testTag("run_structured_test_button")
          ) {
            if (isRunning) {
              CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 2.dp)
              Spacer(modifier = Modifier.width(6.dp))
              Text("Testing...", style = MaterialTheme.typography.labelSmall)
            } else {
              Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Test", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
            }
          }
        }

        // Test Input
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = "Test $inputLabel:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
          OutlinedTextField(
            value = testInput,
            onValueChange = onTestInputChange,
            placeholder = { Text("Enter a new input to test few-shot calibration...") },
            modifier = Modifier.fillMaxWidth().testTag("structured_test_input"),
            textStyle = MaterialTheme.typography.bodyMedium
          )
        }

        // Test Output
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = "Predicted $outputLabel:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = StudioGreen
          )
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 60.dp)
          ) {
            Box(modifier = Modifier.padding(10.dp)) {
              if (testOutput.isNotBlank()) {
                FormattedMarkdownText(text = testOutput)
              } else {
                Text(
                  text = "Output will be generated here when you click 'Test' or 'Run'...",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ExampleRowCard(
  index: Int,
  example: StructuredExample,
  inputLabel: String,
  outputLabel: String,
  onUpdate: (String, String) -> Unit,
  onDelete: () -> Unit
) {
  var input by remember(example.input) { mutableStateOf(example.input) }
  var output by remember(example.output) { mutableStateOf(example.output) }

  Surface(
    shape = RoundedCornerShape(8.dp),
    color = MaterialTheme.colorScheme.surface,
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
    tonalElevation = 1.dp,
    modifier = Modifier.fillMaxWidth().testTag("example_row_$index")
  ) {
    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = StudioBlue.copy(alpha = 0.15f)
        ) {
          Text(
            text = "Example #$index",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = StudioBlue,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }

        IconButton(
          onClick = onDelete,
          modifier = Modifier.size(24.dp).testTag("delete_example_$index")
        ) {
          Icon(
            imageVector = Icons.Default.DeleteOutline,
            contentDescription = "Delete Example",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      // Input field
      OutlinedTextField(
        value = input,
        onValueChange = {
          input = it
          onUpdate(it, output)
        },
        label = { Text(inputLabel) },
        modifier = Modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodySmall
      )

      // Output field
      OutlinedTextField(
        value = output,
        onValueChange = {
          output = it
          onUpdate(input, it)
        },
        label = { Text(outputLabel) },
        modifier = Modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodySmall
      )
    }
  }
}
