package thm.ap.hangman.persistence

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import thm.ap.hangman.models.Player
import thm.ap.hangman.models.Statistic

class Auth {
    private val playerDAO = PlayerDAO()

    fun authenticate (): Boolean {
        return Firebase.auth.currentUser != null
    }

    fun signIn(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i(TAG, task.result?.user?.uid ?: "No User id")
            }
            task.exception?.message?.let {Log.e("UID", it)}
        }
    }

    fun signIn() {
        Firebase.auth.signInAnonymously().addOnCompleteListener {task ->
            if (task.isSuccessful) {
                createPlayer("Anonymous")
            } else {
                Log.e(TAG, "Sign up anonymously failed")
            }
        }
    }

    fun signUp(email: String, password: String) {
        val auth = Firebase.auth
        if (auth.currentUser != null && auth.currentUser!!.isAnonymous) {
            val credential = EmailAuthProvider.getCredential(email, password)
            auth.currentUser!!.linkWithCredential(credential).addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e(TAG, "Register failed")
                }
            }
        } else {
            Firebase.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.i("UID", it.result?.user?.uid ?: "No User id")
                }
                it.exception?.message?.let {Log.e("UID", it)}
            }
        }
    }

    private fun createPlayer(userName: String) {
        val user = Firebase.auth.currentUser
        if (user != null) {
            val player = Player()
            player.id = user.uid
            player.statistic = Statistic()
            player.userName = userName
            playerDAO.addPlayer(player)
        }
    }
}