package de.hsd.inflab.parkinggarage.models.ticket;

import java.time.LocalDateTime;

public class ValidTicket extends Ticket {
    ValidTicket(LocalDateTime timeStamp){
        this.timeStamp = timeStamp;
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
