package fr.unice.polytech.isa.dd;

import arquillian.AbstractDroneManagementTest;
import fr.unice.polytech.isa.dd.entities.DRONE_STATES;
import fr.unice.polytech.isa.dd.entities.Drone;
import fr.unice.polytech.isa.dd.entities.DroneStatus;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.text.ParseException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
@Transactional(TransactionMode.COMMIT)
public class DroneRegisterTest extends AbstractDroneManagementTest {

    @PersistenceContext private EntityManager entityManager;
    @EJB(name = "drone-stateless")
    DroneRegister droneRegister;
    @EJB(name = "drone-stateless")
    DroneStatusInterface droneStatusInterface;

    private Drone drone = new Drone("2");
    private Drone drone2 = new Drone("Kalachnikov");
    @Test
    public void registerTest() throws Exception {
        droneRegister.register("1","06/06/2020","10h00");
        assertEquals(1,droneRegister.allDrones().size());
        Drone drone = droneRegister.allDrones().get(0);
        assertEquals(1,drone.getStatusDrone().size());
    }

    @Test
    public void setStatusTest() throws Exception {
        drone.addStatut(new DroneStatus(DRONE_STATES.AVAILABLE,"14/04/2020 10h00"));
        entityManager.persist(drone);
        drone = entityManager.find(Drone.class,drone.getId());
        assertEquals(2,droneRegister.allDrones().size());
        droneStatusInterface.changeStatus(DRONE_STATES.IN_DELIVERING,drone,"14/06/2020","15h00");
        assertEquals(DRONE_STATES.IN_DELIVERING,drone.getStatusDrone().get(1).getLibelleStatusDrone());
        HashMap<Drone,DroneStatus> laststatusdrones = droneStatusInterface.lastStatusDrone();
        DRONE_STATES drone_states = laststatusdrones.get(drone).getLibelleStatusDrone();
        assertEquals(DRONE_STATES.IN_DELIVERING,drone_states);
    }

    @Test
    public void updateandchangestatusTest() throws ParseException {
        drone2.addStatut(new DroneStatus(DRONE_STATES.AVAILABLE,"14/04/2020 10h00"));
        entityManager.persist(drone2);
        drone2 = entityManager.find(Drone.class,drone2.getId());
        droneStatusInterface.UpdtateAttributsDrone(drone2,0.45,"14/04/2020","10h45");
        HashMap<Drone,DroneStatus> laststatusdrones = droneStatusInterface.lastStatusDrone();
        DRONE_STATES drone_states = laststatusdrones.get(drone2).getLibelleStatusDrone();
        assertEquals(DRONE_STATES.AVAILABLE,drone_states);
        for(int i = 0; i < 26; i++){
            droneStatusInterface.UpdtateAttributsDrone(drone2,0.45,"14/04/2020","10h45");
        }
        laststatusdrones = droneStatusInterface.lastStatusDrone();
        drone_states = laststatusdrones.get(drone2).getLibelleStatusDrone();
        assertEquals(DRONE_STATES.IN_LOADING,drone_states);
        droneStatusInterface.changeStatus(DRONE_STATES.AVAILABLE,drone2,"14/04/2020","10h45");
        for(int i = 0; i <20; i++){
            droneStatusInterface.UpdtateAttributsDrone(drone2,0.45,"14/04/2020","10h45");
        }
        laststatusdrones = droneStatusInterface.lastStatusDrone();
        drone_states = laststatusdrones.get(drone2).getLibelleStatusDrone();
        assertEquals(DRONE_STATES.BEING_REPAIRED,drone_states);
    }
}
