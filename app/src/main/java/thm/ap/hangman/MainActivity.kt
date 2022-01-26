package thm.ap.hangman

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import thm.ap.hangman.models.Competition
import thm.ap.hangman.models.Player
import thm.ap.hangman.models.Result
import thm.ap.hangman.persistence.CategoryDAO
import thm.ap.hangman.persistence.CompetitionDAO
import thm.ap.hangman.persistence.PlayerDAO
import thm.ap.hangman.service.AuthenticationService

class MainActivity : AppCompatActivity() {
    private val RC_SIGN_IN = 123

    private lateinit var authService: AuthenticationService

    private val playerDAO = PlayerDAO()
    private val categoryDAO = CategoryDAO()
    private val competitionDAO = CompetitionDAO(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        authService = AuthenticationService(this)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        authService.authenticate()
    }

    fun startSignInIntent() {
        val signInClient = GoogleSignIn.getClient(this, authService.gso)
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
                authService.firebaseAuthWithPlayGames(signedInAccount!!)
            } else {
                Toast.makeText(
                    baseContext, "Play Games authentication failed.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}