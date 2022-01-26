package thm.ap.hangman.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import thm.ap.hangman.databinding.FragmentMultiPlayerBinding
import thm.ap.hangman.models.Competition
import thm.ap.hangman.models.Result
import thm.ap.hangman.persistence.CompetitionDAO
import thm.ap.hangman.persistence.PlayerDAO

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MultiPlayer.newInstance] factory method to
 * create an instance of this fragment.
 */
class MultiPlayer : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentMultiPlayerBinding? = null
    private val binding get() = _binding!!

    private val playerDAO = PlayerDAO()
    private val competitionDAO = CompetitionDAO(this)

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
    ): View? {
        _binding = FragmentMultiPlayerBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

        binding.buttonEnterRoom.setOnClickListener {
            val action = MultiPlayerDirections.actionMultiPlayerToChooseWord()
            navController.navigate(action)
        }

        binding.buttonCreateRoom.setOnClickListener {
            Log.e("testing", binding.roomCode.text.toString())
            //TODO check failure room exists
            playerDAO.getPlayerByID(Firebase.auth.currentUser!!.uid)
                .observe(viewLifecycleOwner) { result ->
                    if (result.status == Result.Status.SUCCESS) {
                        competitionDAO.addCompetition(
                            Competition(
                                binding.roomCode.text.toString(),
                                result.data!!
                            )
                        ).observe(viewLifecycleOwner) {
                            if (it.status == Result.Status.SUCCESS) {
                                Log.e("TAG", it.data!!.toString())
                            }
                        }
                    }
                }

            val action = MultiPlayerDirections.actionMultiPlayerToChooseWord()
            navController.navigate(action)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MultiPlayer.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MultiPlayer().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}