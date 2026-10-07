package de.hsd.inflab.parkinggarage.parking.gate.impl;

import de.hsd.inflab.parkinggarage.models.Receipt;
import de.hsd.inflab.parkinggarage.models.vehicles.VehicleTypes;
import de.hsd.inflab.parkinggarage.parking.gate.iface.Exit;

import java.time.Duration;

public class ExitGate implements Exit {
    public Receipt processOutgoing() {
        return new Receipt(500, Duration.ofMinutes(90), VehicleTypes.CAR);
    }
}
