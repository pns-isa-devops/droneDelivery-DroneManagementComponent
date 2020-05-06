package fr.unice.polytech.isa.dd;

import fr.unice.polytech.isa.dd.entities.Drone;

import javax.ejb.Local;
import java.text.ParseException;
import java.util.List;

@Local
public interface DroneRegister {

    Boolean register(String drone_id, String date, String hour) throws ParseException;
    List<Drone> allDrones ();

}
