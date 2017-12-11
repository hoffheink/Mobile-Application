package edu.uwplatt.projects1.spbmobile;

/**
 * Created by Bear on 12/6/2017.
 */

public class RequestClass
{
    private String apId;
    private String apPin;

    public RequestClass()
    {
        return;
    }

    public  RequestClass(String id, String pin)
    {
        this.apId = id;
        this.apPin = pin;
    }

    public void SetPin(String pin)
    {
        apPin = pin;
    }

    public String GetPin()
    {
        return this.apPin;
    }

    public void SetId(String id)
    {
        this.apId = id;
    }

    public String GetId()
    {
        return this.apId;
    }
}