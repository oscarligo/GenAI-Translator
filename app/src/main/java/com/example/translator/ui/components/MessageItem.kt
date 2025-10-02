package com.example.translator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.translator.domain.model.Message
import com.example.translator.domain.model.MessageRole
import com.example.translator.domain.model.MessageType

@Composable
fun MessageItem(message: Message, modifier: Modifier = Modifier) {
    val isUser = message.role == MessageRole.USER
    val bg = if (isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(horizontalAlignment = if (isUser) Alignment.End else Alignment.Start) {
            Surface(
                color = bg,
                contentColor = contentColor,
                shape = RoundedCornerShape(16.dp)
            ) {
                when (message.type) {
                    MessageType.TEXT -> {
                        Text(
                            text = message.text.orEmpty(),
                            modifier = Modifier.padding(12.dp),
                            textAlign = if (isUser) TextAlign.End else TextAlign.Start,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    MessageType.IMAGE -> {
                        AsyncImage(
                            model = message.imageUri,
                            contentDescription = message.text ?: "Imagen",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.sizeIn(maxWidth = 280.dp).clip(RoundedCornerShape(16.dp))
                        )
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}

