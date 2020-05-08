package fr.unice.polytech.isa.dd;

import fr.unice.polytech.isa.dd.entities.DRONE_STATES;
import fr.unice.polytech.isa.dd.entities.DroneStatus;
import fr.unice.polytech.isa.dd.entities.Drone;
import utils.MyDate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Stateless(name="drone-stateless")
public class DroneManagementBean implements AvailableDrone,DroneRegister, DroneStatusInterface {

    @PersistenceContext
    private EntityManager entityManager;

    HashMap<Drone, DroneStatus> last_drone_status = new HashMap<>();

    @Override
    public List<DroneStatus> historyDrone(String idDrone) {
        return findDroneById(idDrone).getStatusDrone();
    }

    @Override
    public HashMap<Drone, DroneStatus> lastStatusDrone() {
        List<Drone> alldrone = allDrones();
        for (Drone drone:alldrone) {
            int size = drone.getStatusDrone().size();
            last_drone_status.put(drone,drone.getStatusDrone().get(size - 1));
        }
        return last_drone_status;
    }

    @Override
    public void changeStatus(DRONE_STATES states, Drone drone, String date, String hour) throws java.text.ParseException {
        Drone new_drone = findDroneById(drone.getDroneId());
        MyDate dt = new MyDate(date,hour);
        DroneStatus status= new DroneStatus(states,dt.toString());
        if(states == DRONE_STATES.AVAILABLE) new_drone.setBattery(0.0);
        if(states == DRONE_STATES.BEING_REPAIRED) new_drone.setFlightHours(0.0);
        new_drone.addStatut(status);
        entityManager.persist(new_drone);
    }

    @Override
    public void UpdtateAttributsDrone(Drone drone, double batteryUsed, String date, String hour) throws ParseException {
        Drone new_drone = findDroneById(drone.getDroneId());
        new_drone.reduceBatteryLife(batteryUsed);
        double flighthoursdone = new_drone.getFlightHours();
        double batteryLife = new_drone.getBatteryLife();
        MyDate dt = new MyDate(date,hour);
        if(batteryLife < 0.45) new_drone.addStatut(new DroneStatus(DRONE_STATES.IN_LOADING, dt.toString()));
        if(flighthoursdone >= 21) new_drone.addStatut(new DroneStatus(DRONE_STATES.BEING_REPAIRED, dt.toString()));
        entityManager.persist(new_drone);
    }

    @Override
    public Boolean register(String drone_id, String date, String hour) throws java.text.ParseException {
        Optional<Drone> d = finddroneByIdInDatabase(drone_id);
        if(d.isPresent()) return false;
        Drone new_drone= new Drone(drone_id);
        MyDate myDate = new MyDate(date, hour);
        DroneStatus status= new DroneStatus(DRONE_STATES.AVAILABLE,myDate.toString());
        new_drone.addStatut(status);
        entityManager.persist(new_drone);
        return true;
    }

    @Override
    public List<Drone> allDrones(){
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Drone> criteria = builder.createQuery(Drone.class);
        Root<Drone> root =  criteria.from(Drone.class);
        criteria.select(root);
        TypedQuery<Drone> query = entityManager.createQuery(criteria);
        try {
            List<Drone> toReturn = new ArrayList<>(query.getResultList());
            return Optional.of(toReturn).get();
        } catch (NoResultException nre){
            return null;
        }
    }

    @Override
    public List<Drone> allDroneAvailable() {
        List<Drone> availables = new ArrayList<>();
        for( Map.Entry<Drone, DroneStatus> entry : lastStatusDrone().entrySet() ){
            if(entry.getValue().getLibelleStatusDrone().equals(DRONE_STATES.AVAILABLE)) availables.add(entry.getKey());
        }
        return availables;
    }

    private Drone findDroneById(String iddrone) {
        Optional<Drone> drone =  finddroneByIdInDatabase(iddrone);
        return drone.orElse(null);
    }

    private Optional<Drone> finddroneByIdInDatabase(String iddrone) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Drone> criteria = builder.createQuery(Drone.class);
        Root<Drone> root =  criteria.from(Drone.class);
        criteria.select(root).where(builder.equal(root.get("droneId"), iddrone));
        TypedQuery<Drone> query = entityManager.createQuery(criteria);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException nre){
            return Optional.empty();
        }
    }
}
