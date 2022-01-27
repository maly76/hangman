package thm.ap.hangman.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import thm.ap.hangman.R
import thm.ap.hangman.databinding.FragmentResultBinding
import thm.ap.hangman.models.Player
import thm.ap.hangman.models.Statistic
import thm.ap.hangman.persistence.CompetitionDAO
import thm.ap.hangman.persistence.PlayerDAO
import thm.ap.hangman.service.AchievementService
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
    private lateinit var playerDAO: PlayerDAO

    private var competitionDAO = CompetitionDAO(this)

    private lateinit var gameResult: PlayingField.GameResult
    private lateinit var achievementService: AchievementService

    private var won: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        achievementService = AchievementService.of(requireActivity())
        playerDAO = PlayerDAO()

        if (arguments != null) {
            gameResult = requireArguments().get("GameResult") as PlayingField.GameResult
            isMultiplayer = gameResult.roomId != null
            updateStats(gameResult)
            if (isMultiplayer) {
                var opponentUsername = ""

                competitionDAO.subscribeCompetition(gameResult.roomId!!).observe(viewLifecycleOwner) { result ->
                    if (result.status == thm.ap.hangman.models.Result.Status.SUCCESS) {
                        result.data.let { comp ->
                            if (AuthenticationService.getCurrentUser()!!.uid == comp!!.host.id) {
                                opponentUsername = comp.guest!!.userName!!
                                /* Host */
                                if (comp.guestInfos.status == Player.Status.PLAYING) {
                                    binding.result.text = "Waiting for $opponentUsername to finish"
                                } else {
                                    when {
                                        comp.hostInfos.tries > comp.guestInfos.tries -> {
                                            binding.result.text = "You Lost against $opponentUsername!"
                                            won = 2
                                        }
                                        comp.hostInfos.tries < comp.guestInfos.tries -> {
                                            binding.result.text = "You Won against $opponentUsername!"
                                            won = 1
                                        }
                                        comp.hostInfos.tries == comp.guestInfos.tries -> {
                                            binding.result.text = "The game with $opponentUsername is tied!"
                                            won = 3
                                        }
                                    }
                                }
                            } else {
                                opponentUsername = comp.host!!.userName!!
                                if (comp.guestInfos.status == Player.Status.PLAYING) {
                                    binding.result.text = "Waiting for $opponentUsername to finish"
                                } else {
                                    when {
                                        comp.guestInfos.tries > comp.hostInfos.tries -> {
                                            binding.result.text = "You Lost against $opponentUsername!"
                                            won = 2
                                        }
                                        comp.guestInfos.tries < comp.hostInfos.tries -> {
                                            binding.result.text = "You Won against $opponentUsername!"
                                            won = 1
                                        }
                                        comp.guestInfos.tries == comp.hostInfos.tries -> {
                                            binding.result.text = "The game with $opponentUsername is tied!"
                                            won = 3
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                binding.result.visibility = View.GONE
            }

            if (gameResult.success) {
                binding.successful.text = "You guessed the word successfully!"
            } else {
                binding.successful.text = "You did not guess the word!"
            }

            binding.tries.text = "You took ${gameResult.tries + 1} out of 12 tries"

            binding.guessWord.text = "The word was: ${gameResult.word}"
        }

        val view = binding.root
        return view
    }

    fun checkAchievements(gameResult: PlayingField.GameResult, player: Player) {
        isMultiplayer = true
        /** Win your first game */
        if ((player.statistic!!.spStats.getGeneralRate().wins == 1 && player.statistic!!.mpStats.getGeneralRate().wins == 0) ||
            (player.statistic!!.mpStats.getGeneralRate().wins == 1 && player.statistic!!.spStats.getGeneralRate().wins == 0)) {
            achievementService.unlockAchievement(getString(R.string.win_a_game))
        }

        /** Win Multi-Player games */
        val numberOfWonMultGames = player.statistic!!.mpStats.getGeneralRate().wins
        if (numberOfWonMultGames != 0 && numberOfWonMultGames % 5 == 0) {
            if (numberOfWonMultGames == 5) {
                achievementService.unlockAchievement(getString(R.string.win_mult_player_game))
            } else {
                achievementService.incrementAchievement(getString(R.string.win_mult_player_game), 5)
            }
        }

        /** Win Single-Player games */
        val numberOfWonSingleGames = player.statistic!!.spStats.getGeneralRate().wins
        if (numberOfWonSingleGames != 0 && numberOfWonSingleGames % 5 == 0) {
            if (numberOfWonSingleGames == 5) {
                achievementService.unlockAchievement(getString(R.string.win_single_player_game))
            } else {
                achievementService.incrementAchievement(getString(R.string.win_single_player_game), 5)
            }
        }

        /** win a game without drawing a line in single player */
        if (!isMultiplayer) {
            if (gameResult.status == PlayingField.GameResult.Status.WON && gameResult.tries == 0) {
                achievementService.unlockAchievement(getString(R.string.win_game_without_lines_single))
            }
        }

        /** win a game without drawing a line in multi player */
        if (isMultiplayer) {
            if (gameResult.status == PlayingField.GameResult.Status.WON && gameResult.tries == 0) {
                achievementService.unlockAchievement(getString(R.string.win_game_without_lines_multi))
            }
        }

        /** Guess a word with the length of at least 10 characters */
        if (gameResult.status == PlayingField.GameResult.Status.WON && gameResult.word.length >= 10) {
            achievementService.unlockAchievement(getString(R.string.win_game_with_10_characters))
        }
    }

    private fun updateStats(gameResult: PlayingField.GameResult) {
        playerDAO.getPlayerByID(AuthenticationService.getCurrentUser()!!.uid)
            .observe(viewLifecycleOwner) { result ->
                if (result.status == thm.ap.hangman.models.Result.Status.SUCCESS) {
                    val player = result.data!!
                    updateObject(
                        if (isMultiplayer) player.statistic!!.mpStats else player.statistic!!.spStats,
                        gameResult
                    )
                    playerDAO.updatePlayer(player).observe(viewLifecycleOwner) { result ->
                        if (result.status == thm.ap.hangman.models.Result.Status.SUCCESS) {
                            checkAchievements(gameResult, result.data!!)
                        }
                    }
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
        when (won) {
            1 -> {
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
            2 -> {
                rate.losses++
                stats.winStreakActive = false
            }
            3 -> {
                rate.ties++
                stats.winStreakActive = false
            }
            else -> {
                // won is null
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

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
                    builder.setCancelable(false)
                    builder.setMessage("Do you want to go back to the main menu?")
                    builder.setPositiveButton(
                        "Yes"
                    ) { _, _ -> //if user pressed "yes", then he is allowed to exit from application
                        if (isMultiplayer) {
                            competitionDAO.exitRoom(gameResult.roomId!!)
                        }
                        val action = ChooseWordDirections.actionChooseWordToMultiPlayer()
                        navController.navigate(action)
                    }
                    builder.setNegativeButton(
                        "No"
                    ) { dialog, _ -> //if user select "No", just cancel this dialog and continue with app
                        dialog.cancel()
                    }
                    val alert: AlertDialog = builder.create()
                    alert.show()
                }
            })

        binding.buttonMainMenu.setOnClickListener {
            if (isMultiplayer) {
                competitionDAO.exitRoom(gameResult.roomId!!)
            }
            val action = ResultDirections.actionResultToMainMenu()
            navController.navigate(action)
        }

        binding.buttonPlayAgain.setOnClickListener {
            if (isMultiplayer) {
                competitionDAO.getCompetitionByID(gameResult.roomId!!)
                    .observe(viewLifecycleOwner) { comp ->
                        if (comp.status == thm.ap.hangman.models.Result.Status.SUCCESS) {
                            comp.data.let {
                                if (it!!.guestInfos.status == Player.Status.AGAIN && it.hostInfos.status == Player.Status.AGAIN) {
                                    val action =
                                        ResultDirections.actionResultToChooseWord(gameResult.roomId!!)
                                    navController.navigate(action)
                                }
                                if (AuthenticationService.getCurrentUser()!!.uid == it.host.id) {
                                    /* Host */
                                    it.guestInfos.wortToGuess = null
                                    it.guestInfos.hiddenWord = null
                                    it.hostInfos.status = Player.Status.AGAIN
                                } else {
                                    /* Guest */
                                    it.hostInfos.wortToGuess = null
                                    it.hostInfos.hiddenWord = null
                                    it.guestInfos.status = Player.Status.AGAIN
                                }
                            }
                        }
                    }
            } else {
                val action = ResultDirections.actionResultToMainMenu()
                navController.navigate(action)
            }
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