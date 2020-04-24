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

    HashMap<Drone, DroneStatus> all_drone_status = new HashMap<>();
    HashMap<Drone, DroneStatus> all_drone_loanding = new HashMap<>();
    HashMap<Drone, DroneStatus> all_drone_fixing = new HashMap<>();



    @Override
    public List<DroneStatus> getAllHistoryDrone(String idDrone) {
        List<DroneStatus> list_history = finddronestatus(idDrone);
        return  list_history;
    }

    @Override
    public HashMap<Drone, DroneStatus> getallstatus() {
        List<DroneStatus> tout_status= get_allStatus();
        for (DroneStatus d : tout_status) {
            DRONE_STATES gv=d.getLibelleStatusDrone();
            if (gv==DRONE_STATES.AVAILABLE){
                Drone f = findById(d.getDrone().getDroneId());
                int size = f.getStatusDrone().size();
                DroneStatus st= f.getStatusDrone().get(size);
                this.all_drone_status.put(f,st);
            }
        }
        return all_drone_status;
    }

    @Override
    public HashMap<Drone, DroneStatus> getAllLoadingDrone() {
        List<DroneStatus> tout_status= get_allStatus();
        for (DroneStatus d : tout_status) {
            DRONE_STATES gv=d.getLibelleStatusDrone();
            if (gv==DRONE_STATES.IN_LOADING){
                Drone f = findById(d.getDrone().getDroneId());
                int size = f.getStatusDrone().size();
                DroneStatus st= f.getStatusDrone().get(size);
                this.all_drone_loanding.put(f,st);
            }
        }
        return all_drone_loanding;
    }

    @Override
    public HashMap<Drone, DroneStatus> getAllFixingDrone() {
        List<DroneStatus> tout_status= get_allStatus();
        for (DroneStatus d : tout_status) {
            DRONE_STATES gv=d.getLibelleStatusDrone();
            if (gv==DRONE_STATES.BEING_REPAIRED){
                Drone f = findById(d.getDrone().getDroneId());
                int size = f.getStatusDrone().size();
                DroneStatus st= f.getStatusDrone().get(size);
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
        DroneStatus status= new DroneStatus(new_drone,states,dt.toString());
        new_drone.addStatut(status);
        entityManager.persist(status);
    }

    @Override
    public Boolean register( int n_battery, int n_flightHours, String id) throws Exception {
        Optional<Drone> d = finddrone(id);
        if(d.isPresent()) return false;
        Drone new_drone= new Drone(n_battery,n_flightHours, id);
        System.out.println("okki 1");
        String localDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        int localTime = LocalTime.now().getHour();
        int localTime1 = LocalTime.now().getMinute();
        String hour = ""+localTime+"h"+localTime1;
        MyDate dt = new MyDate(localDate,hour);
        DroneStatus status= new DroneStatus(new_drone,DRONE_STATES.AVAILABLE,dt.toString());
        System.out.println("okki 2");

        entityManager.persist(status);
        System.out.println("okki 3");

        new_drone.addStatut(status);
        entityManager.persist(new_drone);

        System.out.println("okki 4");

        //entityManager.persist(new_drone);
        return true;
    }

    @Override
    public List<Drone> getAllDroneAvailable() {
        List<DroneStatus> tout_status= get_allStatus();
        List<Drone> availables = null;
        for (DroneStatus d : tout_status) {
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

    public List<DroneStatus> finddronestatus(String iddrone) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<DroneStatus> criteria = builder.createQuery(DroneStatus.class);
        Root<DroneStatus> root =  criteria.from(DroneStatus.class);
        criteria.select(root).where(builder.equal(root.get("droneId"), iddrone));
        TypedQuery<DroneStatus> query = entityManager.createQuery(criteria);
        try {
            List<DroneStatus> toReturn = new ArrayList<>(query.getResultList());
            return Optional.of(toReturn).get();
        } catch (NoResultException nre){
            return null;
        }
    }

    public List<DroneStatus> get_allStatus(){
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<DroneStatus> criteria = builder.createQuery(DroneStatus.class);
        Root<DroneStatus> root =  criteria.from(DroneStatus.class);
        criteria.select(root);
        TypedQuery<DroneStatus> query = entityManager.createQuery(criteria);
        try {
            List<DroneStatus> toReturn = new ArrayList<>(query.getResultList());
            return Optional.of(toReturn).get();
        } catch (NoResultException nre){
            return null;
        }
    }
}
