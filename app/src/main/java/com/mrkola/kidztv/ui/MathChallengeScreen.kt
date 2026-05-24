package com.mrkola.kidztv.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mrkola.kidztv.R
import kotlin.random.Random

@Composable
fun MathChallengeScreen(
    onSuccess: () -> Unit,
    onCancel: () -> Unit
) {
    val num1 = remember { Random.nextInt(1, 11) }
    val num2 = remember { Random.nextInt(1, 11) }
    val correctAnswer = num1 + num2

    var userAnswer by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF5E35B1), Color(0xFF7E57C2))
                )
            )
    ) {
        val isLandscape = maxWidth > maxHeight

        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
                .fillMaxWidth(if (isLandscape) 0.9f else 0.95f),
            shape = RoundedCornerShape(32.dp),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            if (isLandscape) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    ProblemSection(
                        num1 = num1,
                        num2 = num2,
                        userAnswer = userAnswer,
                        showError = showError,
                        modifier = Modifier.weight(1f)
                    )

                    NumpadSection(
                        userAnswer = userAnswer,
                        onAnswerChange = {
                            userAnswer = it
                            showError = false
                        },
                        onSubmit = {
                            if (userAnswer.toIntOrNull() == correctAnswer) {
                                onSuccess()
                            } else {
                                showError = true
                                userAnswer = ""
                            }
                        },
                        onCancel = onCancel,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ProblemSection(
                        num1 = num1,
                        num2 = num2,
                        userAnswer = userAnswer,
                        showError = showError
                    )

                    Spacer(Modifier.height(16.dp))

                    NumpadSection(
                        userAnswer = userAnswer,
                        onAnswerChange = {
                            userAnswer = it
                            showError = false
                        },
                        onSubmit = {
                            if (userAnswer.toIntOrNull() == correctAnswer) {
                                onSuccess()
                            } else {
                                showError = true
                                userAnswer = ""
                            }
                        },
                        onCancel = onCancel
                    )
                }
            }
        }
    }
}


@Composable
private fun ProblemSection(
    num1: Int,
    num2: Int,
    userAnswer: String,
    showError: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.parent_verification),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            stringResource(R.string.solve_math_problem),
            color = Color.Gray
        )

        Spacer(Modifier.height(24.dp))

        Text(
            "$num1 + $num2 =",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF7E57C2)
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = if (userAnswer.isEmpty()) "?" else userAnswer,
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold
        )

        if (showError) {
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.incorrect_try_again),
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun NumpadSection(
    userAnswer: String,
    onAnswerChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Numpad(
            onDigit = { digit ->
                if (userAnswer.length < 3) {
                    onAnswerChange(userAnswer + digit)
                }
            },
            onDelete = {
                onAnswerChange(userAnswer.dropLast(1))
            },
            onSubmit = onSubmit
        )

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.cancel))
        }
    }
}

@Composable
private fun Numpad(
    onDigit: (String) -> Unit,
    onDelete: () -> Unit,
    onSubmit: () -> Unit
) {
    val buttons = listOf(
        "1","2","3",
        "4","5","6",
        "7","8","9",
        "⌫","0","OK"
    )

    Column {
        buttons.chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { label ->
                    Button(
                        onClick = {
                            when (label) {
                                "⌫" -> onDelete()
                                "OK" -> onSubmit()
                                else -> onDigit(label)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                    ) {
                        Text(
                            if (label == "OK") stringResource(R.string.ok) else label,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}