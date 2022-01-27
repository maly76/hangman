package thm.ap.hangman

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import thm.ap.hangman.persistence.CategoryDAO
import thm.ap.hangman.persistence.CompetitionDAO
import thm.ap.hangman.persistence.PlayerDAO
import thm.ap.hangman.service.AchievementService
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

        if (checkConnectivity()) {
            authService.authenticate()
        } else {
            AlertDialog.Builder(this)
                .setTitle(R.string.no_internet)
                .setMessage(R.string.no_internet_msg)
                .setPositiveButton(R.string.exit) { _,_ ->
                    finish()
                }
                .show()
        }
    }

    private fun checkConnectivity(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = cm.activeNetwork ?: return false
        val activeNetwork = cm.getNetworkCapabilities(networkCapabilities) ?: return false
        return when{
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)     -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else                                                               -> false
        }
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
                finish()
            }
        }
    }
}