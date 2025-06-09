package com.example.multimodelai.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FormattedResponse(
    text: String,
    modifier: Modifier = Modifier
) {
    val formattedText = buildAnnotatedString {
        val lines = text.split("\n")
        var currentIndex = 0
        
        lines.forEach { line ->
            when {
                // Handle headers (markdown ##)
                line.startsWith("##") -> {
                    withStyle(style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )) {
                        append(line.removePrefix("##").trim())
                    }
                }
                
                // Handle bold text (**text**)
                line.contains("**") -> {
                    var tempLine = line
                    while (tempLine.contains("**")) {
                        val beforeBold = tempLine.substringBefore("**")
                        append(beforeBold)
                        
                        tempLine = tempLine.substringAfter("**")
                        if (tempLine.contains("**")) {
                            val boldText = tempLine.substringBefore("**")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(boldText)
                            }
                            tempLine = tempLine.substringAfter("**")
                        } else {
                            append("**$tempLine")
                            break
                        }
                    }
                    if (!tempLine.contains("**") && tempLine.isNotEmpty()) {
                        append(tempLine)
                    }
                }
                
                // Handle bullet points (• or -)
                line.trim().startsWith("•") || line.trim().startsWith("-") -> {
                    append("  • ${line.trim().removePrefix("•").removePrefix("-").trim()}")
                }
                
                // Handle checkmarks (✅)
                line.contains("✅") -> {
                    append(line.replace("✅", "✓"))
                }
                
                // Handle numbered lists
                line.trim().matches(Regex("^\\d+\\..*")) -> {
                    append("  ${line.trim()}")
                }
                
                // Regular text
                else -> {
                    append(line)
                }
            }
            
            // Add line break if not the last line
            if (currentIndex < lines.size - 1) {
                append("\n")
            }
            currentIndex++
        }
    }

    Text(
        text = formattedText,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier.fillMaxWidth(),
        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
    )
} 