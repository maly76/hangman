package thm.ap.hangman.persistence

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import thm.ap.hangman.models.Player
import thm.ap.hangman.models.Statistic

class Auth {
    private val playerDAO = PlayerDAO()

    fun authenticate (): Boolean {
        val user = Firebase.auth.currentUser
        return if (user != null) {
            Log.e("TEST", "Angemeldet")
            createPlayer("Mohammad")
            true
        } else {
            /* TODO: Oberfläche öffnen: anmelden, registrieren, als Gast fortfahren */
            false
        }
    }

    fun signIn(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i("UID", task.result?.user?.uid ?: "No User id")
            }
            task.exception?.message?.let {Log.e("UID", it)}
        }
    }

    fun signIn() {
        Firebase.auth.signInAnonymously()
        createPlayer("Anonymous")
    }

    fun signUp(email: String, password: String) {
        val user = Firebase.auth.currentUser
        if (user != null && user.isAnonymous) {
            val credential = EmailAuthProvider.getCredential(email, password)
            Firebase.auth.currentUser!!.linkWithCredential(credential).addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e(TAG, "Authentication failed")
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

    fun createPlayer(userName: String) {
        val user = Firebase.auth.currentUser
        if (user != null) {
            val player = Player("")
            player.id = user.uid
            player.statistic = Statistic()
            player.userName = userName
            playerDAO.addPlayer(player)
        }
    }
}