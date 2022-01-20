package thm.ap.hangman

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import thm.ap.hangman.databinding.PlayingFieldBinding
import thm.ap.hangman.gamelogic.GameLogic

class PlayingFieldActivity : AppCompatActivity() {

    private lateinit var binding: PlayingFieldBinding
//    private val viewModel: PlayingFieldViewModel by viewModels()
    private val gameLogic = GameLogic()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = PlayingFieldBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindButtons()
        binding.guessButton.setOnClickListener { guessWord() }
    }

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()
        updateHiddenWord()
        //TODO: write wordToGuess to DB
    }

    @SuppressLint("SetTextI18n")
    private fun onButtonPressed(button: Button) {
        button.isClickable = false
        if (gameLogic.checkLetter(button.text[0])) {
            if (gameLogic.checkIfWon()) { gameWon() }
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
//        binding.hangmanPlaceholder.text = "You Won!" //TODO go to results
    }

    private fun gameLost() {
//        binding.hangmanPlaceholder.text = "You Lose!" //TODO go to results
    }

    private fun bindButtons() {
        binding.letterA.setOnClickListener { onButtonPressed(binding.letterA)}
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

}
