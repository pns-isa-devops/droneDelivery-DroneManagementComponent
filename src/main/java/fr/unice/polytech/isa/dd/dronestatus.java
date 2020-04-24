package fr.unice.polytech.isa.dd;

import fr.unice.polytech.isa.dd.entities.DRONE_STATES;
import fr.unice.polytech.isa.dd.entities.Drone;
import fr.unice.polytech.isa.dd.entities.DroneStatus;

import javax.ejb.Local;
import java.util.HashMap;
import java.util.List;

@Local
public interface dronestatus {
    List<DroneStatus> getAllHistoryDrone (String idDrone);
    HashMap<Drone, DroneStatus> getallstatus();
    HashMap<Drone, DroneStatus> getAllLoadingDrone();
    HashMap<Drone, DroneStatus> getAllFixingDrone();
    void setStatut(DRONE_STATES states, Drone drone) throws Exception;
}
