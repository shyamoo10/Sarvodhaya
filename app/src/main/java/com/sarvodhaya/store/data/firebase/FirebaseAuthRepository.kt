package com.sarvodhaya.store.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.sarvodhaya.store.domain.model.Address
import com.sarvodhaya.store.domain.model.UserProfile
import com.sarvodhaya.store.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseAuthRepository(
    private val auth: FirebaseAuth?,
    private val db: FirebaseFirestore?
) : AuthRepository {

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    override val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()
    override val isConfigured: Boolean = auth != null

    init {
        auth?.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                _currentUser.value = null
            } else {
                if (_currentUser.value?.uid != user.uid) {
                    _currentUser.value = UserProfile(
                        uid = user.uid,
                        name = user.displayName.orEmpty(),
                        email = user.email.orEmpty()
                    )
                }
                syncProfile(user)
            }
        }
    }

    private fun syncProfile(user: FirebaseUser) {
        val store = db ?: return
        store.collection(FirestorePaths.USERS).document(user.uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    _currentUser.value = document.toProfile(user)
                } else {
                    val fallback = _currentUser.value ?: UserProfile(
                        user.uid,
                        user.displayName.orEmpty(),
                        user.email.orEmpty()
                    )
                    store.collection(FirestorePaths.USERS).document(user.uid).set(fallback.toMap())
                }
            }
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> = withContext(Dispatchers.IO) {
        val firebaseAuth = auth ?: return@withContext failure(
            "Firebase is not configured. Add app/google-services.json to enable accounts."
        )
        if (email.isBlank() || password.isBlank()) {
            return@withContext failure("Enter your email and password")
        }
        try {
            firebaseAuth.signInWithEmailAndPassword(email.trim(), password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e.toFriendlyError())
        }
    }

    override suspend fun register(name: String, email: String, password: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            val firebaseAuth = auth ?: return@withContext failure(
                "Firebase is not configured. Add app/google-services.json to enable accounts."
            )
            if (name.isBlank() || email.isBlank() || password.length < 6) {
                return@withContext failure("Enter your name, email and a password of 6+ characters")
            }
            try {
                val result = firebaseAuth.createUserWithEmailAndPassword(email.trim(), password).await()
                val firebaseUser = result.user ?: return@withContext failure("Could not create your account")
                firebaseUser.updateProfile(
                    UserProfileChangeRequest.Builder().setDisplayName(name.trim()).build()
                ).await()
                val profile = UserProfile(firebaseUser.uid, name.trim(), email.trim())
                _currentUser.value = profile
                db?.collection(FirestorePaths.USERS)?.document(firebaseUser.uid)?.set(profile.toMap())?.await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e.toFriendlyError())
            }
        }

    override suspend fun signOut() {
        auth?.signOut()
        _currentUser.value = null
    }

    override suspend fun saveProfile(name: String, phone: String, address: Address): Result<Unit> =
        withContext(Dispatchers.IO) {
            val current = _currentUser.value ?: return@withContext failure("Sign in to save your profile")
            val updated = current.copy(name = name.trim(), phone = phone.trim(), address = address)
            try {
                db?.collection(FirestorePaths.USERS)?.document(current.uid)?.set(updated.toMap())?.await()
                _currentUser.value = updated
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(IllegalStateException(e.message ?: "Could not save your profile"))
            }
        }

    private fun failure(message: String): Result<Unit> = Result.failure(IllegalStateException(message))
}

private fun UserProfile.toMap(): Map<String, Any> = mapOf(
    "name" to name,
    "email" to email,
    "phone" to phone,
    "address" to mapOf(
        "fullName" to address.fullName,
        "phone" to address.phone,
        "line1" to address.line1,
        "city" to address.city,
        "state" to address.state,
        "pincode" to address.pincode
    )
)

private fun DocumentSnapshot.toProfile(user: FirebaseUser): UserProfile {
    @Suppress("UNCHECKED_CAST")
    val map = get("address") as? Map<String, Any?>
    val address = Address(
        fullName = map?.get("fullName")?.toString().orEmpty(),
        phone = map?.get("phone")?.toString().orEmpty(),
        line1 = map?.get("line1")?.toString().orEmpty(),
        city = map?.get("city")?.toString().orEmpty(),
        state = map?.get("state")?.toString().orEmpty(),
        pincode = map?.get("pincode")?.toString().orEmpty()
    )
    return UserProfile(
        uid = user.uid,
        name = getString("name") ?: user.displayName.orEmpty(),
        email = getString("email") ?: user.email.orEmpty(),
        phone = getString("phone").orEmpty(),
        address = address
    )
}

private fun Throwable.toFriendlyError(): Throwable = when (this) {
    is FirebaseAuthWeakPasswordException -> IllegalStateException("Password must be at least 6 characters")
    is FirebaseAuthUserCollisionException -> IllegalStateException("An account with this email already exists")
    is FirebaseAuthInvalidUserException -> IllegalStateException("No account found for this email")
    is FirebaseAuthInvalidCredentialsException -> IllegalStateException("Incorrect email or password")
    else -> IllegalStateException(message ?: "Something went wrong. Please try again.")
}
