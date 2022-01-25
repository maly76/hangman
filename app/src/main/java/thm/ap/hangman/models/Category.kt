package thm.ap.hangman.models

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

/**
 * The category-entity for firebase
 * The id is the primary key
 * */
data class Category(
    @set:DocumentId
    var id: String = ""
) : Serializable {
    var catName: String? = null
    var words: MutableList<String>? = null

    companion object {
        /**
         * Static method for creating a new category
         * @param catName a specified name for the category
         * @param words a list with the specified words
         * @return a new object of category
         * */
        fun new(catName: String, words: MutableList<String>): Category {
            val category = Category()
            category.catName = catName
            category.words = words
            return category
        }

        /**
         * Create an empty category without name and words
         * @return an empty object of category
         * */
        fun empty(): Category {
            return Category()
        }
    }
}