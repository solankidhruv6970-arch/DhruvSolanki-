package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GeminiEngine
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class StudioUiState(
  val currentPromptType: PromptType = PromptType.FREEFORM,
  val freeformPrompt: String = "",
  val systemInstruction: String = "You are a helpful, expert AI assistant. Provide concise, clear, and well-reasoned answers.",
  val isSystemExpanded: Boolean = false,
  val attachments: List<MediaAttachment> = emptyList(),
  // Chat Prompt State
  val chatTurns: List<ChatTurn> = listOf(
    ChatTurn(
      id = "turn-1",
      role = "user",
      text = "What are the primary architectural differences between Gemini 3.5 Flash and Gemini 3.1 Pro?"
    ),
    ChatTurn(
      id = "turn-2",
      role = "model",
      text = "Gemini 3.5 Flash is designed for sub-second latency and high-volume tasks with native multimodal reasoning. Gemini 3.1 Pro is tuned for deep mathematical deductions, code generation, and complex tool-use workflows."
    )
  ),
  val currentChatMessage: String = "",
  // Structured Prompt State (Few-Shot Grid)
  val structuredInputLabel: String = "Customer Review",
  val structuredOutputLabel: String = "Sentiment & Category JSON",
  val structuredExamples: List<StructuredExample> = listOf(
    StructuredExample(
      id = "ex-1",
      input = "The camera takes stunning 4K photos at night, but the battery drains fast.",
      output = "{\"sentiment\": \"mixed\", \"topics\": [\"camera\", \"battery\"], \"score\": 0.6}"
    ),
    StructuredExample(
      id = "ex-2",
      input = "Customer support resolved my shipping dispute within five minutes. Fantastic service!",
      output = "{\"sentiment\": \"positive\", \"topics\": [\"support\", \"shipping\"], \"score\": 0.95}"
    )
  ),
  val structuredTestInput: String = "The display is vibrant, though shipping took 2 weeks longer than promised.",
  val structuredTestOutput: String = "",
  // Model Parameters
  val selectedModel: GeminiModelOption = AvailableGeminiModels.first(),
  val temperature: Float = 1.0f,
  val topP: Float = 0.95f,
  val topK: Int = 40,
  val maxOutputTokens: Int = 8192,
  val googleSearchGrounding: Boolean = false,
  val codeExecution: Boolean = false,
  val safetyThresholds: Map<SafetyCategory, SafetyThreshold> = mapOf(
    SafetyCategory.HATE_SPEECH to SafetyThreshold.BLOCK_SOME,
    SafetyCategory.HARASSMENT to SafetyThreshold.BLOCK_SOME,
    SafetyCategory.SEXUALLY_EXPLICIT to SafetyThreshold.BLOCK_SOME,
    SafetyCategory.DANGEROUS_CONTENT to SafetyThreshold.BLOCK_SOME
  ),
  // Generation & Execution
  val generationStatus: GenerationStatus = GenerationStatus.Idle,
  // Navigation & Modals
  val isRightPanelOpen: Boolean = false,
  val isLeftDrawerOpen: Boolean = false,
  val isGetCodeModalOpen: Boolean = false,
  val selectedExportLanguage: ExportLanguage = ExportLanguage.PYTHON,
  val isGetApiKeyModalOpen: Boolean = false,
  val isSettingsModalOpen: Boolean = false,
  val isDarkMode: Boolean = true,
  val currentProject: String = "Default Project",
  val availableProjects: List<String> = listOf("Default Project", "Gemini 2.5 Experiment", "Customer Support Bot"),
  val customApiKey: String = ""
)

class StudioViewModel : ViewModel() {

  private val engine = GeminiEngine()
  private var generationJob: Job? = null

  private val _uiState = MutableStateFlow(StudioUiState())
  val uiState: StateFlow<StudioUiState> = _uiState.asStateFlow()

  fun setPromptType(type: PromptType) {
    _uiState.update { it.copy(currentPromptType = type) }
  }

  fun updateFreeformPrompt(text: String) {
    _uiState.update { it.copy(freeformPrompt = text) }
  }

  fun updateSystemInstruction(text: String) {
    _uiState.update { it.copy(systemInstruction = text) }
  }

  fun toggleSystemExpanded() {
    _uiState.update { it.copy(isSystemExpanded = !it.isSystemExpanded) }
  }

