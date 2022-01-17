
package thm.ap.hangman.persistence

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import thm.ap.hangman.models.Category


class CategoryDAO {
    private val categoriesRef: CollectionReference = Firebase.firestore.collection("categories")
    val categoriesObserver: MutableLiveData<List<Category>> by lazy {
        refreshCategories()
        MutableLiveData<List<Category>>()
    }

    private fun refreshCategories() {
        categoriesRef.get().addOnSuccessListener {
            val cats = mutableListOf<Category>()
            it.forEach { doc ->
                val category = doc.toObject<Category>()
                cats.add(category)
            }
            categoriesObserver.value = cats
        }
    }

    fun addCategory(category: Category) {
        categoriesRef.document().set(category).addOnSuccessListener {
            refreshCategories()
        }
    }

    fun updateCategory(category: Category) {
        categoriesRef.document(category.id).set(category).addOnSuccessListener {
            refreshCategories()
        }
    }

    fun deleteCategory(category: Category) {
        categoriesRef.document(category.id).delete().addOnSuccessListener {
            refreshCategories()
        }
    }
}