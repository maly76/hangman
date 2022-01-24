package thm.ap.hangman.persistence

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import thm.ap.hangman.models.Player
import thm.ap.hangman.models.Statistic

class PlayerAuth() {


    fun signout() {
        Firebase.auth.signOut()
    }

    /*fun show(resId: Int) {
        Toast.makeText(
            context, resId,
            Toast.LENGTH_SHORT
        ).show()
    }

    fun getString(resId: Int): String {
        return context.getString(resId)
    }*/

    fun createPlayer(user: FirebaseUser) {
        val playerDAO = PlayerDAO()
        val player = Player.new(user.uid, user.displayName, Statistic())
        playerDAO.addPlayer(player)
    }

}