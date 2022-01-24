package thm.ap.hangman

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PlayGamesAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import thm.ap.hangman.models.Result
import thm.ap.hangman.persistence.CategoryDAO
import thm.ap.hangman.persistence.CompetitionDAO
import thm.ap.hangman.persistence.PlayerAuth
import thm.ap.hangman.persistence.PlayerDAO

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private val RC_SIGN_IN = 123
    private val TAG = "testing"
    private lateinit var gso: GoogleSignInOptions

    private val playerAuth = PlayerAuth(this)

    private val playerDAO = PlayerDAO()
    private val categoryDAO = CategoryDAO()
    private val competitionDAO = CompetitionDAO(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
            .requestServerAuthCode(getString(R.string.default_web_client_id))
            .requestIdToken(getString(R.string.default_web_client_id))
            .build()

        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        loginFlow(currentUser)
    }

    private fun loginFlow(currentUser: FirebaseUser?) {
        if (currentUser == null) {
            signInSilently()
        } else {
            val user = auth.currentUser
            user?.let {
                Log.e(TAG,user.uid + " " + user.displayName)
                playerAuth.createPlayer(user)

                playerDAO.getPlayersObserver().observe(this, { result ->
                    when (result.status) {
                        Result.Status.IN_PROGRESS -> Log.i(TAG, "the request in progress")
                        Result.Status.SUCCESS -> {
                            Log.i(TAG, result.data!!.toString())
                        }
                        Result.Status.FAILURE -> Log.i(TAG, result.error!!)
                    }
                })

                competitionDAO.subscribeCompetition("234").observe(this, { result ->
                    when (result.status) {
                        Result.Status.IN_PROGRESS -> Log.i(TAG, "the request in progress")
                        Result.Status.SUCCESS -> {
                            Log.i(TAG, result.data!!.toString())
                        }
                        Result.Status.FAILURE -> Log.i(TAG, result.error!!)
                    }
                })

            }

        }

    }

    private fun signInSilently() {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (GoogleSignIn.hasPermissions(account, *gso.scopeArray)) {
            // Already signed in.
            // The signed in account is stored in the 'account' variable.
            val signedInAccount = account
            firebaseAuthWithPlayGames(signedInAccount!!)
        } else {
            // Haven't been signed-in before. Try the silent sign-in first.
            val signInClient = GoogleSignIn.getClient(this, gso)
            signInClient
                .silentSignIn()
                .addOnCompleteListener(
                    this
                ) { task ->
                    if (task.isSuccessful) {
                        // The signed in account is stored in the task's result.
                        val signedInAccount = task.result
                        firebaseAuthWithPlayGames(signedInAccount!!)
                    } else {
                        startSignInIntent()
                    }
                }
        }
    }

    private fun startSignInIntent() {
        val signInClient = GoogleSignIn.getClient(this, gso)
        val intent = signInClient.signInIntent
        startActivityForResult(intent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result: GoogleSignInResult =
                Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)!!
            if (result.isSuccess) {
                // The signed in account is stored in the result.
                val signedInAccount = result.signInAccount
                firebaseAuthWithPlayGames(signedInAccount!!)
            } else {
                Toast.makeText(
                    baseContext, "Play Games authentication failed.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun firebaseAuthWithPlayGames(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithPlayGames:" + acct.id)

        val auth = Firebase.auth
        val credential = PlayGamesAuthProvider.getCredential(acct.serverAuthCode!!)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    loginFlow(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    loginFlow(null)
                }

                // ...
            }
    }
}