package thm.ap.hangman.models

/**
 * The Result class, it is used for events to return a specified status and data
 * T should be specified and it is the datatype of @property data
 * @param status to identify the status of the request SUCCESS, IN_PROGRESS or FAILURE
 * @param data is only set if the status is SUCCESS (the request is successfully)
 * @param error is only set if the status is FAILURE (the request is failed)
 * */
class Result<T>(val status: Status, val data: T? = null, val error: String? = null) {
    enum class Status {
        SUCCESS, IN_PROGRESS, FAILURE
    }

    companion object {
        /**
         * @param data, the datatype should be the same of the specified T
         * @return a new object of Result with Status SUCCESS and the data
         * */
        fun <T> success(data: T?) = Result(Status.SUCCESS, data)

        /**
         * @param error is the message of the error
         * @return a new object of Result with the status FAILURE and the specified error
         * */
        fun <T> failure(error: String) = Result<T>(Status.FAILURE, error = error)

        /**
         * @return a new object of Result with the status IN_PROGRESS
         * */
        fun <T> inProgress() = Result<T>(Status.IN_PROGRESS)
    }
}