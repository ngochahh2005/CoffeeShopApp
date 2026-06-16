package com.example.coffeeshopapp.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshopapp.data.model.dto.AIChatRequestDto
import com.example.coffeeshopapp.data.remote.NetworkClient
import com.example.coffeeshopapp.utils.getErrorMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatMessage(
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class AIChatUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

class AIChatViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AIChatUiState())
    val uiState = _uiState.asStateFlow()

    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> = _messages

    init {
        _messages.add(ChatMessage("Xin chào! Tôi là trợ lý ảo của Coffee Shop. Tôi có thể giúp gì cho bạn?", false))
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val userMessage = ChatMessage(text, true)
        _messages.add(userMessage)

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = NetworkClient.api.chatWithAI(AIChatRequestDto(text))
                if (response.result != null) {
                    _messages.add(ChatMessage(response.result, false))
                } else {
                    _uiState.update { it.copy(error = response.message ?: "Không nhận được phản hồi từ AI") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.getErrorMessage()) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
