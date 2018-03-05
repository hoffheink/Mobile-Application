package edu.uwplatt.projects1.spbmobile.Shadow;

/**
 * Created by Bear on 3/4/2018.
 */

public class AsyncTaskResult<T>
{
    private T result;
    private Exception error;

    public T getResult()
    {
        return result;
    }

    public Exception getError()
    {
        return error;
    }

    public AsyncTaskResult(T result)
    {
        super();
        this.result = result;
    }

    public AsyncTaskResult(Exception error)
    {
        super();
        this.error = error;
    }
}