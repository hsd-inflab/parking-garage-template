package de.hsd.inflab.parkinggarage.simulator;
import java.time.LocalDateTime;

public class SimClock {
    private LocalDateTime clock;

    public SimClock(LocalDateTime startTime) {
        this.clock = startTime;
    }

    public void tick() {
        clock = clock.plusMinutes(1);
    }

    public LocalDateTime now() {
        return clock;
    }
}
