package thm.ap.hangman.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import thm.ap.hangman.adapters.CustomCategoriesAdapter
import thm.ap.hangman.databinding.FragmentSinglePlayerBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SinglePlayer.newInstance] factory method to
 * create an instance of this fragment.
 */
class SinglePlayer : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentSinglePlayerBinding? = null
    private val binding get() = _binding!!

    lateinit var db: FirebaseFirestore
    lateinit var querySnapshot: QuerySnapshot

    var tracker: SelectionTracker<Long>? = null
    lateinit var customCategoriesAdapter: CustomCategoriesAdapter

    var dataSet: MutableList<String> = mutableListOf()
    lateinit var dataSetSelected: List<String>

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
        _binding = FragmentSinglePlayerBinding.inflate(inflater, container, false)
        val view = binding.root

        db = Firebase.firestore

        customCategoriesAdapter = CustomCategoriesAdapter(dataSet)
        binding.recyclerviewCategories.adapter = customCategoriesAdapter

        tracker = SelectionTracker.Builder<Long>(
            "mySelection",
            binding.recyclerviewCategories,
            StableIdKeyProvider(binding.recyclerviewCategories),
            ItemLookup(binding.recyclerviewCategories),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()
        customCategoriesAdapter.setTracker(tracker)

        tracker?.addObserver(object: SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                //handle the selected according to your logic
            }
        })

        tracker?.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    dataSetSelected = tracker?.selection!!.map {
                        customCategoriesAdapter.dataSet[it.toInt()]
                    }.toList()
                }
            })

        db.collection("categories")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    querySnapshot = result
                    dataSet.add(document.id)
                }
                customCategoriesAdapter.dataSet = dataSet
                customCategoriesAdapter.notifyDataSetChanged()
            }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

        binding.buttonChoose.setOnClickListener {
            var listOfSelectedWords = mutableListOf<String>()
            for (document in querySnapshot) {
                if(dataSetSelected.contains(document.id)){
                    listOfSelectedWords.addAll(document.get("words") as List<String>)
                }
            }

            val randomWord = listOfSelectedWords.random().trim()
            Log.e("test", randomWord)
            val action = SinglePlayerDirections.actionSinglePlayerToPlayingField("word-$randomWord")
            navController.navigate(action)
        }
    }

    inner class ItemIdKeyProvider(private val recyclerView: RecyclerView)
        : ItemKeyProvider<Long>(SCOPE_MAPPED) {

        override fun getKey(position: Int): Long? {
            return recyclerView.adapter?.getItemId(position)
                ?: throw IllegalStateException("RecyclerView adapter is not set!")
        }

        override fun getPosition(key: Long): Int {
            val viewHolder = recyclerView.findViewHolderForItemId(key)
            return viewHolder?.layoutPosition ?: RecyclerView.NO_POSITION
        }
    }

    inner class ItemLookup(private val rv: RecyclerView)
        : ItemDetailsLookup<Long>() {
        override fun getItemDetails(event: MotionEvent)
                : ItemDetails<Long>? {

            val view = rv.findChildViewUnder(event.x, event.y)
            if(view != null) {
                return (rv.getChildViewHolder(view) as CustomCategoriesAdapter.ViewHolder)
                    .getItemDetails()
            }
            return null
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SinglePlayer.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SinglePlayer().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}