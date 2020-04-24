package fr.unice.polytech.isa.dd;

import fr.unice.polytech.isa.dd.entities.DRONE_STATES;
import fr.unice.polytech.isa.dd.entities.Drone;

import javax.ejb.Local;
import java.util.HashMap;
import java.util.List;

@Local
public interface dronestatus {
    List<fr.unice.polytech.isa.dd.entities.DroneStatus> getAllHistoryDrone (String idDrone);
    HashMap<Drone, fr.unice.polytech.isa.dd.entities.DroneStatus> getallstatus();
    HashMap<Drone, fr.unice.polytech.isa.dd.entities.DroneStatus> getAllLoadingDrone();
    HashMap<Drone, fr.unice.polytech.isa.dd.entities.DroneStatus> getAllFixingDrone();
    void setStatut(DRONE_STATES states, Drone drone) throws Exception;
}
