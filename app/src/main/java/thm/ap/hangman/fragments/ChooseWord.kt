package thm.ap.hangman.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import thm.ap.hangman.R
import thm.ap.hangman.databinding.FragmentChooseWordBinding
import thm.ap.hangman.gamelogic.GameLogic
import thm.ap.hangman.models.Player
import thm.ap.hangman.models.Result
import thm.ap.hangman.persistence.CompetitionDAO
import thm.ap.hangman.service.AuthenticationService

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChooseWord.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChooseWord : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val competitionDAO = CompetitionDAO(this)
    private var _binding: FragmentChooseWordBinding? = null
    private val binding get() = _binding!!
    private var roomID: String? = null
    private var guestFound = false
    private var isHost = false

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
    ): View {
        _binding = FragmentChooseWordBinding.inflate(inflater, container, false)

        arguments?.let {
            val roomId = requireArguments().getString("roomId")
            roomID = roomId
            roomId.let {
                competitionDAO.getCompetitionByID(roomId!!).observe(viewLifecycleOwner) { result ->
                    if (result.status == Result.Status.SUCCESS) {
                        val comp = result.data!!
                        binding.room.text = "Room code: ${comp.roomCode}"
                        if (AuthenticationService.getCurrentUser()!!.uid == comp.host.id) {
                            /** HOST */
                            isHost = true
                            if (comp.guest != null) {
                                binding.oponent.text = comp.guest!!.userName
                                setVisible(true)
                            } else {
                                binding.indeterminateBar.visibility = View.VISIBLE
                                binding.oponent.text = "Waiting for an openent"
                                subscribeCompForChanges(roomId)
                            }
                        } else {
                            /** GUEST */
                            subscribeCompForChanges(roomId)
                            binding.oponent.text = comp.host.userName
                            setVisible(true)
                        }
                    }
                }
            }
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun subscribeCompForChanges(roomId: String) {
        competitionDAO.notifyIfRoomDeleted(roomId).observe(viewLifecycleOwner) {
            if (it) {
                val navController = findNavController()
                navController.popBackStack()
            }
        }

        competitionDAO.subscribeCompetition(roomId).observe(viewLifecycleOwner) {
            if (it.status == Result.Status.SUCCESS) {
                val c = it.data!!
                if (isHost && c.hostInfos.hiddenWord != null) {
                    binding.hiddenWord.text = GameLogic.generateHiddenWord(c.hostInfos.hiddenWord!!)
                } else if (!isHost && c.guestInfos.hiddenWord != null) {
                    binding.hiddenWord.text =
                        GameLogic.generateHiddenWord(c.guestInfos.hiddenWord!!)
                }

                if (c.guest != null && !guestFound) {
                    guestFound = true
                    if (isHost) {
                        binding.oponent.text = c.guest!!.userName
                    } else {
                        binding.oponent.text = c.host.userName
                    }
                    binding.indeterminateBar.visibility = View.GONE
                    setVisible(true)
                }

                if (c.guest != null && c.hostInfos.status == Player.Status.READY && c.guestInfos.status == Player.Status.READY) {
                    competitionDAO.unsubscripeCompetition()
                    val navController = findNavController()
                    val action = ChooseWordDirections.actionChooseWordToPlayingField(roomID!!)
                    navController.navigate(action)
                }
            }
        }
    }

    private fun setVisible(visible: Boolean) {
        val value = if (visible) View.VISIBLE else View.GONE
        binding.oponentWord.visibility = value
        binding.choseHiddenWord.visibility = value
        binding.buttonOk.visibility = value
        binding.hiddenWord.visibility = value
        binding.word.visibility = value
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
                    builder.setCancelable(false)
                    builder.setMessage("Do you want to go back?")
                    builder.setPositiveButton(
                        "Yes"
                    ) { _, _ -> //if user pressed "yes", then he is allowed to exit from application
                        competitionDAO.exitRoom(roomID!!)
                        val navController = findNavController()
                        navController.popBackStack()
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

        val buttonOk: Button = view.findViewById(R.id.button_ok)
        buttonOk.setOnClickListener {
            competitionDAO.getCompetitionByID(roomID!!).observe(viewLifecycleOwner) { result ->
                if (result.status == Result.Status.SUCCESS) {
                    val comp = result.data!!
                    if (isHost) {
                        comp.guestInfos.hiddenWord = binding.word.text.toString()
                        comp.guestInfos.wortToGuess = binding.word.text.toString()
                        comp.hostInfos.status = Player.Status.READY
                        if (comp.hostInfos.status == Player.Status.READY && comp.guestInfos.status == Player.Status.OFFLINE) {
                            setWaitingForAponent(comp.guest!!.userName!!)
                        }
                    } else {
                        comp.hostInfos.hiddenWord = binding.word.text.toString()
                        comp.hostInfos.wortToGuess = binding.word.text.toString()
                        comp.guestInfos.status = Player.Status.READY
                        if (comp.guestInfos.status == Player.Status.READY && comp.hostInfos.status == Player.Status.OFFLINE) {
                            setWaitingForAponent(comp.host.userName!!)
                        }
                    }
                    competitionDAO.updateCompetition(comp)
                }
            }
        }
    }

    private fun setWaitingForAponent(name: String) {
        binding.buttonOk.visibility = View.GONE
        binding.word.visibility = View.GONE
        binding.oponentWord.visibility = View.GONE
        binding.choseHiddenWord.text = "Waiting for ${name} to get ready .."
        binding.indeterminateBar.visibility = View.VISIBLE
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChooseWord.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChooseWord().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}