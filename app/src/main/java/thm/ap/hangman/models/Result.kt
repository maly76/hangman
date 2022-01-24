package thm.ap.hangman.models

class Result<T>(val status: Status, val data: T? = null, val error: String? = null) {
    enum class Status {
        SUCCESS, IN_PROGRESS, FAILURE
    }

    companion object{
        fun <T> success(data: T?) = Result(Status.SUCCESS, data)
        fun <T> failure(error: String) = Result<T>(Status.FAILURE, error = error)
        fun <T> inProgress() = Result<T>(Status.IN_PROGRESS)
    }
}