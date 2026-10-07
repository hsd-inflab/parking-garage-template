package de.hsd.inflab.parkinggarage.models.vehicles;

public class Vehicle {
    private static int vehicleCount = 0;
    private final VehicleTypes type;

    public Vehicle(VehicleTypes type) {
        vehicleCount++;
        this.type = type;
    }

    public VehicleTypes getVehicleType() {
        return type;
    }

    @Override
    public String toString() {
        return type.toString() + vehicleCount;
    }
}
