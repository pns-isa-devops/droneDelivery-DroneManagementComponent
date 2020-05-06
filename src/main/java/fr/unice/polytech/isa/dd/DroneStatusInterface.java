package fr.unice.polytech.isa.dd;

import fr.unice.polytech.isa.dd.entities.DRONE_STATES;
import fr.unice.polytech.isa.dd.entities.Drone;
import fr.unice.polytech.isa.dd.entities.DroneStatus;

import javax.ejb.Local;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

@Local
public interface DroneStatusInterface {
    List<DroneStatus> historyDrone (String idDrone);
    HashMap<Drone, DroneStatus> lastStatusDrone();
    void changeStatus(DRONE_STATES states, Drone drone, String date, String hour) throws ParseException;
}
