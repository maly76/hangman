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
        binding.hangmanPlaceholder.text = "Try ${gameLogic.getTries()} of ${gameLogic.getAmountTries()}"
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
    }

    private fun updateTries() {
        if (gameLogic.getTries() > gameLogic.getAmountTries()-1) {
            gameLost()
        } else {
            binding.hangmanPlaceholder.text =
                "Try ${gameLogic.getTries()} of ${gameLogic.getAmountTries()}"
        }
    }

    private fun gameWon() {
        binding.hangmanPlaceholder.text = "You Won!" //TODO go to results
    }

    private fun gameLost() {
        binding.hangmanPlaceholder.text = "You Lose!" //TODO go to results
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
