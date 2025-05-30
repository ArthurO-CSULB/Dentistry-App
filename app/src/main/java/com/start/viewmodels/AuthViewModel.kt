package com.start.viewmodels

import android.R.bool
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/*
We define an AuthViewModel, which is a ViewModel class to manage the authentication state
of our app. We will be using Firebase Authentication as a backend service to handle the login,
signup, and logout functionality. We have our required dependencies within our build.gradle.kts.

Author Referenced: EasyTuto
URL: https://www.youtube.com/watch?v=KOnLpNZ4AFc&t=778s
*/

class AuthViewModel : ViewModel() {

    // We establish our authentication by getting an instance of the Firebase Authentication.
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
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
    fun checkAuthStatus() {

        val user = auth.currentUser

        // Check firebase. If the current user is not logged in currently...
        if (user == null) {
            // The authentication state is 'Unauthenticated'.
            _authState.value = AuthState.UnAuthenticated
            Log.d("Authentication Status Check", "User is unauthenticated")
        }
        // If the user is logged in but not verified...
        else if (!user.isEmailVerified) {
            // The authentication state is 'Unverified'
            _authState.value = AuthState.Unverified
            Log.d("Authentication Status Check", "User is unverified")
        }
        // If the user is logged in with email already verified...
        else {
            // The authentication state is 'Authenticated'.
            _authState.value = AuthState.Authenticated
            Log.d("Authentication Status Check", "User is authenticated")
        }
    }

    // Method for user to login using email and password.
    // ToDo: 2/1/2025 Create password rules to make passwords stronger, can be delayed so testing will be easy
    fun login(email: String, password: String) {
        // If there is no email or no password that has been passed...
        if (email.isEmpty() || password.isEmpty()) {
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
            .addOnCompleteListener { task ->
                // If the sign-in is successful...
                if (task.isSuccessful) {
                    // check if user can login properly
                    checkAuthStatus()
                }
                // If the sign-in task is not successful...
                else {
                    // The authentication state is an "Error" with a a message
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }


    // Method for user to sign up using email and password.
    suspend fun signup(email: String, password: String, firstName: String, lastName: String): Boolean {
        // If there is no email or no password that has been passed...
        if (email.isEmpty() || password.isEmpty()) {
            // The authentication state is "Error" with message.
            _authState.value = AuthState.Error("Empty email or password")
            // Sign-in method terminates.
            return false
        }

        var signUpFlag: Boolean = false

        // If there is both email and password...
        // The authentication state is "Loading"
        _authState.value = AuthState.Loading
        // Attempt to sign up to Firebase using the email and password.
        auth.createUserWithEmailAndPassword(email, password)
            // Add a listener to the sign in task in process.
            .addOnCompleteListener { task ->
                // If sign-up is successful...
                if (task.isSuccessful) {

                    // Create hashmap of email contents containing the name of user.
                    val userDetails = hashMapOf(
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "email" to email,
                        "experience" to 0,
                        "prestige" to 0
                    )

                    // Get current user
                    val user = auth.currentUser
                    // Add user details to the database
                    db.collection("accounts").document(user?.uid ?: "UID not found")
                        .set(userDetails)
                        // If writing to database is successful, output it's successful
                        .addOnSuccessListener {
                            Log.i(
                                "Database Update",
                                "Document successfully written!"
                            )

                            // If user is successfully added to database, send verification email,
                            // sign them out, then set flag to true
                            sendVerificationEmail()
                            signout()
                            signUpFlag = true;
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
            }.await()

        Log.d("Signup flag output", signUpFlag.toString())
        return signUpFlag
    }

    // Method for user to sign-out.
    fun signout() {
        // Sign out of Firebase.
        auth.signOut()
        // Authentication state is "Unauthenticated"
        _authState.value = AuthState.UnAuthenticated

    }

    // Method for sending the verification email to user's email
    // Executed when a user creates an account for the first time and email is not verified
    fun sendVerificationEmail() {

        //sends a verification email to user
        auth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                // if task is successful, log result and sign out user
                if (task.isSuccessful) {

                    Log.i(
                        "Email Verification",
                        "Verification Email sent to " + auth.currentUser?.email
                    )
                    signout()
                }
                // otherwise, log it and try again
                else {
                    Log.w("Email Verification", task.exception)
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Something went wrong")

                }
            }
    }

    // Changes the user's password through Firebase Authentication
    // Requies a re-authentication before doing it
    fun changePassword(password: String) {
        val user = auth.currentUser

        user!!.updatePassword(password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Password change", "User password updated successfully.")
            } else {
                Log.e(
                    "Password change",
                    task.exception?.message ?: "User password failed to update"
                )
                _authState.value =
                    AuthState.Error(task.exception?.message ?: "User password update failed.")
            }
        }
    }

    fun changeUserDetails(firstName: String, lastName: String) {

        val user = auth.currentUser
        db.collection("accounts").document(user?.uid ?: "N/A").update(
            mapOf(
                "firstName" to firstName,
                "lastName" to lastName
            )
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("User Details change", "User Details Updated")
            } else {
                Log.e("User Details change", task.exception?.message ?: "Something went wrong")
            }
        }
    }

    // Reauthentication function, returns true if successful, otherwise false
    suspend fun reauthenticate(email: String, password: String): Boolean {

        val user = auth.currentUser!!
        val credential = EmailAuthProvider.getCredential(email, password)
        var isSuccess: Boolean = false;

        try {
                user.reauthenticate(credential).await()
                Log.d("User re-authentication", "User re-authenticated.")
                isSuccess = true;
            } catch(e: Exception) {
                Log.e("User re-authentication", e.message?: "Re-authentication failed")
            }
        Log.d("Reauthentication flag value:" , isSuccess.toString())
        return isSuccess;

    }

    // Delete account function, requires reauthentication to proceed
    // Delete user data in Firestore first, if successful, then deletes the auth instance
    //TODO: Must delete auth instance firt before Firestore Data since latter data lingers
    fun deleteAccount() {
        val user = auth.currentUser!!
        val uid = user.uid.toString()

        _authState.value = AuthState.Loading

        (CoroutineScope(Dispatchers.Main).launch{
                val firestoreDeleteStatus = withContext(IO) {deleteAccountInFirestore(uid)}
                if (firestoreDeleteStatus) {
                    (CoroutineScope(Dispatchers.Main).launch{(
                    user.delete().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("Account Deletion", "Authentication instance successfully deleted")
                        } else {
                            Log.e(
                                "Account Deletion",
                                task.exception?.message ?: "Authentication instance failed to delete"
                            )
                            _authState.value = AuthState.Error(
                                task.exception?.message ?: "Authentication instance failed to delete"
                            )
                        }
                    }).await()
                    })
                }
                signout()
                checkAuthStatus()
            })
        }




    // Helper function for delete function
    // Deletes user data from Firestore
    // Does not delete data created by the user, only deletes the account
    suspend private fun deleteAccountInFirestore(uid: String?): Boolean {

        if (uid == null) return false;

        var flag: Boolean = false;

        db.collection("accounts").document(uid).delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Account Deletion", "User data in Firestore successfully deleted")
                flag = true
            } else {
                Log.e(
                    "Account Deletion",
                    task.exception?.message ?: "User data in Firestore failed to be deleted."
                )
            }
        }.await()

        return flag;
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

        // Unverified State
        // Occurs when login attempt is created but email verification not completed
        object Unverified : AuthState()

        // Error state
        data class Error(val message: String) : AuthState()
    }
