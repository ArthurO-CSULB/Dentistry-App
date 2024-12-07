package com.start

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {

    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    // Check whether or not user is allowed to login
    fun checkAuthStatus(){
        if (auth.currentUser == null){
            _authState.value = AuthState.UnAuthenticated
        }
        else{
            _authState.value = AuthState.Authenticated
        }
    }

    // Login function
    fun login(email : String, password : String){
        // Check for empty values
        if (email.isEmpty() || password.isEmpty())
        {
            _authState.value = AuthState.Error("Empty email or password")
            return
        }

        // Verify user login information
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{task->
                if (task.isSuccessful){
                    _authState.value = AuthState.Authenticated
                }
                else{
                    _authState.value = AuthState.Error(task.exception?.message?:"Something went wrong")
                }
            }
    }


    // Signup function
    fun signup(email : String, password : String){
        // Check for empty values
        if (email.isEmpty() || password.isEmpty())
        {
            _authState.value = AuthState.Error("Empty email or password")
            return
        }

        // Verify user login information
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{task->
                if (task.isSuccessful){
                    _authState.value = AuthState.Authenticated
                }
                else{
                    _authState.value = AuthState.Error(task.exception?.message?:"Something went wrong")
                }
            }
    }

    // Signout function
    fun signout(){
        auth.signOut()
        _authState.value = AuthState.UnAuthenticated
    }
}


sealed class AuthState{
    object Authenticated : AuthState()
    object UnAuthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message : String) : AuthState()
}