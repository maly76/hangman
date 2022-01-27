package thm.ap.hangman.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import thm.ap.hangman.databinding.FragmentResultBinding
import thm.ap.hangman.models.Statistic
import thm.ap.hangman.persistence.PlayerDAO
import thm.ap.hangman.service.AuthenticationService

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Result.newInstance] factory method to
 * create an instance of this fragment.
 */
class Result : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    private var isMultiplayer = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)

        if (arguments != null) {
            val gameResult = requireArguments().get("GameResult") as PlayingField.GameResult
            updateStats(gameResult)
            if (isMultiplayer) {
                val oponentUsername = "testuser"

                if (gameResult.status == PlayingField.GameResult.Status.WON) {
                    binding.result.text = "You Won against ${oponentUsername}!"
                }
                if (gameResult.status == PlayingField.GameResult.Status.LOST) {
                    binding.result.text = "You Lost against ${oponentUsername}!"
                }
                if (gameResult.status == PlayingField.GameResult.Status.TIE) {
                    binding.result.text = "The game with ${oponentUsername} is tied!"
                }
            } else {
                binding.result.visibility = View.GONE
            }

            if (gameResult.success) {
                binding.successful.text = "You guessed the word successfully!"
            } else {
                binding.successful.text = "You did not guess the word!"
            }

            binding.tries.text = "You took ${gameResult.tries+1} out of 12 tries"

            binding.guessWord.text = "The word was: ${gameResult.word}"
        }

        val view = binding.root
        return view
    }

    private fun updateStats(gameResult: PlayingField.GameResult) {
        val playerDAO = PlayerDAO()
        val isMultiplayer = true
        playerDAO.getPlayerByID(AuthenticationService.getCurrentUser()!!.uid)
            .observe(this) { result ->
                if (result.status == thm.ap.hangman.models.Result.Status.SUCCESS) {
                    val player = result.data!!
                    updateObject(
                        if (isMultiplayer) player.statistic!!.mpStats else player.statistic!!.spStats,
                        gameResult
                    )
                    playerDAO.updatePlayer(player)
                }
            }
    }

    private fun updateObject(
        stats: Statistic.Stats,
        gameResult: PlayingField.GameResult
    ): Statistic.Stats {
        val categoryID = "CSPifMcrWbVK54Oke6EK"         // should be given
        // check if a rate for this category already exists
        val rates = stats.rates.filter { rate -> rate.categoryID == categoryID }
        val rate = if (rates.isEmpty()) Statistic.Rate.new(categoryID) else rates[0]
        when (gameResult.status) {
            PlayingField.GameResult.Status.WON -> {
                rate.wins++
                if (!stats.winStreakActive) {
                    stats.oldWinStreak = 0
                    stats.winStreakActive = true
                }
                stats.oldWinStreak++
                if (stats.oldWinStreak > stats.winstreak) {
                    stats.winstreak = stats.oldWinStreak
                }

                if (gameResult.word.length > stats.longestWord.length) {
                    stats.longestWord = gameResult.word
                }
            }
            PlayingField.GameResult.Status.LOST -> {
                rate.losses++
                stats.winStreakActive = false
            }
            else -> {
                rate.ties++
                stats.winStreakActive = false
            }
        }

        if (rates.isEmpty()) {
            stats.rates.add(rate)
        }
        return stats
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

        binding.buttonMainMenu.setOnClickListener {
            //TODO ifMultiplayer delete Competition after pressing main menu button
            val action = ResultDirections.actionResultToMainMenu()
            navController.navigate(action)
        }

        binding.buttonPlayAgain.setOnClickListener {
            //TODO if Multiplayer, make player ready and delete the old word
//            val action = ResultDirections.actionResultToChooseWord()
//            navController.navigate(action)
            //TODO if Singleplayer, go back to category selection
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Result.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Result().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}