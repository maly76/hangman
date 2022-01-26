package thm.ap.hangman.service

import android.content.Context
import android.util.Log
import android.widget.Toast
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

class AuthenticationService(val context: Context) {
    private var auth: FirebaseAuth = Firebase.auth
    var gso: GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
            .requestServerAuthCode(context.getString(R.string.default_web_client_id))
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .build()

    val TAG = "test"

    fun authenticate() {
        val currentUser = Firebase.auth.currentUser
        if (currentUser == null) {
            signInSilently()
        } else {
            currentUser.let {
                Log.e("test", currentUser!!.uid + " " + currentUser!!.displayName)
            }
        }
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
                        //main.signinintent
                        var mContext = context as MainActivity
                        mContext.startSignInIntent()
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
                        Log.e(TAG, user.uid + " " + user.displayName)
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    (context as MainActivity).finish()
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
}