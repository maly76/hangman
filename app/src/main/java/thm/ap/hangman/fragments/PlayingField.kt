package thm.ap.hangman.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import thm.ap.hangman.R
import thm.ap.hangman.databinding.FragmentPlayingFieldBinding
import thm.ap.hangman.gamelogic.GameLogic

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PlayingField.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlayingField : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentPlayingFieldBinding? = null
    private val binding get() = _binding!!

    private val gameLogic = GameLogic()

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
        _binding = FragmentPlayingFieldBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()
        updateHiddenWord()
        //TODO: write wordToGuess to DB
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    @SuppressLint("SetTextI18n")
    private fun onButtonPressed(button: Button) {
        button.isClickable = false
        if (gameLogic.checkLetter(button.text[0])) {
            if (gameLogic.checkIfWon()) {
                gameWon()
            }
            button.setBackgroundColor(Color.parseColor("#4CAF50"))
            updateHiddenWord()
        } else {
            button.setBackgroundColor(Color.parseColor("#F44336"))
            updateTries()
        }
    }

    private fun guessWord() {
        if (gameLogic.guessWord(binding.guessWord.text.toString().trim())) {
            updateHiddenWord()
            gameWon()
        } else {
            updateTries()
        }
    }

    private fun updateHiddenWord() {
        binding.word.text = gameLogic.getHiddenWord()
        //TODO: write hidden Word to DB; gameLogic.getHiddenWord()
    }

    private fun updateTries() {
        when (gameLogic.getTries()) {
            1 -> binding.imageView.setImageResource(R.drawable.hangman_1)
            2 -> binding.imageView.setImageResource(R.drawable.hangman_2)
            3 -> binding.imageView.setImageResource(R.drawable.hangman_3)
            4 -> binding.imageView.setImageResource(R.drawable.hangman_4)
            5 -> binding.imageView.setImageResource(R.drawable.hangman_5)
            6 -> binding.imageView.setImageResource(R.drawable.hangman_6)
            7 -> binding.imageView.setImageResource(R.drawable.hangman_7)
            8 -> binding.imageView.setImageResource(R.drawable.hangman_8)
            9 -> binding.imageView.setImageResource(R.drawable.hangman_9)
            10 -> binding.imageView.setImageResource(R.drawable.hangman_10)
            11 -> binding.imageView.setImageResource(R.drawable.hangman_11)
            else -> binding.imageView.setImageResource(R.drawable.hangman_11)
        }
        //TODO: write tries to DB; gameLogic.getTries()
    }

    private fun gameWon() {
        val navController = findNavController()
        val action = PlayingFieldDirections.actionPlayingFieldToResult()
        navController.navigate(action)
    }

    private fun gameLost() {
        val navController = findNavController()
        val action = PlayingFieldDirections.actionPlayingFieldToResult()
        navController.navigate(action)
    }

    private fun bindButtons() {
        binding.letterA.setOnClickListener { onButtonPressed(binding.letterA) }
        binding.letterB.setOnClickListener { onButtonPressed(binding.letterB) }
        binding.letterC.setOnClickListener { onButtonPressed(binding.letterC) }
        binding.letterD.setOnClickListener { onButtonPressed(binding.letterD) }
        binding.letterE.setOnClickListener { onButtonPressed(binding.letterE) }
        binding.letterF.setOnClickListener { onButtonPressed(binding.letterF) }
        binding.letterG.setOnClickListener { onButtonPressed(binding.letterG) }
        binding.letterI.setOnClickListener { onButtonPressed(binding.letterI) }
        binding.letterH.setOnClickListener { onButtonPressed(binding.letterH) }
        binding.letterJ.setOnClickListener { onButtonPressed(binding.letterJ) }
        binding.letterK.setOnClickListener { onButtonPressed(binding.letterK) }
        binding.letterL.setOnClickListener { onButtonPressed(binding.letterL) }
        binding.letterM.setOnClickListener { onButtonPressed(binding.letterM) }
        binding.letterN.setOnClickListener { onButtonPressed(binding.letterN) }
        binding.letterO.setOnClickListener { onButtonPressed(binding.letterO) }
        binding.letterP.setOnClickListener { onButtonPressed(binding.letterP) }
        binding.letterQ.setOnClickListener { onButtonPressed(binding.letterQ) }
        binding.letterR.setOnClickListener { onButtonPressed(binding.letterR) }
        binding.letterS.setOnClickListener { onButtonPressed(binding.letterS) }
        binding.letterT.setOnClickListener { onButtonPressed(binding.letterT) }
        binding.letterU.setOnClickListener { onButtonPressed(binding.letterU) }
        binding.letterV.setOnClickListener { onButtonPressed(binding.letterV) }
        binding.letterW.setOnClickListener { onButtonPressed(binding.letterW) }
        binding.letterX.setOnClickListener { onButtonPressed(binding.letterX) }
        binding.letterY.setOnClickListener { onButtonPressed(binding.letterY) }
        binding.letterZ.setOnClickListener { onButtonPressed(binding.letterZ) }
        binding.letterAE.setOnClickListener { onButtonPressed(binding.letterAE) }
        binding.letterUE.setOnClickListener { onButtonPressed(binding.letterUE) }
        binding.letterOE.setOnClickListener { onButtonPressed(binding.letterOE) }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PlayingField.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlayingField().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}