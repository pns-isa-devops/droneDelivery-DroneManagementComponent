package fr.unice.polytech.isa.dd;

import fr.unice.polytech.isa.dd.entities.DRONE_STATES;
import fr.unice.polytech.isa.dd.entities.Drone;
import utils.MyDate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Stateless(name="drone-stateless")
public class DroneManagementBean implements AvailableDrone,DroneRegister,DroneStatus {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public DroneStatus status(int idDrone) {
        return null;
    }

    @Override
    public void register( int n_battery, int n_flightHours) throws Exception {
        Drone new_drone= new Drone(n_battery,n_flightHours);
        String localDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        int localTime = LocalTime.now().getHour();
        int localTime1 = LocalTime.now().getMinute();
        String hour = ""+localTime+"h"+localTime1;
        MyDate dt = new MyDate(localDate,hour);
        fr.unice.polytech.isa.dd.entities.DroneStatus status= new fr.unice.polytech.isa.dd.entities.DroneStatus(new_drone,DRONE_STATES.AVAILABLE,dt.getDate());
        entityManager.persist(new_drone);
        entityManager.persist(status);
    }
}
