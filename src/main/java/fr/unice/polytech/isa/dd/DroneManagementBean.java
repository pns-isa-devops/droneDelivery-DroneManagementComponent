package fr.unice.polytech.isa.dd;

import fr.unice.polytech.isa.dd.entities.*;
import fr.unice.polytech.isa.dd.entities.DroneStatus;
import utils.MyDate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Stateless(name="drone-stateless")
public class DroneManagementBean implements AvailableDrone,DroneRegister, dronestatus {

    @PersistenceContext
    private EntityManager entityManager;

    HashMap<Drone, fr.unice.polytech.isa.dd.entities.DroneStatus> all_drone_status = new HashMap<>();
    HashMap<Drone, fr.unice.polytech.isa.dd.entities.DroneStatus> all_drone_loanding = new HashMap<>();
    HashMap<Drone, fr.unice.polytech.isa.dd.entities.DroneStatus> all_drone_fixing = new HashMap<>();



    @Override
    public List<fr.unice.polytech.isa.dd.entities.DroneStatus> getAllHistoryDrone(String idDrone) {
        List<fr.unice.polytech.isa.dd.entities.DroneStatus> list_history = finddronestatus(idDrone);
        return  list_history;
    }

    @Override
    public HashMap<Drone, fr.unice.polytech.isa.dd.entities.DroneStatus> getallstatus() {
        List<fr.unice.polytech.isa.dd.entities.DroneStatus> tout_status= get_allStatus();
        for (fr.unice.polytech.isa.dd.entities.DroneStatus d : tout_status) {
            DRONE_STATES gv=d.getLibelleStatusDrone();
            if (gv==DRONE_STATES.AVAILABLE){
                Drone f = findById(d.getDrone().getDroneId());
                int size = f.getStatusDrone().size();
                fr.unice.polytech.isa.dd.entities.DroneStatus st= f.getStatusDrone().get(size);
                this.all_drone_status.put(f,st);
            }
        }
        return all_drone_status;
    }

    @Override
    public HashMap<Drone, fr.unice.polytech.isa.dd.entities.DroneStatus> getAllLoadingDrone() {
        List<fr.unice.polytech.isa.dd.entities.DroneStatus> tout_status= get_allStatus();
        for (fr.unice.polytech.isa.dd.entities.DroneStatus d : tout_status) {
            DRONE_STATES gv=d.getLibelleStatusDrone();
            if (gv==DRONE_STATES.IN_LOADING){
                Drone f = findById(d.getDrone().getDroneId());
                int size = f.getStatusDrone().size();
                fr.unice.polytech.isa.dd.entities.DroneStatus st= f.getStatusDrone().get(size);
                this.all_drone_loanding.put(f,st);
            }
        }
        return all_drone_loanding;
    }

    @Override
    public HashMap<Drone, fr.unice.polytech.isa.dd.entities.DroneStatus> getAllFixingDrone() {
        List<fr.unice.polytech.isa.dd.entities.DroneStatus> tout_status= get_allStatus();
        for (fr.unice.polytech.isa.dd.entities.DroneStatus d : tout_status) {
            DRONE_STATES gv=d.getLibelleStatusDrone();
            if (gv==DRONE_STATES.BEING_REPAIRED){
                Drone f = findById(d.getDrone().getDroneId());
                int size = f.getStatusDrone().size();
                fr.unice.polytech.isa.dd.entities.DroneStatus st= f.getStatusDrone().get(size);
                this.all_drone_fixing.put(f,st);
            }
        }
        return all_drone_fixing;
    }

    @Override
    public void setStatut(DRONE_STATES states, Drone drone) throws Exception {
        Drone new_drone = findById(drone.getDroneId());
        String localDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        int localTime = LocalTime.now().getHour();
        int localTime1 = LocalTime.now().getMinute();
        String hour = ""+localTime+"h"+localTime1;
        MyDate dt = new MyDate(localDate,hour);
        fr.unice.polytech.isa.dd.entities.DroneStatus status= new fr.unice.polytech.isa.dd.entities.DroneStatus(new_drone,states,dt.toString());
        new_drone.addStatut(status);
        entityManager.persist(status);
    }

    @Override
    public Boolean register( int n_battery, int n_flightHours, String id) throws Exception {
        Optional<Drone> d = finddrone(id);
        if(d.isPresent()) return false;
        Drone new_drone= new Drone(n_battery,n_flightHours, id);
        String localDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        int localTime = LocalTime.now().getHour();
        int localTime1 = LocalTime.now().getMinute();
        String hour = ""+localTime+"h"+localTime1;
        MyDate dt = new MyDate(localDate,hour);
        fr.unice.polytech.isa.dd.entities.DroneStatus status= new fr.unice.polytech.isa.dd.entities.DroneStatus(new_drone,DRONE_STATES.AVAILABLE,dt.toString());
        new_drone.addStatut(status);
        entityManager.persist(new_drone);
        entityManager.persist(status);
        return true;
    }

    @Override
    public List<Drone> getAllDroneAvailable() {
        List<fr.unice.polytech.isa.dd.entities.DroneStatus> tout_status= get_allStatus();
        List<Drone> availables = null;
        for (fr.unice.polytech.isa.dd.entities.DroneStatus d : tout_status) {
            DRONE_STATES gv=d.getLibelleStatusDrone();
            if (gv==DRONE_STATES.AVAILABLE){
                Drone f = findById(d.getDrone().getDroneId());
                availables.add(f);
            }
        }
        return availables;
    }

    public Drone findById(String iddrone) {
        Optional<Drone> drone =  finddrone(iddrone);
        return drone.orElse(null);
    }

    public Optional<Drone> finddrone(String iddrone) {
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

    public List<fr.unice.polytech.isa.dd.entities.DroneStatus> finddronestatus(String iddrone) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<fr.unice.polytech.isa.dd.entities.DroneStatus> criteria = builder.createQuery(fr.unice.polytech.isa.dd.entities.DroneStatus.class);
        Root<fr.unice.polytech.isa.dd.entities.DroneStatus> root =  criteria.from(fr.unice.polytech.isa.dd.entities.DroneStatus.class);
        criteria.select(root).where(builder.equal(root.get("droneId"), iddrone));
        TypedQuery<fr.unice.polytech.isa.dd.entities.DroneStatus> query = entityManager.createQuery(criteria);
        try {
            List<fr.unice.polytech.isa.dd.entities.DroneStatus> toReturn = new ArrayList<>(query.getResultList());
            return Optional.of(toReturn).get();
        } catch (NoResultException nre){
            return null;
        }
    }

    public List<fr.unice.polytech.isa.dd.entities.DroneStatus> get_allStatus(){
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<fr.unice.polytech.isa.dd.entities.DroneStatus> criteria = builder.createQuery(fr.unice.polytech.isa.dd.entities.DroneStatus.class);
        Root<fr.unice.polytech.isa.dd.entities.DroneStatus> root =  criteria.from(fr.unice.polytech.isa.dd.entities.DroneStatus.class);
        criteria.select(root);
        TypedQuery<fr.unice.polytech.isa.dd.entities.DroneStatus> query = entityManager.createQuery(criteria);
        try {
            List<DroneStatus> toReturn = new ArrayList<>(query.getResultList());
            return Optional.of(toReturn).get();
        } catch (NoResultException nre){
            return null;
        }
    }
}
