package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.PromptType
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.AIStudioTheme
import com.example.viewmodel.StudioViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: StudioViewModel = viewModel()
      val state by viewModel.uiState.collectAsStateWithLifecycle()

      AIStudioTheme(darkTheme = state.isDarkMode) {
        // Back handler to close drawers/panels
        BackHandler(enabled = state.isLeftDrawerOpen || state.isRightPanelOpen) {
          if (state.isLeftDrawerOpen) viewModel.setLeftDrawerOpen(false)
          else if (state.isRightPanelOpen) viewModel.setRightPanelOpen(false)
        }

        BoxWithConstraints(
          modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
          val isWideScreen = maxWidth >= 840.dp

          Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
              TopHeaderBar(
                currentPromptType = state.currentPromptType,
                onSelectPromptType = { viewModel.setPromptType(it) },
                selectedModel = state.selectedModel,
                onSelectModel = { viewModel.selectModel(it) },
                generationStatus = state.generationStatus,
                onRunClicked = { viewModel.runPrompt() },
                onStopClicked = { viewModel.stopGeneration() },
                onGetCodeClicked = { viewModel.setGetCodeModalOpen(true) },
                onToggleLeftDrawer = { viewModel.toggleLeftDrawer() },
                onToggleRightPanel = { viewModel.toggleRightPanel() },
                isRightPanelOpen = state.isRightPanelOpen,
                isDarkMode = state.isDarkMode,
                onToggleTheme = { viewModel.toggleTheme() }
              )
            }
          ) { innerPadding ->
            Row(
              modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
            ) {
              // Wide screen permanent or semi-permanent left drawer
              if (isWideScreen && state.isLeftDrawerOpen) {
                LeftSidebarDrawer(
                  currentProject = state.currentProject,
                  availableProjects = state.availableProjects,
                  onSelectProject = { viewModel.selectProject(it) },
                  onAddProject = { viewModel.addProject(it) },
                  onNavigatePlayground = { viewModel.setLeftDrawerOpen(false) },
                  onOpenGetApiKey = { viewModel.setGetApiKeyModalOpen(true) },
                  onOpenSettings = { viewModel.setSettingsModalOpen(true) },
                  onClose = { viewModel.setLeftDrawerOpen(false) },
                  isDarkMode = state.isDarkMode,
                  onToggleTheme = { viewModel.toggleTheme() },
                  modifier = Modifier.width(260.dp)
                )
              }

              // Center Main Workspace based on PromptType
              Box(
                modifier = Modifier
                  .weight(1f)
                  .fillMaxHeight()
              ) {
                when (state.currentPromptType) {
                  PromptType.FREEFORM -> {
                    FreeformPromptScreen(
                      prompt = state.freeformPrompt,
                      onPromptChange = { viewModel.updateFreeformPrompt(it) },
                      systemInstruction = state.systemInstruction,
                      onSystemInstructionChange = { viewModel.updateSystemInstruction(it) },
                      isSystemExpanded = state.isSystemExpanded,
                      onToggleSystemExpanded = { viewModel.toggleSystemExpanded() },
                      onSelectSystemPreset = { viewModel.applySystemPreset(it) },
                      attachments = state.attachments,
                      onAddAttachment = { viewModel.addAttachment(it) },
                      onRemoveAttachment = { viewModel.removeAttachment(it) },
                      googleSearchGrounding = state.googleSearchGrounding,
                      onToggleSearchGrounding = { viewModel.toggleGoogleSearchGrounding() },
                      codeExecution = state.codeExecution,
                      onToggleCodeExecution = { viewModel.toggleCodeExecution() },
                      generationStatus = state.generationStatus,
                      onRunPrompt = { viewModel.runPrompt() }
                    )
                  }

                  PromptType.CHAT -> {
                    ChatPromptScreen(
                      chatTurns = state.chatTurns,
                      currentMessage = state.currentChatMessage,
                      onMessageChange = { viewModel.updateChatMessage(it) },
                      onSendMessage = { viewModel.sendChatMessage() },
                      onAddTurn = { viewModel.addChatTurn(it) },
                      onUpdateTurnText = { id, text -> viewModel.updateChatTurnText(id, text) },
                      onDeleteTurn = { viewModel.deleteChatTurn(it) },
                      onClearChat = { viewModel.clearChat() },
                      systemInstruction = state.systemInstruction,
                      onSystemInstructionChange = { viewModel.updateSystemInstruction(it) },
                      isSystemExpanded = state.isSystemExpanded,
                      onToggleSystemExpanded = { viewModel.toggleSystemExpanded() },
                      onSelectSystemPreset = { viewModel.applySystemPreset(it) },
                      attachments = state.attachments,
                      onAddAttachment = { viewModel.addAttachment(it) },
                      onRemoveAttachment = { viewModel.removeAttachment(it) },
                      googleSearchGrounding = state.googleSearchGrounding,
                      onToggleSearchGrounding = { viewModel.toggleGoogleSearchGrounding() },
                      generationStatus = state.generationStatus
                    )
                  }

                  PromptType.STRUCTURED -> {
                    StructuredPromptScreen(
                      examples = state.structuredExamples,
                      inputLabel = state.structuredInputLabel,
                      outputLabel = state.structuredOutputLabel,
                      onUpdateLabels = { inLabel, outLabel -> viewModel.updateStructuredLabels(inLabel, outLabel) },
                      onAddExample = { viewModel.addStructuredExample() },
                      onUpdateExample = { id, inVal, outVal -> viewModel.updateStructuredExample(id, inVal, outVal) },
                      onDeleteExample = { viewModel.deleteStructuredExample(it) },
                      testInput = state.structuredTestInput,
                      onTestInputChange = { viewModel.updateStructuredTestInput(it) },
                      testOutput = state.structuredTestOutput,
                      onRunTest = { viewModel.runStructuredTest() },
                      systemInstruction = state.systemInstruction,
                      onSystemInstructionChange = { viewModel.updateSystemInstruction(it) },
                      isSystemExpanded = state.isSystemExpanded,
                      onToggleSystemExpanded = { viewModel.toggleSystemExpanded() },
                      onSelectSystemPreset = { viewModel.applySystemPreset(it) },
                      generationStatus = state.generationStatus
                    )
                  }
                }
              }

              // Right Configuration Panel (Side-by-side on wide screens)
              if (isWideScreen && state.isRightPanelOpen) {
                RightConfigDrawer(
                  selectedModel = state.selectedModel,
                  onSelectModel = { viewModel.selectModel(it) },
                  temperature = state.temperature,
                  onTemperatureChanged = { viewModel.updateTemperature(it) },
                  topP = state.topP,
                  onTopPChanged = { viewModel.updateTopP(it) },
                  topK = state.topK,
                  onTopKChanged = { viewModel.updateTopK(it) },
                  maxOutputTokens = state.maxOutputTokens,
                  onMaxOutputTokensChanged = { viewModel.updateMaxTokens(it) },
                  googleSearchGrounding = state.googleSearchGrounding,
                  onToggleSearchGrounding = { viewModel.toggleGoogleSearchGrounding() },
                  codeExecution = state.codeExecution,
                  onToggleCodeExecution = { viewModel.toggleCodeExecution() },
                  safetyThresholds = state.safetyThresholds,
                  onUpdateSafety = { cat, thresh -> viewModel.updateSafety(cat, thresh) },
                  onResetDefaults = { viewModel.resetParameters() },
                  onClose = { viewModel.setRightPanelOpen(false) }
                )
              }
            }
          }

          // Mobile Overlay Drawers
          if (!isWideScreen) {
            // Left Drawer Overlay
            AnimatedVisibility(
              visible = state.isLeftDrawerOpen,
              enter = slideInHorizontally { -it },
              exit = slideOutHorizontally { -it }
            ) {
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f))
              ) {
                LeftSidebarDrawer(
                  currentProject = state.currentProject,
                  availableProjects = state.availableProjects,
                  onSelectProject = { viewModel.selectProject(it) },
                  onAddProject = { viewModel.addProject(it) },
                  onNavigatePlayground = { viewModel.setLeftDrawerOpen(false) },
                  onOpenGetApiKey = { viewModel.setGetApiKeyModalOpen(true) },
                  onOpenSettings = { viewModel.setSettingsModalOpen(true) },
                  onClose = { viewModel.setLeftDrawerOpen(false) },
                  isDarkMode = state.isDarkMode,
                  onToggleTheme = { viewModel.toggleTheme() },
                  modifier = Modifier.align(Alignment.CenterStart)
                )
              }
            }

            // Right Panel Overlay
            AnimatedVisibility(
              visible = state.isRightPanelOpen,
              enter = slideInHorizontally { it },
              exit = slideOutHorizontally { it }
            ) {
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f))
              ) {
                RightConfigDrawer(
                  selectedModel = state.selectedModel,
                  onSelectModel = { viewModel.selectModel(it) },
                  temperature = state.temperature,
                  onTemperatureChanged = { viewModel.updateTemperature(it) },
                  topP = state.topP,
                  onTopPChanged = { viewModel.updateTopP(it) },
                  topK = state.topK,
                  onTopKChanged = { viewModel.updateTopK(it) },
                  maxOutputTokens = state.maxOutputTokens,
                  onMaxOutputTokensChanged = { viewModel.updateMaxTokens(it) },
                  googleSearchGrounding = state.googleSearchGrounding,
                  onToggleSearchGrounding = { viewModel.toggleGoogleSearchGrounding() },
                  codeExecution = state.codeExecution,
                  onToggleCodeExecution = { viewModel.toggleCodeExecution() },
                  safetyThresholds = state.safetyThresholds,
                  onUpdateSafety = { cat, thresh -> viewModel.updateSafety(cat, thresh) },
                  onResetDefaults = { viewModel.resetParameters() },
                  onClose = { viewModel.setRightPanelOpen(false) },
                  modifier = Modifier.align(Alignment.CenterEnd)
                )
              }
            }
          }

          // Get Code Export Modal
          if (state.isGetCodeModalOpen) {
            val activePromptText = when (state.currentPromptType) {
              PromptType.FREEFORM -> state.freeformPrompt
              PromptType.CHAT -> state.chatTurns.joinToString("\n") { "${it.role}: ${it.text}" }
              PromptType.STRUCTURED -> state.structuredExamples.joinToString("\n") { "Input: ${it.input}\nOutput: ${it.output}" } + "\nInput: ${state.structuredTestInput}"
            }

            GetCodeModal(
              selectedModel = state.selectedModel,
              prompt = activePromptText,
              systemInstruction = state.systemInstruction,
              temperature = state.temperature,
              topP = state.topP,
              topK = state.topK,
              maxTokens = state.maxOutputTokens,
              searchGrounding = state.googleSearchGrounding,
              codeExecution = state.codeExecution,
              safetyThresholds = state.safetyThresholds,
              selectedLanguage = state.selectedExportLanguage,
              onSelectLanguage = { viewModel.selectExportLanguage(it) },
              onDismiss = { viewModel.setGetCodeModalOpen(false) }
            )
          }

          // Get API Key Dialog
          if (state.isGetApiKeyModalOpen) {
            GetApiKeyDialog(
              currentCustomKey = state.customApiKey,
              onSaveKey = { viewModel.setCustomApiKey(it) },
              onDismiss = { viewModel.setGetApiKeyModalOpen(false) }
            )
          }

          // Settings Dialog
          if (state.isSettingsModalOpen) {
            SettingsDialog(
              isDarkMode = state.isDarkMode,
              onToggleTheme = { viewModel.toggleTheme() },
              selectedModel = state.selectedModel,
              onSelectModel = { viewModel.selectModel(it) },
              onResetParameters = { viewModel.resetParameters() },
              onDismiss = { viewModel.setSettingsModalOpen(false) }
            )
          }
        }
      }
    }
  }
}
