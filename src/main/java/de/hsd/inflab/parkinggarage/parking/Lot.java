package de.hsd.inflab.parkinggarage.parking;

import de.hsd.inflab.parkinggarage.parking.gate.iface.Entrance;
import de.hsd.inflab.parkinggarage.parking.gate.iface.Exit;
import de.hsd.inflab.parkinggarage.parking.gate.impl.EntranceGate;
import de.hsd.inflab.parkinggarage.models.Receipt;
import de.hsd.inflab.parkinggarage.models.ticket.Ticket;
import de.hsd.inflab.parkinggarage.parking.gate.impl.ExitGate;
import de.hsd.inflab.parkinggarage.simulator.SimClock;
import de.hsd.inflab.parkinggarage.models.vehicles.Vehicle;

import java.util.List;

public class Lot {
    private Entrance entrance;
    private Exit exit;
    private SimClock clock;
    private int capacity;
    private ParkingSpot[] spots;

    public Lot(Entrance entrance, Exit exit, SimClock clock) {
        this.entrance = entrance;
        this.exit = exit;
        this.clock = clock;
        capacity = 100;
        spots = new ParkingSpot[capacity];
        for (int i=0; i<capacity; i++) {
            spots[i] = new ParkingSpot();
        }
    }

    public static Lot buildLot(SimClock clock) {
        // Standard = jeweils erste Implementierung aus den Katalog-Listen unten
        return new Lot(availableEntrances().get(0), availableExits().get(0), clock);
    }

    //  Tragen Sie hier Ihre eigenen Einfahrt- bzw. Ausfahrt-Klassen ein, damit sie
    //  in der GUI auswaehlbar werden. Die jeweils erste Klasse ist der Standard. ----
    public static List<Entrance> availableEntrances() {
        return List.of(new EntranceGate() /*, new IhreEinfahrtRegel() */);
    }

    public static List<Exit> availableExits() {
        return List.of(new ExitGate() /*, new IhreAusfahrtRegel() */);
    }
    //-----------------------------------

    public Entrance getEntrance() {
        return entrance;
    }

    public void setEntrance(Entrance entrance) {
        this.entrance = entrance;
    }

    public Exit getExit() {
        return exit;
    }

    public void setExit(Exit exit) {
        this.exit = exit;
    }

    public Ticket enter(Vehicle vehicle) {
        Ticket ticket = entrance.processIncoming();
        if (ticket.isValid())
            assignSpot(vehicle);
        return ticket;
    }

    public Receipt exit(Ticket ticket, Vehicle vehicle) {
        Receipt receipt = exit.processOutgoing();
        releaseSpot(vehicle);
        return receipt;
    }

    private void assignSpot(Vehicle vehicle) {
        for (ParkingSpot s : spots) {
            if(s.isFree()) {
                s.park(vehicle);
                return;
            }

        }
    }

    private void releaseSpot(Vehicle vehicle) {
        for (ParkingSpot s : spots) {
            if(s.getOccupant() == vehicle) {
                s.free();
                return;
            }
        }
    }

    public boolean isFull() {
        for (ParkingSpot s: spots) {
            if (s.isFree())
                return false;
        }
        return true;        //no free spaces in lot
    }

    public int getCapacity() {
        return capacity;
    }

    public ParkingSpot[] getSpots() {
        return spots;
    }

    public int countVehicles() {
        int vehiclesInLot = 0;
        for (ParkingSpot s : spots) {
            if (!s.isFree())
                vehiclesInLot++;
        }
        return vehiclesInLot;
    }

    public double calculateOccupancy() {
        return (double) countVehicles() / capacity;
    }
}
