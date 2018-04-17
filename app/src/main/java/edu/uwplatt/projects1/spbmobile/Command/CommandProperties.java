package edu.uwplatt.projects1.spbmobile.Command;

import java.util.Date;
import java.util.UUID;

import edu.uwplatt.projects1.spbmobile.Time;

class CommandProperties {
    private static UUID getRandomUUID() {
        return UUID.randomUUID();
    }

    private int priority;
    private UUID guid;
    private String timestamp;

    CommandProperties(boolean isPriority) {
        if (isPriority)
            priority = 1;
        else
            priority = 0;
        guid = getRandomUUID();
        timestamp = Time.getUTCTime(new Date());
    }

    public CommandProperties(boolean isPriority, UUID inGuid) {
        this(isPriority);
        guid = inGuid;
    }
}
