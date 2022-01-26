package thm.ap.hangman.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import thm.ap.hangman.databinding.FragmentProfileBinding
import thm.ap.hangman.models.Statistic
import thm.ap.hangman.persistence.PlayerDAO

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class Profile : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var playerDAO = PlayerDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        var player = playerDAO.getPlayerByID(Firebase.auth.currentUser!!.uid)

        player.observe(this, {
            if (it.data != null) {
                binding.name.text = it.data.userName

                val spStats: Statistic.Stats? = it.data.statistic?.spStats
                if (spStats != null) {
                    binding.played.text = "Games played: ${spStats.getGeneralRate().getNumebrOfPlayedGames()}"
                    binding.won.text = "Games Won: ${spStats.getGeneralRate().wins}"
                    binding.lost.text = "Games Lost: ${spStats.getGeneralRate().losses}"
                    binding.ratio.text = "Win ratio: ${spStats.getGeneralRate().getWinLosRate()}%"
                    binding.longest.text = "Longest word: ${spStats.longestWord}"
                    binding.streak.text = "Longest winstreak: ${spStats.winstreak}"
                }

                val mpStats: Statistic.Stats? = it.data.statistic?.mpStats
                if (mpStats != null) {
                    binding.playedMp.text = "Games played: ${mpStats.getGeneralRate().getNumebrOfPlayedGames()}"
                    binding.wonMp.text = "Games Won: ${mpStats.getGeneralRate().wins}"
                    binding.lostMp.text = "Games Lost: ${mpStats.getGeneralRate().losses}"
                    binding.ratioMp.text = "Win ratio: ${mpStats.getGeneralRate().getWinLosRate()}%"
                    binding.longestMp.text = "Longest word: ${mpStats.longestWord}"
                    binding.streakMp.text = "Longest winstreak: ${mpStats.winstreak}"
                }
            }
        })
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Profile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Profile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}