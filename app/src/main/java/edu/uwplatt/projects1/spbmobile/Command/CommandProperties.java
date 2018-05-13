package edu.uwplatt.projects1.spbmobile.Command;

import java.util.Date;
import java.util.UUID;

/**
 * This class represents command properties.
 */
class CommandProperties {
    /**
     * This method returns a random UUID.
     *
     * @return the random UUID.
     */
    static UUID getRandomUUID() {
        return UUID.randomUUID();
    }

    private int priority;
    private UUID guid;
    private long timestamp;

    /**
     * This constructor creates a Command.
     *
     * @param isPriority the boolean representing whether or not this Command is a priority or not.
     * @param date       the Date this Command was created.
     */
    private CommandProperties(boolean isPriority, Date date) {
        if (isPriority)
            priority = 1;
        else
            priority = 0;
        guid = getRandomUUID();
        timestamp = date.getTime();//Time.getUTCTime(date);
    }

    /**
     * This constructor creates a Command with a Guid passed in.
     *
     * @param isPriority the boolean representing whether or not this Command is a priority or not.
     * @param date       the Date this Command was created.
     * @param inGuid     the Guid used to identify this Command.
     */
    CommandProperties(boolean isPriority, Date date, UUID inGuid) {
        this(isPriority, date);
        guid = inGuid;
    }
}
