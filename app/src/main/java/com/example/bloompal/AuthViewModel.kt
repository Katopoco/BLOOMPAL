package com.example.bloompal

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AuthState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val currentUser: FirebaseUser? = null,
    val error: String? = null
)

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // ⭐ ADD CALLBACK FOR DASHBOARD VIEWMODEL
    private var onUserChanged: ((Boolean) -> Unit)? = null

    init {
        // Check if user is already signed in
        val currentUser = auth.currentUser
        _authState.value = AuthState(
            isAuthenticated = currentUser != null,
            currentUser = currentUser
        )
    }

    // ⭐ CALL THIS FROM YOUR MAINACTIVITY/NAVIGATION TO CONNECT VIEWMODELS
    fun setOnUserChangedListener(callback: (Boolean) -> Unit) {
        onUserChanged = callback
    }

    fun signUp(email: String, password: String, name: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            _authState.value = _authState.value.copy(
                error = "Please fill in all fields"
            )
            return
        }

        if (password.length < 6) {
            _authState.value = _authState.value.copy(
                error = "Password must be at least 6 characters"
            )
            return
        }

        _authState.value = _authState.value.copy(isLoading = true, error = null)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("AuthViewModel", "Sign up successful")
                    val user = auth.currentUser
                    _authState.value = AuthState(
                        isAuthenticated = true,
                        currentUser = user
                    )
                    // ⭐ NOTIFY THAT USER LOGGED IN
                    onUserChanged?.invoke(true)
                    onSuccess()
                } else {
                    Log.e("AuthViewModel", "Sign up failed", task.exception)
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = task.exception?.message ?: "Sign up failed"
                    )
                }
            }
    }

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = _authState.value.copy(
                error = "Please fill in all fields"
            )
            return
        }

        _authState.value = _authState.value.copy(isLoading = true, error = null)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("AuthViewModel", "Sign in successful")
                    val user = auth.currentUser
                    _authState.value = AuthState(
                        isAuthenticated = true,
                        currentUser = user
                    )
                    // ⭐ NOTIFY THAT USER LOGGED IN
                    onUserChanged?.invoke(true)
                    onSuccess()
                } else {
                    Log.e("AuthViewModel", "Sign in failed", task.exception)
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = task.exception?.message ?: "Sign in failed"
                    )
                }
            }
    }

    fun signOut() {
        // ⭐ NOTIFY THAT USER LOGGED OUT (BEFORE SIGNING OUT)
        onUserChanged?.invoke(false)

        auth.signOut()
        _authState.value = AuthState(
            isAuthenticated = false,
            currentUser = null
        )
    }

    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }
}