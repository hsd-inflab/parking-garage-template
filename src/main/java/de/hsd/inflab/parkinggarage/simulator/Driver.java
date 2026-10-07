package de.hsd.inflab.parkinggarage.simulator;

import de.hsd.inflab.parkinggarage.models.ticket.Ticket;
import de.hsd.inflab.parkinggarage.models.vehicles.Vehicle;

public class Driver {
    private Vehicle vehicle;
    private Ticket ticket;

    public Driver(Ticket ticket, Vehicle vehicle) {
        this.ticket = ticket;
        this.vehicle = vehicle;
    }


    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }
}
