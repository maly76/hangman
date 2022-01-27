package thm.ap.hangman.service

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.Games
import com.google.android.gms.tasks.Task

/**
 * AchievementService
 * managing the achievements from in google plays
 * */
class AchievementService private constructor(val context: FragmentActivity) {

    /**
     * call the achievements of a player
     * @return intent for be showing in the activity
     * */
    fun showAchievements(): Task<Intent> {
        return Games.getAchievementsClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .achievementsIntent
    }

    /**
     * unlocks an achievement for the account which is logged in
     * @param achievementID the id of the achievement which should be unlocked
     * */
    fun unlockAchievement(achievementID: String) {
        Games.getAchievementsClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .unlock(achievementID)
    }

    /**
     * increment the score of an achievement
     * @param achievementID the id of the achievement
     * @param incrementBy the score to increment the achievement by it
     * */
    fun incrementAchievement(achievementID: String, incrementBy: Int) {
        Games.getAchievementsClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .increment(achievementID, incrementBy);
    }

    companion object {
        const val RC_ACHIEVEMENT_UI = 9003

        fun of(context: FragmentActivity): AchievementService {
            return AchievementService(context)
        }
    }
}