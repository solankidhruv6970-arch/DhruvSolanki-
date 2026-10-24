package com.example.data

import com.example.model.ExportLanguage
import com.example.model.GeminiModelOption
import com.example.model.SafetyCategory
import com.example.model.SafetyThreshold

object CodeGenerator {

  fun generateSnippet(
    language: ExportLanguage,
    model: GeminiModelOption,
    prompt: String,
    systemInstruction: String,
    temperature: Float,
    topP: Float,
    topK: Int,
    maxTokens: Int,
    searchGrounding: Boolean,
    codeExecution: Boolean,
    safetyThresholds: Map<SafetyCategory, SafetyThreshold>
  ): String {
    val cleanPrompt = if (prompt.isBlank()) "Explain the theory of general relativity simply." else prompt
    val escapedPrompt = cleanPrompt.replace("\"", "\\\"").replace("\n", "\\n")
    val cleanSystem = systemInstruction.replace("\"", "\\\"").replace("\n", "\\n")

    return when (language) {
      ExportLanguage.PYTHON -> """
# pip install google-genai
import os
from google import genai
from google.genai import types

client = genai.Client(api_key=os.environ.get("GEMINI_API_KEY"))

config = types.GenerateContentConfig(
    temperature=$temperature,
    top_p=$topP,
    top_k=$topK,
    max_output_tokens=$maxTokens,${if (cleanSystem.isNotBlank()) "\n    system_instruction=\"$cleanSystem\"," else ""}${if (searchGrounding) "\n    tools=[types.Tool(google_search=types.GoogleSearch())]," else ""}${if (codeExecution) "\n    tools=[types.Tool(code_execution=types.CodeExecution())]," else ""}
)

response = client.models.generate_content(
    model="${model.id}",
    contents="${escapedPrompt}",
    config=config,
)

print(response.text)
""".trimIndent()

      ExportLanguage.TYPESCRIPT -> """
// npm install @google/genai
import { GoogleGenAI } from "@google/genai";

const ai = new GoogleGenAI({
  apiKey: process.env.GEMINI_API_KEY,
});

async function run() {
  const response = await ai.models.generateContent({
    model: "${model.id}",
    contents: "${escapedPrompt}",
    config: {
      temperature: $temperature,
      topP: $topP,
      topK: $topK,
      maxOutputTokens: $maxTokens,${if (cleanSystem.isNotBlank()) "\n      systemInstruction: \"$cleanSystem\"," else ""}${if (searchGrounding) "\n      tools: [{ googleSearch: {} }]," else ""}${if (codeExecution) "\n      tools: [{ codeExecution: {} }]," else ""}
    },
  });

  console.log(response.text);
}

run();
""".trimIndent()

      ExportLanguage.CURL -> """
curl "https://generativelanguage.googleapis.com/v1beta/models/${model.id}:generateContent?key=${'$'}GEMINI_API_KEY" \
  -H "Content-Type: application/json" \
  -X POST \
  -d '{
    "contents": [
      {
        "parts": [
          { "text": "${escapedPrompt}" }
        ]
      }
    ],
    "generationConfig": {
      "temperature": $temperature,
      "topP": $topP,
      "topK": $topK,
      "maxOutputTokens": $maxTokens
    }${if (cleanSystem.isNotBlank()) """,
    "systemInstruction": {
      "parts": [{ "text": "$cleanSystem" }]
    }""" else ""}${if (searchGrounding) """,
    "tools": [{ "googleSearch": {} }]""" else ""}
  }'
""".trimIndent()

      ExportLanguage.SWIFT -> """
import GoogleGenAI

let ai = GenerativeModel(
    name: "${model.id}",
    apiKey: ProcessInfo.processInfo.environment["GEMINI_API_KEY"] ?? "",
    generationConfig: GenerationConfig(
        temperature: $temperature,
        topP: $topP,
        topK: $topK,
        maxOutputTokens: $maxTokens
    )${if (cleanSystem.isNotBlank()) """,
    systemInstruction: ModelContent(role: "system", parts: "$cleanSystem")""" else ""}
)

Task {
    do {
        let response = try await ai.generateContent("${escapedPrompt}")
        if let text = response.text {
            print(text)
        }
    } catch {
        print("Error: \(error)")
    }
}
""".trimIndent()
    }
  }
}
