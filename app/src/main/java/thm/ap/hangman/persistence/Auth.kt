package thm.ap.hangman.persistence

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.PlayGamesAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import thm.ap.hangman.R
import thm.ap.hangman.models.Player
import thm.ap.hangman.models.Statistic

class Auth(val context: Context) {

    fun authenticate(): Boolean {
        return Firebase.auth.currentUser != null
    }

    fun signIn(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i(TAG, task.result?.user?.uid ?: "No User id")
            }
            task.exception?.message?.let { Log.e("UID", it) }
        }
    }

    fun signIn(acct: GoogleSignInAccount) {
        val auth = Firebase.auth
        val credential = PlayGamesAuthProvider.getCredential(acct.serverAuthCode!!)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.i(TAG, getString(R.string.auth_success))
                    Toast.makeText(
                        context, R.string.auth_success,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.e(TAG, getString(R.string.auth_fail))
                    Toast.makeText(
                        context, R.string.auth_fail,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    fun signout() {
        Firebase.auth.signOut()
    }

    fun signUp(email: String, password: String) {
        val auth = Firebase.auth
        if (auth.currentUser != null && auth.currentUser!!.isAnonymous) {
            val credential = EmailAuthProvider.getCredential(email, password)
            auth.currentUser!!.linkWithCredential(credential).addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    show(R.string.signup_fail)
                }
            }
        } else {
            Firebase.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.i("UID", it.result?.user?.uid ?: "No User id")
                }
                it.exception?.message?.let { Log.e("UID", it) }
            }
        }
    }

    fun signUp() {
        Firebase.auth.signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                createPlayer("Anonymous")
            } else {
                Log.e(TAG, getString(R.string.signup_fail))
                show(R.string.signup_fail)
            }
        }
    }

    fun show(resId: Int) {
        Toast.makeText(
            context, resId,
            Toast.LENGTH_SHORT
        ).show()
    }

    fun getString(resId: Int): String {
        return context.getString(resId)
    }

    private fun createPlayer(userName: String) {
        val playerDAO = PlayerDAO()
        val user = Firebase.auth.currentUser
        if (user != null) {
            val player = Player.new(user.uid, userName, Statistic())
            playerDAO.addPlayer(player)
        }
    }
}