package fr.unice.polytech.isa.dd;

import javax.ejb.Local;

@Local
public interface DroneRegister {
    Boolean register(int n_battery, int  n_flightHours, String id) throws Exception;
}
