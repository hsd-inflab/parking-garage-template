package de.hsd.inflab.parkinggarage.parking;

import de.hsd.inflab.parkinggarage.models.vehicles.Vehicle;

public class ParkingSpot {
    private Vehicle occupant;

    public ParkingSpot() {
        free();
    }

    public boolean isFree() {
        return occupant == null;
    }

    public void park(Vehicle vehicle) {
        if(isFree())
            occupant = vehicle;
    }

    public void free() {
        if(!isFree())
            occupant = null;
    }

    public Vehicle getOccupant() {
        return occupant;
    }
}
