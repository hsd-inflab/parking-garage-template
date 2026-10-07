package de.hsd.inflab.parkinggarage.models.ticket;

import java.time.LocalDateTime;

public abstract class Ticket {

    protected LocalDateTime timeStamp;
    protected String reason;

    public static Ticket issue(LocalDateTime timeStamp) {
        return new ValidTicket(timeStamp);
    }

    public static Ticket denyEntry(String reason) {
        return new NoEntryTicket(reason);
    }

    public abstract boolean isValid();

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }
    public String getReason() {
        return reason;
    }
}
