// NoteEditorItem.kt
package com.example.happybirthday

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape // Import for rounded corners
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip // Import for clipping shape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb // Import for color conversion
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random // Import Random

// Function to generate a vibrant color based on note content hash
@Composable
fun rememberNoteColor(noteId: String): Color {
    return remember(noteId) {
        val seed = noteId.hashCode()
        val random = Random(seed)
        Color(
            red = random.nextFloat() * 0.6f + 0.4f, // Brighter reds
            green = random.nextFloat() * 0.6f + 0.4f, // Brighter greens
            blue = random.nextFloat() * 0.6f + 0.4f, // Brighter blues
            alpha = 1f
        )
    }
}

// Function to determine readable text color based on background
fun getTextColorForBackground(backgroundColor: Color): Color {
    val luminance = (0.299 * backgroundColor.red + 0.587 * backgroundColor.green + 0.114 * backgroundColor.blue)
    return if (luminance > 0.5) Color.Black else Color.White
}


@Composable
fun NoteEditorItem(
    note: NoteItem,
    // Remove unused parameter
    // onUpdate: (NoteItem) -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier // Add modifier parameter
) {
    val noteColor = rememberNoteColor(noteId = note.id) // Get the note color
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val textColor = getTextColorForBackground(noteColor) // Determine text color

    Card(
        modifier = modifier // Use the passed modifier
            .fillMaxWidth()
            // .padding(vertical = 4.dp) // Padding handled by LazyColumn arrangement
            .clip(RoundedCornerShape(12.dp)) // Add rounded corners
            .clip(RoundedCornerShape(12.dp)) // Add rounded corners
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Slightly more elevation
        shape = RoundedCornerShape(12.dp), // Apply rounded shape
        colors = CardDefaults.cardColors(containerColor = noteColor) // Use dynamic color
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = note.title.takeIf { it.isNotBlank() } ?: note.text.lineSequence().firstOrNull()?.take(80) ?: "New Note",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), // Bolder title
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
                color = textColor // Use calculated text color
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (note.title.isNotBlank() && note.text.isNotBlank()) {
                Text(
                    text = note.text,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 8,
                    overflow = TextOverflow.Ellipsis,
                    color = textColor.copy(alpha = 0.85f), // Slightly transparent text color for body
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Updated: ${dateFormat.format(Date(note.updatedAt))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor.copy(alpha = 0.7f) // Slightly transparent text color for date
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = textColor.copy(alpha = 0.7f) // Use text color for icon tint
                    )
                }
            }
        }
    }
}