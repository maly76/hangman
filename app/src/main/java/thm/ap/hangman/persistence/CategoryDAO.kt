
package thm.ap.hangman.persistence

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import thm.ap.hangman.models.Category
import thm.ap.hangman.models.Result

class CategoryDAO {
    private val categoriesRef: CollectionReference = Firebase.firestore.collection(TAG)
    private val categoriesObserver = MutableLiveData<Result<List<Category>>>()

    fun getCategoriesObserver(): MutableLiveData<Result<List<Category>>> {
        refreshCategories()
        return categoriesObserver
    }

    private fun refreshCategories() {
        categoriesObserver.value = Result.inProgress()
        categoriesRef.get()
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    val cats = mutableListOf<Category>()
                    task.result.forEach { doc ->
                        val category = doc.toObject<Category>()
                        cats.add(category)
                    }
                    cats.removeIf { cat -> cat.id == "baseline" }
                    categoriesObserver.value = Result.success(cats)
                } else {
                    categoriesObserver.value = Result.failure(task.exception!!.message!!)
                }
            }
    }

    fun addCategory(category: Category): MutableLiveData<Result<Category>> {
        val observer = MutableLiveData<Result<Category>>()

        observer.value = Result.inProgress()
        categoriesRef.document().set(category)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    refreshCategories()
                    observer.value = Result.success(category)
                } else {
                    observer.value = Result.failure(task.exception!!.message!!)
                }
            }

        return observer
    }

    fun updateCategory(category: Category): MutableLiveData<Result<Category>> {
        val observer = MutableLiveData<Result<Category>>()

        observer.value = Result.inProgress()
        categoriesRef.document(category.id).set(category)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    refreshCategories()
                    observer.value = Result.success(category)
                } else {
                    observer.value = Result.failure(task.exception!!.message!!)
                }
            }

        return observer
    }

    fun deleteCategory(category: Category): MutableLiveData<Result<Category>> {
        val observer = MutableLiveData<Result<Category>>()

        observer.value = Result.inProgress()
        categoriesRef.document(category.id).delete()
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    refreshCategories()
                    observer.value = Result.success(category)
                } else {
                    observer.value = Result.failure(task.exception!!.message!!)
                }
            }

        return observer
    }

    companion object {
        const val TAG = "categories"
    }
}