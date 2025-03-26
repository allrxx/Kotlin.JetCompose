package com.example.happybirthday

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class NoteItem(
    val id: String,
    val text: String = "",
    val isExpanded: Boolean = false
)

@Composable
fun NoteEditorItem(
    note: NoteItem,
    onUpdate: (NoteItem) -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 160.dp, height = 240.dp)
            .animateContentSize(animationSpec = tween(durationMillis = 300))
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFF5F5F5))
            .clickable { if (!note.isExpanded) onUpdate(note.copy(isExpanded = true)) }
            .padding(20.dp, 16.dp)
    ) {
        if (note.isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                TextField(
                    value = note.text,
                    onValueChange = { newText ->
                        onUpdate(note.copy(text = newText))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(8.dp),
                    placeholder = { Text("Enter your note...") },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }
                    TextButton(
                        onClick = { onUpdate(note.copy(isExpanded = false)) }
                    ) {
                        Text("Save")
                    }
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "Note",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = note.text.ifEmpty { "Tap to add note..." },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    maxLines = 7,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoteEditorPreview() {
    MaterialTheme {
        NoteEditorItem(
            note = NoteItem(id = "1", text = "Preview Note", isExpanded = false),
            onUpdate = {},
            onDelete = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NoteEditorExpandedPreview() {
    MaterialTheme {
        NoteEditorItem(
            note = NoteItem(id = "1", text = "Preview Note", isExpanded = true),
            onUpdate = {},
            onDelete = {}
        )
    }
}