  fun applySystemPreset(preset: String) {
    _uiState.update { it.copy(systemInstruction = preset, isSystemExpanded = true) }
  }

  fun addAttachment(attachment: MediaAttachment) {
    _uiState.update { it.copy(attachments = it.attachments + attachment) }
  }

  fun removeAttachment(id: String) {
    _uiState.update { it.copy(attachments = it.attachments.filter { item -> item.id != id }) }
  }

  fun toggleGoogleSearchGrounding() {
    _uiState.update { it.copy(googleSearchGrounding = !it.googleSearchGrounding) }
  }

  fun toggleCodeExecution() {
    _uiState.update { it.copy(codeExecution = !it.codeExecution) }
  }

  fun selectModel(model: GeminiModelOption) {
    _uiState.update { it.copy(selectedModel = model) }
  }

  fun updateTemperature(value: Float) {
    _uiState.update { it.copy(temperature = (Math.round(value * 100) / 100f).coerceIn(0f, 2f)) }
  }

  fun updateTopP(value: Float) {
    _uiState.update { it.copy(topP = (Math.round(value * 100) / 100f).coerceIn(0f, 1f)) }
  }

  fun updateTopK(value: Int) {
    _uiState.update { it.copy(topK = value.coerceIn(1, 64)) }
  }

  fun updateMaxTokens(value: Int) {
    _uiState.update { it.copy(maxOutputTokens = value.coerceIn(1, 8192)) }
  }

  fun updateSafety(category: SafetyCategory, threshold: SafetyThreshold) {
    _uiState.update { state ->
      val updated = state.safetyThresholds.toMutableMap()
      updated[category] = threshold
      state.copy(safetyThresholds = updated)
    }
  }

  fun resetParameters() {
    _uiState.update {
      it.copy(
        temperature = 1.0f,
        topP = 0.95f,
        topK = 40,
        maxOutputTokens = 8192,
        googleSearchGrounding = false,
        codeExecution = false,
        safetyThresholds = mapOf(
          SafetyCategory.HATE_SPEECH to SafetyThreshold.BLOCK_SOME,
          SafetyCategory.HARASSMENT to SafetyThreshold.BLOCK_SOME,
          SafetyCategory.SEXUALLY_EXPLICIT to SafetyThreshold.BLOCK_SOME,
          SafetyCategory.DANGEROUS_CONTENT to SafetyThreshold.BLOCK_SOME
        )
      )
    }
  }

  // --- Run / Stop Generation ---
  fun runPrompt() {
    val state = _uiState.value
    when (state.currentPromptType) {
      PromptType.FREEFORM -> runFreeform()
      PromptType.CHAT -> sendChatMessage()
      PromptType.STRUCTURED -> runStructuredTest()
    }
  }

  private fun runFreeform() {
    val state = _uiState.value
    val prompt = state.freeformPrompt.ifBlank { "Explain how neural networks learn representations." }
    _uiState.update { it.copy(freeformPrompt = prompt, generationStatus = GenerationStatus.Streaming("", 0.0)) }

    generationJob?.cancel()
    generationJob = viewModelScope.launch {
      try {
        val result = engine.executePrompt(
          prompt = prompt,
          systemInstruction = state.systemInstruction,
          model = state.selectedModel,
          temperature = state.temperature,
          topP = state.topP,
          topK = state.topK,
          maxTokens = state.maxOutputTokens,
          searchGrounding = state.googleSearchGrounding,
          codeExecution = state.codeExecution,
          attachments = state.attachments,
          customApiKey = state.customApiKey,
          onChunk = { chunk ->
            _uiState.update { it.copy(generationStatus = GenerationStatus.Streaming(chunk, 0.5)) }
          }
        )
        _uiState.update {
          it.copy(
            generationStatus = GenerationStatus.Success(
              responseText = result.text,
              metrics = result.metrics,
              searchSources = result.searchSources,
              executedCode = result.executedCode,
              codeOutput = result.codeOutput
            )
          )
        }
      } catch (e: Exception) {
        _uiState.update { it.copy(generationStatus = GenerationStatus.Error(e.message ?: "Generation error")) }
      }
    }
  }

