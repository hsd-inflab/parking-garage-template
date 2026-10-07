package de.hsd.inflab.parkinggarage.models.ticket;

public class NoEntryTicket extends Ticket {

    NoEntryTicket(String reason){
        this.reason = reason;
    }

    @Override
    public boolean isValid() {
        return false;
    }

}
