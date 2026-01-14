package com.mrkola.kidztv.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri

@Composable
fun AboutDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "About KidzTV",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "KidzTV",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "A safe, offline video player made for kids.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "This app lets you download your own videos from YouTube so your kids can watch them quietly offline — without ads, recommendations, or distractions.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Parents stay in control with screen locking and protected settings.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "No ads • No tracking • Offline only",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Version 1.0",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                TextButton(
                    onClick = {
                        openGithubRepo(context)
                    }
                ) {
                    Text("Open source")
                }

                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    )
}

private fun openGithubRepo(context: Context) {
    val intent = Intent(
        Intent.ACTION_VIEW,
        "https://github.com/eltonkola/kidztv".toUri()
    )
    context.startActivity(intent)
}
