package thm.ap.hangman.persistence

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import thm.ap.hangman.models.Category


class CategoryDAO {
    val categoriesRef: CollectionReference = Firebase.firestore.collection("categories")

    fun getCategories(): MutableLiveData<List<Category>> {
        val categories: MutableLiveData<List<Category>> by lazy {
            MutableLiveData<List<Category>>()
        }

        categoriesRef.get().addOnSuccessListener {
            val cats = mutableListOf<Category>()
            it.forEach { doc ->
                val category = doc.toObject<Category>()
                cats.add(category)
            }
            categories.value = cats
        }
        return categories
    }

    fun addCategory(category: Category) = categoriesRef.document().set(category)

    fun updateCategory(category: Category) = categoriesRef.document(category.id).set(category)

    fun deleteCategory(category: Category) = categoriesRef.document(category.id).delete()
}