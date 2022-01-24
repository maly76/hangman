package thm.ap.hangman.models

import com.google.firebase.firestore.DocumentId
import java.io.Serializable


data class Category(
    @set:DocumentId
    var id: String = ""
) : Serializable {
    var catName: String? = null
    var words: MutableList<String>? = null

    companion object {
        fun new(catName: String, words: MutableList<String>): Category {
            val category = Category()
            category.catName = catName
            category.words = words
            return category
        }

        fun empty(): Category {
            return Category()
        }
    }
}