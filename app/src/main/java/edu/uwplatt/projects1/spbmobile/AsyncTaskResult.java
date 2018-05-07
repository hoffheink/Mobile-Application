package edu.uwplatt.projects1.spbmobile;

/**
 * Centralize the return type of an AsyncTask, provided any parameter. Brings together data types
 * and error types.
 *
 * @param <T> any object type to be returned at the end of the execution of an AsyncTask.
 */
public class AsyncTaskResult<T> {
    private T result;
    private Exception error;

    /**
     * Returns the result of a any established data type to the user.
     *
     * @return the result of data type defined for an object.
     */
    public T getResult() {
        return result;
    }

    /**
     * Returns the exception caused in an AsyncTask.
     *
     * @return the exception caused in an AsyncTask.
     */
    public Exception getError() {
        return error;
    }

    /**
     * Sets the values of a result produced in an AsyncTask of any object type, except
     * an exception object.
     *
     * @param result the result produced by an AsyncTask.
     */
    public AsyncTaskResult(T result) {
        super();
        this.result = result;
    }

    /**
     * Sets the values of a exception produced in an AsyncTask.
     *
     * @param error the exception produced by an AsyncTask.
     */
    public AsyncTaskResult(Exception error) {
        super();
        this.error = error;
    }
}