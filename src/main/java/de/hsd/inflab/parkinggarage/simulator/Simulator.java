package de.hsd.inflab.parkinggarage.simulator;

import de.hsd.inflab.parkinggarage.models.vehicles.Vehicle;
import de.hsd.inflab.parkinggarage.models.vehicles.VehicleTypes;
import de.hsd.inflab.parkinggarage.parking.Lot;
import de.hsd.inflab.parkinggarage.models.Receipt;
import de.hsd.inflab.parkinggarage.models.ticket.Ticket;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Simulator {
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

    private SimClock clock;
    private Lot lot;
    private final Random rng;

    private final List<Driver> drivers = new ArrayList<>();
    private int averageParkingTime = 120;
    private double targetOccupancyRatio = 0.8;

    private String lastTransaction = "Noch keine Transaktion";


    public Simulator(Lot lot, SimClock clock, Random rng) {
        this.clock = clock;
        this.lot = lot;
        this.rng = rng;
    }

    // Rechnet genau einen Tick (kein sleep, kein Loop) - fuer GUI und CLI nutzbar.
    public void step() {
        clock.tick();
        double departureProbabilityPerVehicle = 1.0 / averageParkingTime;
        double vehicleSpawnRate = lot.getCapacity() * targetOccupancyRatio * departureProbabilityPerVehicle;
        arrival(vehicleSpawnRate);
        departure(departureProbabilityPerVehicle);
        System.out.println("Vehicles in lot: " + lot.countVehicles());
    }

    private void arrival(double vehicleSpawnRate) {
        double CAR_PROB = 0.7;
        double SEMI_PROB = 0.2;
        double MOTORCYCLE_PROB = 0.1;

        if(rng.nextDouble() >= vehicleSpawnRate) return;
        VehicleTypes type;
        double roll = rng.nextDouble();
        if(roll < CAR_PROB) {
            type = VehicleTypes.CAR;
        }
        else if(roll < (CAR_PROB+SEMI_PROB)) {
            type = VehicleTypes.SEMI;
        }
        else {
            type = VehicleTypes.MOTORCYCLE;
        }

        Vehicle vehicle = new Vehicle(type);


        System.out.println(vehicle.toString() + " wants to enter the parking lot.");

        Ticket ticket = lot.enter(vehicle);
        if (ticket.isValid()) {
            drivers.add(new Driver(ticket, vehicle));
            lastTransaction = "Einfahrt: " + vehicle + " um " + clock.now().format(TIME);
            System.out.println("Success. Entered at " + ticket.getTimeStamp());
        }
        else {
            String reason = ticket.isValid() ? "Parkhaus voll" : ticket.getReason();
            lastTransaction = "Abgewiesen: " + vehicle + " - " + reason;
            System.out.println("Entering refused. Reason: " + reason);
        }

    }

    private void departure(double departureProbabilityPerVehicle) {
        List<Driver> departingDrivers = new ArrayList<>();
        for (Driver d : drivers) {
            if(rng.nextDouble() >= departureProbabilityPerVehicle) continue;
            System.out.println(d.getVehicle() + " wants to leave the parking lot.");
            Receipt receipt = lot.exit(d.getTicket(), d.getVehicle());
            departingDrivers.add(d);
            lastTransaction = "Ausfahrt: " + d.getVehicle() + " zahlt " + receipt.getAmount()
                    + " EUR fuer " + receipt.getParkingTime().toMinutes() + " min";
            printReceipt(receipt);
        }
        for (Driver d: departingDrivers) {
            drivers.remove(d);
        }
    }

    private void printReceipt(Receipt receipt) {
        System.out.println("Parking Receipt: " + receipt.getType() + " has to pay " + receipt.getAmount() + " € for " + receipt.getParkingTime().toMinutes() + " minutes");
    }

    public void setTargetOccupancyRatio(double targetOccupancyRatio) {
        this.targetOccupancyRatio = targetOccupancyRatio;
    }

    public double getTargetOccupancyRatio() {
        return targetOccupancyRatio;
    }

    public String getLastTransaction() {
        return lastTransaction;
    }

    public String getTime() {
        return clock.now().format(TIME);
    }

}
