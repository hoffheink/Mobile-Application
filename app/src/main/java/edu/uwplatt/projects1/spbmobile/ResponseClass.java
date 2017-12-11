package edu.uwplatt.projects1.spbmobile;

/**
 * Created by Bear on 12/6/2017.
 */

public class ResponseClass
{
    String retMsg;

    public ResponseClass()
    {
        return;
    }

    public ResponseClass(String confirmation)
    {
        this.retMsg = confirmation;
    }

    public void SetCon(String msg)
    {
        this.retMsg = msg;
    }

    public String GetMsg()
    {
        return retMsg;
    }
}