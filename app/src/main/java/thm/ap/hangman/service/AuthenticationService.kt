package thm.ap.hangman.service

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PlayGamesAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import thm.ap.hangman.MainActivity
import thm.ap.hangman.R
import thm.ap.hangman.models.Player
import thm.ap.hangman.models.Statistic
import thm.ap.hangman.persistence.PlayerDAO
import java.lang.Exception

class AuthenticationService (val context: Context) {
    private var auth: FirebaseAuth = Firebase.auth
    var gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
        .requestServerAuthCode(context.getString(R.string.default_web_client_id))
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .build()
    private val authObserver = MutableLiveData<Result>()

    fun tryAuthenticating(): MutableLiveData<Result> {

        val currentUser = Firebase.auth.currentUser
        if (currentUser == null) {
            signInSilently()
        } else {
            currentUser.let {
                Log.e(TAG, currentUser.uid + " " + currentUser.displayName)
            }
            authObserver.value = Result.success(currentUser)
        }
        return authObserver
    }

    private fun signInSilently() {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        if (GoogleSignIn.hasPermissions(account, *gso.scopeArray)) {
            // Already signed in.
            // The signed in account is stored in the 'account' variable.
            firebaseAuthWithPlayGames(account!!)
        } else {
            // Haven't been signed-in before. Try the silent sign-in first.
            val signInClient = GoogleSignIn.getClient(context, gso)
            signInClient
                .silentSignIn()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // The signed in account is stored in the task's result.
                        val signedInAccount = task.result
                        firebaseAuthWithPlayGames(signedInAccount!!)
                    } else {
                        authObserver.value = Result.shouldSignIn()
                    }
                }
        }
    }

    fun firebaseAuthWithPlayGames(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithPlayGames:" + acct.id)

        val auth = Firebase.auth
        val credential = PlayGamesAuthProvider.getCredential(acct.serverAuthCode!!)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = Firebase.auth.currentUser
                    user?.let {
                        Log.e(AuthenticationService.TAG, user.uid + " " + user.displayName)
                    }
                    authObserver.value = Result.success(auth.currentUser!!)
                } else {
                    // If sign in fails, display a message to the user.
                    authObserver.value = Result.failure(task.exception!!)
                }
            }
    }

    fun signout() {
        Firebase.auth.signOut()
    }

    private fun show(resId: Int) {
        Toast.makeText(
            context, resId,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun getString(resId: Int): String {
        return context.getString(resId)
    }

    private fun createPlayer(user: FirebaseUser) {
        val playerDAO = PlayerDAO()
        val player = Player.new(user.uid, user.displayName, Statistic())
        playerDAO.addPlayer(player)
    }

    companion object {
        const val TAG = "testing"

        fun of(context: Context): AuthenticationService {
            return AuthenticationService(context)
        }
    }

    enum class Status {
        SUCCESS,
        SIGN_IN,
        FAILURE
    }

    class Result(val status: Status, val user: FirebaseUser? = null, val exception: Exception? = null) {
        companion object {
            fun success(user: FirebaseUser) = Result(Status.SUCCESS, user)
            fun shouldSignIn() = Result(Status.SIGN_IN)
            //fun finishSignIn(user: FirebaseUser) = Result(Status.FINISH_SIGN_IN, user)
            fun failure(exception: Exception) = Result(Status.FAILURE, exception = exception)
        }
    }
}