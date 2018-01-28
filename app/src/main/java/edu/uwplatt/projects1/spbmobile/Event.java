package edu.uwplatt.projects1.spbmobile;


/**
 * Created by jakei on 1/28/2018.
 */

/**
 * This class handles events sent to/received from the cloud and appliance.
 */
public class Event {
    private String id;
    private String status;
    private Priorities priority;
    public enum Priorities{
        STANDARD,
        URGENT
    }

    /**
     * Constructor
     * @param inId id of the device.
     * @param inStatus status of the device.
     * @param inPriority event priority.
     */
    public Event(String inId, String inStatus, Priorities inPriority) {
        id = inId;
        status = inStatus;
        priority = inPriority;
    }

    /**
     * Gets the id.
     * @return id
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id.
     */
    public void setId(String inID)
    {
        id = inID;
    }

    /**
     * Gets the status.
     * @return status
     */
    public String getStatus()
    {
        return status;
    }

    /**
     * Sets the status
     * @param inStatus status
     */
    public void setStatus(String inStatus)
    {
        status = inStatus;
    }

    /**
     * Gets the id.
     * @return priority
     */
    public Priorities getPriority()
    {
        return priority;
    }

    /**
     * Sets the priority.
     * @param inPriority priority.
     */
    public void setPriority(Priorities inPriority)
    {
        priority = inPriority;
    }
}
