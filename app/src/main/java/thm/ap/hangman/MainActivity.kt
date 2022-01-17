package thm.ap.hangman

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import thm.ap.hangman.models.Category
import thm.ap.hangman.models.Competition
import thm.ap.hangman.models.Player
import thm.ap.hangman.models.Statistic
import thm.ap.hangman.persistence.*
import java.util.*

class MainActivity : AppCompatActivity() {
    val auth = Auth()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (auth.authenticate()) {
            Toast.makeText(this, "You are logged in", Toast.LENGTH_SHORT).show()
        } else {
            // TODO: sign in OR sign up
            throw NotImplementedError();
        }

        setContentView(R.layout.activity_main)
    }
}