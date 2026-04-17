package com.example.graduatejobmatcher.screens.employer

import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumePreviewScreen(
    navController: NavController,
    resumeUrl: String
) {
    val context = LocalContext.current
    val isImageAttachment = remember(resumeUrl) { resumeUrl.isPreviewImage(context) }
    val title = if (isImageAttachment) "Attachment Preview" else "Resume Preview"

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF3D73E6)
                )
            )
        }
    ) { innerPadding ->
        if (isImageAttachment) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                factory = { androidContext ->
                    ImageView(androidContext).apply {
                        scaleType = ImageView.ScaleType.FIT_CENTER
                        adjustViewBounds = true
                    }
                },
                update = { imageView ->
                    imageView.setImageURI(Uri.parse(resumeUrl))
                }
            )
        } else {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                factory = { contextForWebView ->
                    WebView(contextForWebView).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                    }
                },
                update = { webView ->
                    webView.loadUrl(resumeUrl)
                }
            )
        }
    }
}

private fun String.isPreviewImage(context: android.content.Context): Boolean {
    if (isBlank()) return false

    val parsedUri = Uri.parse(this)
    val mimeType = runCatching {
        context.contentResolver.getType(parsedUri)
    }.getOrNull()

    if (mimeType?.startsWith("image/") == true) {
        return true
    }

    val lower = lowercase()
    return lower.endsWith(".png") ||
        lower.endsWith(".jpg") ||
        lower.endsWith(".jpeg") ||
        lower.endsWith(".webp")
}
