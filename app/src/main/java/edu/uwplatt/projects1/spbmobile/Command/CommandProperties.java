package edu.uwplatt.projects1.spbmobile.Command;

import java.util.Date;
import java.util.UUID;

import edu.uwplatt.projects1.spbmobile.Time;

/**
 * This class represents command properties.
 */
class CommandProperties {
    private static UUID getRandomUUID() {
        return UUID.randomUUID();
    }

    private int priority;
    private UUID guid;
    private String timestamp;

    /**
     * This constructor creates Command
     * */
    FINISH THIS
    /*
     * @param isPriority
     * @param date
     */
    private CommandProperties(boolean isPriority, Date date) {
        if (isPriority)
            priority = 1;
        else
            priority = 0;
        guid = getRandomUUID();
        timestamp = Time.getUTCTime(date);
    }

    CommandProperties(boolean isPriority, Date date, UUID inGuid) {
        this(isPriority, date);
        guid = inGuid;
    }
}
