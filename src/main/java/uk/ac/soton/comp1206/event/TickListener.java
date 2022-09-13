package uk.ac.soton.comp1206.event;

/**
 * Used to listen for the passing of ticks during the simulation
 */
public interface TickListener {

    /**
     * Activated when a tick passes in the simulation
     */
    void tick();
}
