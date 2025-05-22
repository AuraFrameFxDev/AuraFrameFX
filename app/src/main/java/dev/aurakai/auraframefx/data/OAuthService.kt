package dev.aurakai.auraframefx.data

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class OAuthService(private val context: Context) {
    private val gso: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.google_oauth_client_id))
            .requestEmail()
            .build()
    }

    private val googleSignInClient: GoogleSignInClient by lazy {
        GoogleSignIn.getClient(context, gso)
    }

    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    fun handleSignInResult(task: Task<GoogleSignInAccount>): String? {
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { token ->
                securePrefs.saveOAuthToken(token)
                return token
            }
            return null
        } catch (e: ApiException) {
            e.printStackTrace()
            return null
        }
    }

    fun signOut() {
        googleSignInClient.signOut()
            .addOnCompleteListener {
                securePrefs.clearOAuthToken()
            }
    }

    fun revokeAccess() {
        googleSignInClient.revokeAccess()
            .addOnCompleteListener {
                securePrefs.clearOAuthToken()
            }
    }

    companion object {
        const val RC_SIGN_IN = 9001
    }
}
