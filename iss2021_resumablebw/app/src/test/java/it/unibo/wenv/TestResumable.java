package it.unibo.wenv;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import java.until.regex.Patter;

public class TestResumable {

    @Before
    public void systemSetUp() {
        System.out.println("TestResumable | setUp: robot should be at HOME-DOWN ");
        /
    }

    @After
    public void  terminate() {
        System.out.println("%%%  TestResumable |  terminates ");
    }

    @Test
    public void testPath() {
	Object appl =RobotApplicationStarter.createInstance(Resumable.class);
	RobotInputController c = ((Resumable)appl).getController();
	c.resume();
	String mappa= c.getMappa();
	assertTrue(mappa.matches("w+lw+lw+lw+l");
      }

    //@Test
    public void testMoveForwardNoHit() {
     }

    //@Test
    public void testMoveForwardHit() {
      }

}
