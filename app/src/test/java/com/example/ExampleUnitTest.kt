package com.example

import com.example.data.CodeGenerator
import com.example.model.*
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testCodeGeneratorPython() {
    val model = AvailableGeminiModels.first()
    val code = CodeGenerator.generateSnippet(
      language = ExportLanguage.PYTHON,
      model = model,
      prompt = "Hello world",
      systemInstruction = "Be concise",
      temperature = 0.7f,
      topP = 0.95f,
      topK = 40,
      maxTokens = 2048,
      searchGrounding = true,
      codeExecution = false,
      safetyThresholds = emptyMap()
    )

    assertTrue(code.contains("from google import genai"))
    assertTrue(code.contains("temperature=0.7"))
    assertTrue(code.contains("Hello world"))
    assertTrue(code.contains("google_search=types.GoogleSearch()"))
  }

  @Test
  fun testCodeGeneratorTypeScript() {
    val model = AvailableGeminiModels.first()
    val code = CodeGenerator.generateSnippet(
      language = ExportLanguage.TYPESCRIPT,
      model = model,
      prompt = "Generate test",
      systemInstruction = "",
      temperature = 1.0f,
      topP = 0.95f,
      topK = 40,
      maxTokens = 8192,
      searchGrounding = false,
      codeExecution = true,
      safetyThresholds = emptyMap()
    )

    assertTrue(code.contains("@google/genai"))
    assertTrue(code.contains("codeExecution"))
  }
}
