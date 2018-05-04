package edu.uwplatt.projects1.spbmobile;

/**
 * This class handles events sent to/received from the cloud and appliance.
 */
public class Event {
    private String id;
    private String status;
    private Priorities priority;

    public enum Priorities {
        Standard,
        Urgent
    }

    /**
     * This constructor will create an Event.
     *
     * @param id       the id of the Appliance.
     * @param status   the status of the Appliance.
     * @param priority the event's priority.
     */
    //Todo: Is this needed, it doesn't appear to be used.
    public Event(String id, String status, Priorities priority) {
        this.id = id;
        this.status = status;
        this.priority = priority;
    }

    /**
     * Gets the id.
     *
     * @return the id.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the status.
     *
     * @return the status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the priority.
     *
     * @return the priority.
     */
    public Priorities getPriority() {
        return priority;
    }

    /**
     * Sets the priority.
     *
     * @param priority the new priority.
     */
    public void setPriority(Priorities priority) {
        this.priority = priority;
    }
}
