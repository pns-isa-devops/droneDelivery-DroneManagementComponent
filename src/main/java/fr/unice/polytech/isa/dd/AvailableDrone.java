package fr.unice.polytech.isa.dd;

import fr.unice.polytech.isa.dd.entities.Drone;

import javax.ejb.Local;
import java.util.List;

@Local
public interface AvailableDrone {

    List<Drone> allDroneAvailable();
}
