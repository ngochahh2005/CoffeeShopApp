package com.example.coffeeshopapp.presentation.screen.user.aichat

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coffeeshopapp.presentation.viewmodel.AIChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIChatScreen(
    onBackClick: () -> Unit,
    viewModel: AIChatViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val messages = viewModel.messages

    AIChatContent(
        uiState = uiState,
        messages = messages,
        onBackClick = onBackClick,
        onSendMessage = { viewModel.sendMessage(it) }
    )
}