  fun stopGeneration() {
    generationJob?.cancel()
    generationJob = null
    val current = _uiState.value.generationStatus
    if (current is GenerationStatus.Streaming) {
      _uiState.update {
        it.copy(
          generationStatus = GenerationStatus.Success(
            responseText = current.partialText + "\n\n*(Generation stopped by user)*",
            metrics = ExecutionMetrics(
              inputTokens = 15,
              outputTokens = (current.partialText.length / 4).coerceAtLeast(1),
              totalTokens = 15 + (current.partialText.length / 4),
              latencySeconds = current.elapsedSec,
              tokensPerSec = 45.0,
              finishReason = "USER_CANCELLED"
            )
          )
        )
      }
    } else {
      _uiState.update { it.copy(generationStatus = GenerationStatus.Idle) }
    }
  }

  // --- Chat Prompt Methods ---
  fun updateChatMessage(text: String) {
    _uiState.update { it.copy(currentChatMessage = text) }
  }

  fun sendChatMessage() {
    val state = _uiState.value
    val msg = state.currentChatMessage.trim()
    if (msg.isBlank() && state.attachments.isEmpty()) return

    val userTurn = ChatTurn(
      id = UUID.randomUUID().toString(),
      role = "user",
      text = msg,
      attachments = state.attachments
    )
    val modelPlaceholder = ChatTurn(
      id = UUID.randomUUID().toString(),
      role = "model",
      text = "",
      isStreaming = true
    )

    _uiState.update {
      it.copy(
        chatTurns = it.chatTurns + userTurn + modelPlaceholder,
        currentChatMessage = "",
        attachments = emptyList(),
        generationStatus = GenerationStatus.Streaming("", 0.1)
      )
    }

    generationJob?.cancel()
    generationJob = viewModelScope.launch {
      try {
        val result = engine.executePrompt(
          prompt = msg,
          systemInstruction = state.systemInstruction,
          model = state.selectedModel,
          temperature = state.temperature,
          topP = state.topP,
          topK = state.topK,
          maxTokens = state.maxOutputTokens,
          searchGrounding = state.googleSearchGrounding,
          codeExecution = state.codeExecution,
          attachments = userTurn.attachments,
          customApiKey = state.customApiKey,
          onChunk = { chunk ->
            _uiState.update { s ->
              val updatedTurns = s.chatTurns.map { turn ->
                if (turn.id == modelPlaceholder.id) turn.copy(text = chunk) else turn
              }
              s.copy(chatTurns = updatedTurns)
            }
          }
        )

        _uiState.update { s ->
          val updatedTurns = s.chatTurns.map { turn ->
            if (turn.id == modelPlaceholder.id) turn.copy(text = result.text, isStreaming = false) else turn
          }
          s.copy(
            chatTurns = updatedTurns,
            generationStatus = GenerationStatus.Success(result.text, result.metrics, result.searchSources)
          )
        }
      } catch (e: Exception) {
        _uiState.update { s ->
          val updatedTurns = s.chatTurns.map { turn ->
            if (turn.id == modelPlaceholder.id) turn.copy(text = "Error: ${e.message}", isStreaming = false) else turn
          }
          s.copy(chatTurns = updatedTurns, generationStatus = GenerationStatus.Error(e.message ?: "Error"))
        }
      }
    }
  }

  fun addChatTurn(role: String) {
    val newTurn = ChatTurn(
      id = UUID.randomUUID().toString(),
      role = role,
      text = if (role == "user") "New user input here..." else "New model response here..."
    )
    _uiState.update { it.copy(chatTurns = it.chatTurns + newTurn) }
  }

  fun updateChatTurnText(id: String, newText: String) {
    _uiState.update { s ->
      s.copy(chatTurns = s.chatTurns.map { if (it.id == id) it.copy(text = newText) else it })
    }
  }

  fun deleteChatTurn(id: String) {
    _uiState.update { s ->
      s.copy(chatTurns = s.chatTurns.filter { it.id != id })
    }
  }

  fun clearChat() {
    _uiState.update { it.copy(chatTurns = emptyList(), generationStatus = GenerationStatus.Idle) }
  }

  // --- Structured Prompt Methods ---
  fun updateStructuredLabels(inputLabel: String, outputLabel: String) {
    _uiState.update { it.copy(structuredInputLabel = inputLabel, structuredOutputLabel = outputLabel) }
  }

  fun updateStructuredTestInput(text: String) {
    _uiState.update { it.copy(structuredTestInput = text) }
  }

