package thm.ap.hangman.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import thm.ap.hangman.databinding.FragmentResultBinding

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
        _binding = FragmentResultBinding.inflate(inflater, container, false)

        if (arguments != null) {
            val gameResult = arguments!!.get("GameResult") as PlayingField.GameResult

            if (gameResult.status == PlayingField.GameResult.Status.WON) {
                binding.result.text = "You Won!"
            }
            if (gameResult.status == PlayingField.GameResult.Status.LOST) {
                binding.result.text = "You Lost!"
            }
            if (gameResult.status == PlayingField.GameResult.Status.TIE) {
                binding.result.text = "The game is tied!"
            }

            binding.tries.text = "You took ${gameResult.tries} out of 11 tries"

            binding.guessWord.text = "The word was: ${gameResult.word}"
        }

        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

        binding.buttonMainMenu.setOnClickListener {
            val action = ResultDirections.actionResultToMainMenu()
            navController.navigate(action)
        }

        binding.buttonPlayAgain.setOnClickListener {
            val action = ResultDirections.actionResultToChooseWord()
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