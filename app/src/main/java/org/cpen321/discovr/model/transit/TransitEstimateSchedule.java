package org.cpen321.discovr.model.transit;

import java.util.Date;

public class TransitEstimateSchedule {
    public final String destination;
    public final int expectedCountdown;
    public final TransitScheduleStatus scheduleStatus;
    public final Date lastUpdated;

    public TransitEstimateSchedule(String destination, int expectedCountdown,
                                   String rawStatus, Date lastUpdated) {
        this.destination = destination;
        this.expectedCountdown = expectedCountdown;
        this.lastUpdated = lastUpdated;

        switch (rawStatus){
            case "*":
                this.scheduleStatus = TransitScheduleStatus.ON_TIME;
                break;

            case "+":
                this.scheduleStatus = TransitScheduleStatus.AHEAD;
                break;

            case "-":
                this.scheduleStatus = TransitScheduleStatus.BEHIND;
                break;

            default:
                this.scheduleStatus = null;
        }
    }
}
