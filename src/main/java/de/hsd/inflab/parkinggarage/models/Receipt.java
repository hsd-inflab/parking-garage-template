package de.hsd.inflab.parkinggarage.models;

import de.hsd.inflab.parkinggarage.models.vehicles.Vehicle;
import de.hsd.inflab.parkinggarage.models.vehicles.VehicleTypes;

import java.time.Duration;
import java.time.LocalDateTime;

public class Receipt {
    private int amount;
    private Duration parkingTime;
    private VehicleTypes type;

    public Receipt (int amount, Duration parkingTime, VehicleTypes type) {
        this.amount = amount;
        this.parkingTime = parkingTime;
        this.type = type;
    }

    public int getAmount() {

        return amount;
    }

    public Duration getParkingTime() {
        return parkingTime;
    }

    public VehicleTypes getType() {
        return type;
    }
}
