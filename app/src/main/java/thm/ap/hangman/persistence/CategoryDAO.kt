package thm.ap.hangman.persistence

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import thm.ap.hangman.models.Category
import thm.ap.hangman.models.Result

/**
 * The category service for managing the Categories in the database
 * */
class CategoryDAO {
    private val categoriesRef: CollectionReference = Firebase.firestore.collection(TAG)
    private val categoriesObserver = MutableLiveData<Result<List<Category>>>()

    /**
     * It can be observed to always receive a notification if the categories are locally changed
     * @return the categoriesObserver
     * */
    fun getCategoriesObserver(): MutableLiveData<Result<List<Category>>> {
        refreshCategories()
        return categoriesObserver
    }

    /**
     * It will be called after changes on Categories
     * set the value of categoriesObserver a Result in progress to notify the observer
     * set the value on Result with success if the request was successfully or on Result with failure if the request is failed
     * */
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
                    categoriesObserver.value = Result.success(cats)
                } else {
                    categoriesObserver.value = Result.failure(task.exception!!.message!!)
                }
            }
    }

    /**
     * add category to the database
     * @param category is the specified category which will be added to the database
     * @return an observer which will receive a notification:
     * 1- a Result with the category if it is successfully added
     * 2- a Result with an error it is failed
     * */
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

    /**
     * update a category in the database
     * @param category the specified category to update
     * @return an observer which will receive a notification:
     * 1- a Result with the category if it is successfully updated
     * 2- a Result with an error it is failed
     * */
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

    /**
     * delete a category from the database
     * @param category the specified category to delete
     * @return an observer which will receive a notification:
     * 1- a Result with the category if it is successfully deleted
     * 2- a Result with an error it is failed
     * */
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