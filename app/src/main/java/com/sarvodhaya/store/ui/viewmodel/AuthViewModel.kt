package com.sarvodhaya.store.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarvodhaya.store.di.AppContainer
import com.sarvodhaya.store.domain.model.Address
import com.sarvodhaya.store.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(private val container: AppContainer) : ViewModel() {

    private val auth = container.authRepository

    val currentUser: StateFlow<UserProfile?> = auth.currentUser
    val isFirebaseConfigured: Boolean = auth.isConfigured

    data class UiState(
        val loading: Boolean = false,
        val error: String? = null,
        val message: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun signIn(email: String, password: String) {
        if (_state.value.loading) return
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null, message = null) }
            val result = auth.signIn(email, password)
            _state.update { current ->
                result.fold(
                    onSuccess = { current.copy(loading = false) },
                    onFailure = { error -> current.copy(loading = false, error = error.message) }
                )
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        if (_state.value.loading) return
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null, message = null) }
            val result = auth.register(name, email, password)
            _state.update { current ->
                result.fold(
                    onSuccess = { current.copy(loading = false) },
                    onFailure = { error -> current.copy(loading = false, error = error.message) }
                )
            }
        }
    }

    fun signOut() {
        viewModelScope.launch { auth.signOut() }
    }

    fun saveProfile(name: String, phone: String, address: Address) {
        if (_state.value.loading) return
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null, message = null) }
            val result = auth.saveProfile(name, phone, address)
            _state.update { current ->
                result.fold(
                    onSuccess = { current.copy(loading = false, message = "Profile updated") },
                    onFailure = { error -> current.copy(loading = false, error = error.message) }
                )
            }
        }
    }

    fun publishCatalog() {
        if (_state.value.loading) return
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null, message = null) }
            val result = container.catalogRepository.publishSeedCatalog()
            _state.update { current ->
                result.fold(
                    onSuccess = { current.copy(loading = false, message = "Demo catalog published to Firebase") },
                    onFailure = { error -> current.copy(loading = false, error = error.message) }
                )
            }
        }
    }

    fun clearMessages() = _state.update { it.copy(error = null, message = null) }
}
