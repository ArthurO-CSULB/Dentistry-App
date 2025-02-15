package com.start.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/*
We define an AuthViewModel, which is a ViewModel class to manage the authentication state
of our app. We will be using Firebase Authentication as a backend service to handle the login,
signup, and logout functionality. We have our required dependencies within our build.gradle.kts.

Author Referenced: EasyTuto
URL: https://www.youtube.com/watch?v=KOnLpNZ4AFc&t=778s
*/

class AuthViewModel : ViewModel() {

    // We establish our authentication by getting an instance of the Firebase Authentication.
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // We declare a data holder called "_authState". This will store data that is mutable from
    // within this ViewModel class. The data that is stored is the authentication state of the app,
    // whether the user has logged in or not, or is loading.
    private val _authState = MutableLiveData<AuthState>()
    // We declare a public value 'authState' which will be observed by the login, home, and signup
    // pages. When authState changes state to a particular state (Authenticated, Unauthenticated,
    // Loading, or Error), the page will navigate to its next specific page assigned in its method.
    val authState: LiveData<AuthState> = _authState

    // When instance of AuthViewModel is created, it will automatically check the user's
    // authentication status.
    init {
        checkAuthStatus()
    }

    // Method to check the authentication status of the user.
    fun checkAuthStatus(){
        // Check firebase. If the current user is not logged in currently...
        if (auth.currentUser == null){
            // The authentication state is 'Unauthenticated'.
            _authState.value = AuthState.UnAuthenticated
        }
        // If the current user is logged in currently...
        else{
            // The authentication state is 'Authenticated'.
            _authState.value = AuthState.Authenticated
        }
    }

    // Method for user to login using email and password.
    fun login(email : String, password : String){
        // If there is no email or no password that has been passed...
        if (email.isEmpty() || password.isEmpty())
        {
            // The authentication state is "Error" with message.
            _authState.value = AuthState.Error("Empty email or password")
            // Login method terminates.
            return
        }

        // If there is both email and password...
        // The authentication state is "Loading"
        _authState.value = AuthState.Loading
        // Attempt to sign into Firebase using the email and password.
        auth.signInWithEmailAndPassword(email, password)
            // Add a listener to the sign task in process.
            .addOnCompleteListener {task->
                // If the sign-in is successful...
                if (task.isSuccessful){
                    // The authentication state is "Authenticated"
                    _authState.value = AuthState.Authenticated
                }
                // If the sign-in task is not successful...
                else{
                    // The authentication state is an "Error" with a a message
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }


    // Method for user to sign up using email and password.
    fun signup(email : String, password : String, firstName: String, lastName: String) {
        // If there is no email or no password that has been passed...
        if (email.isEmpty() || password.isEmpty())
        {
            // The authentication state is "Error" with message.
            _authState.value = AuthState.Error("Empty email or password")
            // Sign-in method terminates.
            return
        }

        // If there is both email and password...
        // The authentication state is "Loading"
        _authState.value = AuthState.Loading
        // Attempt to sign up to Firebase using the email and password.
        auth.createUserWithEmailAndPassword(email, password)
            // Add a listener to the sign in task in process.
            .addOnCompleteListener{task->
                // If sign-up is successful...
                if (task.isSuccessful) {
                    // The authentication state is "Authenticated"
                    _authState.value = AuthState.Authenticated

                    // Create hashmap of email contents containing the name of user.
                    val userDetails = hashMapOf(
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "email" to email
                    )

                    // Add user details to database
                    val user = auth.currentUser
                    // Add user details to the database
                    db.collection("accounts").document(user?.uid ?: "UID not found")
                        .set(userDetails)
                        // If writing to database is successful, output it's successful
                        .addOnSuccessListener {
                            Log.d(
                                "Database Update",
                                "Document successfully written!"
                            )
                        }
                        // If failed, output it's unsuccessful
                        .addOnFailureListener { e ->
                            Log.w(
                                "Database Update",
                                "Error writing document",
                                e
                            )
                        }

                }
                // If the sign-up task is not successful...
                else {
                    // The authentication state is an "Error" with a a message
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    // Method for user to sign-out.
    fun signout(){
        // Sign out of Firebase.
        auth.signOut()
        // Authentication state is "Unauthenticated"
        _authState.value = AuthState.UnAuthenticated

    }
}

/*
    -Sealed class "AuthState" to represent different authentication states.
    -We use a sealed class to ensure that "AuthState" has these only defined states below.
    -Use sealed so that when the authState changes, the UI reacts by observing it and processing it
        in a when expression to show what the user should see next.
 */
sealed class AuthState {
    // Authenticated State
    object Authenticated : AuthState()
    // Unauthenticated State
    object UnAuthenticated : AuthState()
    // Loading State
    object Loading : AuthState()
    // Error state
    data class Error(val message : String) : AuthState()
}
