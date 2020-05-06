package arquillian;

import fr.unice.polytech.isa.dd.DroneRegister;
import fr.unice.polytech.isa.dd.DroneStatusInterface;
import fr.unice.polytech.isa.dd.entities.Drone;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import utils.MyDate;

public abstract class AbstractDroneManagementTest {

    @Deployment
    public static WebArchive createDeployement(){
        return ShrinkWrap.create(WebArchive.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE,"beans.xml")
                .addPackage(MyDate.class.getPackage())
                .addPackage(DroneStatusInterface.class.getPackage())
                .addPackage(Drone.class.getPackage())
                .addPackage(DroneRegister.class.getPackage())
                .addAsManifestResource(new ClassLoaderAsset("META-INF/persistence.xml"), "persistence.xml")
                ;
    }
}