  fun addStructuredExample() {
    val newEx = StructuredExample(
      id = UUID.randomUUID().toString(),
      input = "New sample input",
      output = "{\"result\": \"sample output\"}"
    )
    _uiState.update { it.copy(structuredExamples = it.structuredExamples + newEx) }
  }

  fun updateStructuredExample(id: String, input: String, output: String) {
    _uiState.update { s ->
      s.copy(structuredExamples = s.structuredExamples.map { if (it.id == id) it.copy(input = input, output = output) else it })
    }
  }

  fun deleteStructuredExample(id: String) {
    _uiState.update { s ->
      s.copy(structuredExamples = s.structuredExamples.filter { it.id != id })
    }
  }

  fun runStructuredTest() {
    val state = _uiState.value
    val testInput = state.structuredTestInput.ifBlank { "Delivery took 3 weeks but product works." }
    _uiState.update { it.copy(structuredTestOutput = "", generationStatus = GenerationStatus.Streaming("", 0.1)) }

    // Synthesize prompt with few shots
    val promptBuilder = StringBuilder()
    promptBuilder.append("Given the following few-shot examples:\n\n")
    state.structuredExamples.forEachIndexed { idx, ex ->
      promptBuilder.append("Example ${idx + 1}:\n")
      promptBuilder.append("${state.structuredInputLabel}: ${ex.input}\n")
      promptBuilder.append("${state.structuredOutputLabel}: ${ex.output}\n\n")
    }
    promptBuilder.append("Now generate the output for this test input:\n")
    promptBuilder.append("${state.structuredInputLabel}: $testInput\n")
    promptBuilder.append("${state.structuredOutputLabel}:")

    generationJob?.cancel()
    generationJob = viewModelScope.launch {
      try {
        val result = engine.executePrompt(
          prompt = promptBuilder.toString(),
          systemInstruction = state.systemInstruction,
          model = state.selectedModel,
          temperature = 0.2f, // Structured runs best at lower temp
          topP = state.topP,
          topK = state.topK,
          maxTokens = state.maxOutputTokens,
          searchGrounding = false,
          codeExecution = false,
          attachments = emptyList(),
          customApiKey = state.customApiKey,
          onChunk = { chunk ->
            _uiState.update { it.copy(structuredTestOutput = chunk) }
          }
        )
        _uiState.update {
          it.copy(
            structuredTestOutput = result.text.trim(),
            generationStatus = GenerationStatus.Success(result.text, result.metrics)
          )
        }
      } catch (e: Exception) {
        _uiState.update {
          it.copy(
            structuredTestOutput = "Error: ${e.message}",
            generationStatus = GenerationStatus.Error(e.message ?: "Error")
          )
        }
      }
    }
  }

  // --- Panels & Modals ---
  fun toggleRightPanel() {
    _uiState.update { it.copy(isRightPanelOpen = !it.isRightPanelOpen) }
  }

  fun setRightPanelOpen(open: Boolean) {
    _uiState.update { it.copy(isRightPanelOpen = open) }
  }

  fun toggleLeftDrawer() {
    _uiState.update { it.copy(isLeftDrawerOpen = !it.isLeftDrawerOpen) }
  }

  fun setLeftDrawerOpen(open: Boolean) {
    _uiState.update { it.copy(isLeftDrawerOpen = open) }
  }

  fun setGetCodeModalOpen(open: Boolean) {
    _uiState.update { it.copy(isGetCodeModalOpen = open) }
  }

  fun selectExportLanguage(lang: ExportLanguage) {
    _uiState.update { it.copy(selectedExportLanguage = lang) }
  }

  fun setGetApiKeyModalOpen(open: Boolean) {
    _uiState.update { it.copy(isGetApiKeyModalOpen = open) }
  }

  fun setSettingsModalOpen(open: Boolean) {
    _uiState.update { it.copy(isSettingsModalOpen = open) }
  }

  fun toggleTheme() {
    _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
  }

  fun setCustomApiKey(key: String) {
    _uiState.update { it.copy(customApiKey = key) }
  }

  fun selectProject(project: String) {
    _uiState.update { it.copy(currentProject = project) }
  }

  fun addProject(name: String) {
    val clean = name.trim()
    if (clean.isNotBlank() && !_uiState.value.availableProjects.contains(clean)) {
      _uiState.update {
        it.copy(
          availableProjects = it.availableProjects + clean,
          currentProject = clean
        )
      }
    }
  }
}
