package com.alimoor.flutterl10nhelper

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import org.json.JSONObject
import java.io.File
import java.util.regex.Pattern

class GenerateL10nAction : AnAction() {

    // Regex patterns for detecting string literals (both single and double quotes)
    private val singleQuotePattern = Pattern.compile("'((?:\\\\.|[^'\\\\])*)'")
    private val doubleQuotePattern = Pattern.compile("\"((?:\\\\.|[^\"\\\\])*)\"")
    private val tripleSingleQuotePattern = Pattern.compile("'''((?:.|\n)*?)'''")
    private val tripleDoubleQuotePattern = Pattern.compile("\"\"\"((?:.|\n)*?)\"\"\"")

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabledAndVisible = editor != null && editor.selectionModel.hasSelection()
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return

        // Get the string at current cursor position
        val stringContent = getStringAtCursor(editor) ?: return

        // Generate l10n key
        val l10nKey = generateL10nKey(stringContent)

        // Add to ARB files in lib/l10n/
        addToAllArbFiles(project, l10nKey, stringContent)

        // Replace in code
        replaceStringWithL10n(project, editor, l10nKey)

        // Run flutter pub get
        runFlutterPubGet(project)
    }

    private fun getStringAtCursor(editor: Editor): String? {
        val document = editor.document
        val caretOffset = editor.caretModel.offset

        // Get text around cursor (larger context to ensure we capture the string)
        val lineNumber = document.getLineNumber(caretOffset)
        val lineStart = document.getLineStartOffset(lineNumber)
        val lineEnd = document.getLineEndOffset(lineNumber)
        val lineText = document.getText(TextRange(lineStart, lineEnd))

        // Calculate offset within the line
        val offsetInLine = caretOffset - lineStart

        // Try to find a string literal at the cursor position
        for (pattern in listOf(singleQuotePattern, doubleQuotePattern, tripleSingleQuotePattern, tripleDoubleQuotePattern)) {
            val matcher = pattern.matcher(lineText)
            while (matcher.find()) {
                // Check if cursor is within this string
                if (offsetInLine >= matcher.start() && offsetInLine <= matcher.end()) {
                    return matcher.group(1)
                }
            }
        }

        return null
    }

    private fun generateL10nKey(text: String): String {
        // Convert "Hello My Friend" to "hello_my_friend"
        return text.lowercase()
            .replace(Regex("[^a-z0-9]"), "_") // Replace non-alphanumeric with underscore
            .replace(Regex("_+"), "_")        // Replace multiple underscores with single
            .trim('_')                        // Trim leading/trailing underscores
    }

    private fun addToAllArbFiles(project: Project, key: String, value: String) {
        // Find the ARB files in the lib/l10n/ directory
        val basePath = project.basePath ?: return
        val l10nDir = File("$basePath/lib/l10n")
        if (l10nDir.exists() && l10nDir.isDirectory) {
            val arbFiles = l10nDir.listFiles { file -> file.extension == "arb" }
            if (arbFiles.isNullOrEmpty()) {
                // No ARB files found, create app_en.arb
                val defaultArbFile = File(l10nDir, "app_en.arb")
                addToArbFile(project, defaultArbFile, key, value)
            } else {
                // Add to all existing ARB files
                arbFiles.forEach { arbFile ->
                    addToArbFile(project, arbFile, key, value)
                }
            }
        } else {
            // Directory does not exist, create it and add app_en.arb
            l10nDir.mkdirs()
            val defaultArbFile = File(l10nDir, "app_en.arb")
            addToArbFile(project, defaultArbFile, key, value)
        }
    }

    private fun addToArbFile(project: Project, arbFile: File, key: String, value: String) {
        // Read existing content
        val jsonObject = if (arbFile.exists()) {
            JSONObject(arbFile.readText())
        } else {
            JSONObject()
        }

        // Add new key-value pair
        jsonObject.put(key, value)

        // Write back to file
        WriteCommandAction.runWriteCommandAction(project) {
            arbFile.parentFile.mkdirs() // Ensure directory exists
            arbFile.writeText(jsonObject.toString(2)) // Pretty print with indent of 2
        }
    }

    private fun replaceStringWithL10n(project: Project, editor: Editor, key: String) {
        val document = editor.document
        val caretOffset = editor.caretModel.offset
        val lineNumber = document.getLineNumber(caretOffset)
        val lineStart = document.getLineStartOffset(lineNumber)
        val lineEnd = document.getLineEndOffset(lineNumber)
        val lineText = document.getText(TextRange(lineStart, lineEnd))

        // Find the string at cursor position
        for (pattern in listOf(singleQuotePattern, doubleQuotePattern, tripleSingleQuotePattern, tripleDoubleQuotePattern)) {
            val matcher = pattern.matcher(lineText)
            while (matcher.find()) {
                // Check if cursor is within this string
                val offsetInLine = caretOffset - lineStart
                if (offsetInLine >= matcher.start() && offsetInLine <= matcher.end()) {
                    // Calculate absolute positions of the string in the document
                    val stringStart = lineStart + matcher.start()
                    val stringEnd = lineStart + matcher.end()

                    // Replace the string with AppLocalizations reference
                    WriteCommandAction.runWriteCommandAction(project) {
                        document.replaceString(stringStart, stringEnd, "AppLocalizations.of(context)!.$key")

                        // Add import if needed (would need to check if it's already imported)
                        // This is a simple version - ideally would check if import exists first
                        addImportIfNeeded(project, document)
                    }

                    return
                }
            }
        }
    }

    private fun addImportIfNeeded(project: Project, document: com.intellij.openapi.editor.Document) {
        // Simple implementation - just add import if it's not already there
        val text = document.text
        if (!text.contains("import 'package:flutter_gen/gen_l10n/app_localizations.dart'")) {
            // Find where to insert - after last import or at beginning
            val lastImportEnd = text.lastIndexOf("import ").let {
                if (it >= 0) {
                    text.indexOf(';', it) + 1
                } else {
                    0
                }
            }

            // Add the import
            WriteCommandAction.runWriteCommandAction(project) {
                document.insertString(lastImportEnd, "\nimport 'package:flutter_gen/gen_l10n/app_localizations.dart';\n")
            }
        }
    }

    private fun runFlutterPubGet(project: Project) {
        val basePath = project.basePath ?: return
        val processBuilder = ProcessBuilder("flutter", "pub", "get")
        processBuilder.directory(File(basePath))
        processBuilder.redirectErrorStream(true)

        try {
            val process = processBuilder.start()
            process.inputStream.bufferedReader().use { reader ->
                reader.lines().forEach { println(it) }
            }
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}