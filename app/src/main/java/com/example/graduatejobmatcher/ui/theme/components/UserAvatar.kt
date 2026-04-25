package com.example.graduatejobmatcher.ui.theme.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.graduatejobmatcher.model.User

@Composable
fun UserAvatar(
    name: String,
    imageBase64: String,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    backgroundColor: Color = avatarFallbackColor(name),
    contentColor: Color = Color.White,
    textSize: TextUnit,
    fontWeight: FontWeight = FontWeight.Bold
) {
    val trimmedImageData = imageBase64.trim()
    val bitmap = remember(trimmedImageData) {
        decodeBase64Image(trimmedImageData)
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "$name profile picture",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = avatarInitials(name),
                color = contentColor,
                fontSize = textSize,
                fontWeight = fontWeight
            )
        }
    }
}

@Composable
fun UserAvatar(
    user: User?,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    backgroundColor: Color = avatarFallbackColor(user?.name.orEmpty()),
    contentColor: Color = Color.White,
    textSize: TextUnit,
    fontWeight: FontWeight = FontWeight.Bold
) {
    UserAvatar(
        name = user?.name.orEmpty(),
        imageBase64 = user?.profileImageBase64.orEmpty(),
        modifier = modifier,
        shape = shape,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        textSize = textSize,
        fontWeight = fontWeight
    )
}

private fun decodeBase64Image(imageBase64: String) =
    runCatching {
        if (imageBase64.isBlank()) return@runCatching null
        val decodedBytes = Base64.decode(imageBase64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }.getOrNull()

fun avatarInitials(name: String?): String {
    val parts = name
        .orEmpty()
        .trim()
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }

    return when {
        parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}".uppercase()
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> "U"
    }
}

fun avatarFallbackColor(seed: String): Color {
    val palette = listOf(
        Color(0xFF3F51B5),
        Color(0xFF009688),
        Color(0xFF7E57C2),
        Color(0xFFEF6C00),
        Color(0xFF546E7A),
        Color(0xFF2E7D32)
    )
    val index = seed.firstOrNull()?.code?.rem(palette.size) ?: 0
    return palette[index]
}

val RoundedAvatarShape = RoundedCornerShape(12.dp)
