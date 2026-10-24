package com.example.model

enum class PromptType(val title: String, val iconDesc: String) {
  FREEFORM("Freeform", "Single prompt playground"),
  CHAT("Chat", "Multi-turn conversation"),
  STRUCTURED("Structured", "Few-shot input & output grid")
}

data class GeminiModelOption(
  val id: String,
  val displayName: String,
  val tag: String,
  val contextWindow: String,
  val description: String,
  val speedTier: String
)

val AvailableGeminiModels = listOf(
  GeminiModelOption(
    id = "gemini-3.5-flash",
    displayName = "Gemini 3.5 Flash",
    tag = "Fast & Multimodal",
    contextWindow = "1,048,576 tokens",
    description = "Optimized for speed, low latency, and multimodal comprehension.",
    speedTier = "⚡ Fast"
  ),
  GeminiModelOption(
    id = "gemini-3.1-pro-preview",
    displayName = "Gemini 3.1 Pro",
    tag = "Advanced Reasoning",
    contextWindow = "2,097,152 tokens",
    description = "Best in class for complex reasoning, mathematical proofs, and code.",
    speedTier = "🧠 Intelligent"
  ),
  GeminiModelOption(
    id = "gemini-2.5-flash-image",
    displayName = "Gemini 2.5 Flash Image",
    tag = "Vision & Image Gen",
    contextWindow = "1,048,576 tokens",
    description = "High-fidelity visual understanding, spatial layout, and image creation.",
    speedTier = "🎨 Creative"
  ),
  GeminiModelOption(
    id = "gemini-flash-latest",
    displayName = "Gemini Flash (Latest)",
    tag = "Stable Production",
    contextWindow = "1,048,576 tokens",
    description = "Stable flagship flash model for production workloads.",
    speedTier = "⚡ Fast"
  )
)

enum class SafetyCategory(val displayName: String, val apiName: String) {
  HATE_SPEECH("Hate Speech", "HARM_CATEGORY_HATE_SPEECH"),
  HARASSMENT("Harassment", "HARM_CATEGORY_HARASSMENT"),
  SEXUALLY_EXPLICIT("Sexually Explicit", "HARM_CATEGORY_SEXUALLY_EXPLICIT"),
  DANGEROUS_CONTENT("Dangerous Content", "HARM_CATEGORY_DANGEROUS_CONTENT")
}

enum class SafetyThreshold(val label: String, val apiValue: String) {
  BLOCK_NONE("Block none", "BLOCK_NONE"),
  BLOCK_FEW("Block few", "BLOCK_ONLY_HIGH"),
  BLOCK_SOME("Block some", "BLOCK_MEDIUM_AND_ABOVE"),
  BLOCK_MOST("Block most", "BLOCK_LOW_AND_ABOVE")
}

enum class MediaType(val label: String, val mimePrefix: String) {
  IMAGE("Image", "image/"),
  AUDIO("Audio", "audio/"),
  VIDEO("Video", "video/"),
  PDF("PDF Document", "application/pdf")
}

data class MediaAttachment(
  val id: String,
  val name: String,
  val type: MediaType,
  val uriOrUrl: String,
  val sizeText: String,
  val colorSeed: Long = 0xFF1E3A5F
)

data class ChatTurn(
  val id: String,
  val role: String, // "user" or "model"
  val text: String,
  val attachments: List<MediaAttachment> = emptyList(),
  val isStreaming: Boolean = false
)

data class StructuredExample(
  val id: String,
  val input: String,
  val output: String
)

data class ExecutionMetrics(
  val inputTokens: Int = 0,
  val outputTokens: Int = 0,
  val totalTokens: Int = 0,
  val latencySeconds: Double = 0.0,
  val tokensPerSec: Double = 0.0,
  val finishReason: String = "STOP"
)

enum class ExportLanguage(val displayName: String, val ext: String) {
  PYTHON("Python (google-genai)", "py"),
  TYPESCRIPT("JavaScript / TypeScript", "ts"),
  CURL("cURL (REST)", "sh"),
  SWIFT("Swift (SDK)", "swift")
}

sealed interface GenerationStatus {
  object Idle : GenerationStatus
  data class Streaming(val partialText: String, val elapsedSec: Double) : GenerationStatus
  data class Success(
    val responseText: String,
    val metrics: ExecutionMetrics,
    val searchSources: List<String> = emptyList(),
    val executedCode: String? = null,
    val codeOutput: String? = null
  ) : GenerationStatus
  data class Error(val message: String) : GenerationStatus
}
