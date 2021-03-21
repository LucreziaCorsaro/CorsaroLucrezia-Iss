package it.unibo.wenv;

import it.unibo.annotations.ArilRobotSpec;
import it.unibo.consolegui.ConsoleGui;
import it.unibo.interaction.IssOperations;
import it.unibo.supports.IssCommSupport;
import it.unibo.supports.RobotApplicationStarter;
@ArilRobotSpec
public class Resumable {

    private RobotInputController controller;

    //private ActorRobotObserver actorObs = new ActorRobotObserver();
    //Constructor
    public Resumable(IssOperations rs) {
        IssCommSupport rsComm = (IssCommSupport) rs;
        controller = new RobotInputController(rsComm, true, true);
        rsComm.registerObserver(controller);
        //rsComm.registerObserver( actorObs );
        System.out.println("BoundaryWebsockResumable | CREATED with rsComm=" + rsComm);
    }



    public RobotInputController getController() {
        return this.controller;
    }

    public static void main(String args[]) {
        try {
            System.out.println("BoundaryWebsockResumable | main start n_Threads=" + Thread.activeCount());
            Object appl = RobotApplicationStarter.createInstance(Resumable.class);
            System.out.println("ClientBoundaryWebsockBasicSynch  | appl n_Threads=" + Thread.activeCount());
            new ConsoleGui(((Resumable)appl).getController());
            
            System.out.println("BoundaryWebsockResumable | main end n_Threads=" + Thread.activeCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
