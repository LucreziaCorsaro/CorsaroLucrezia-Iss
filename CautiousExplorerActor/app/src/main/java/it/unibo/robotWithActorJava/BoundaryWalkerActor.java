package it.unibo.robotWithActorJava;

import it.unibo.supports2021.ActorBasicJava;
import it.unibo.supports2021.IssWsHttpJavaSupport;
import org.json.JSONObject;

import java.util.Random;

public class BoundaryWalkerActor extends ActorBasicJava {
    final String forwardMsg   = "{\"robotmove\":\"moveForward\", \"time\": 350}";
    final String backwardMsg  = "{\"robotmove\":\"moveBackward\", \"time\": 350}";
    final String turnLeftMsg  = "{\"robotmove\":\"turnLeft\", \"time\": 300}";
    final String turnRightMsg = "{\"robotmove\":\"turnRight\", \"time\": 300}";
    final String haltMsg      = "{\"robotmove\":\"alarm\", \"time\": 100}";

    private enum State {start, walking, obstacle, end,stop };
    private IssWsHttpJavaSupport support;
    private State curState       =  State.start ;
    private int stepNum          = 1;
    private RobotMovesInfo moves = new RobotMovesInfo(false);
    private boolean ostacolo = false;
    private boolean firstSonar= true;
    public BoundaryWalkerActor(String name, IssWsHttpJavaSupport support) {
        super(name);
        this.support = support;
    }
/*
//Removed since we want use just the fsm, without any 'external' code
    public void reset(){
        System.out.println("RobotBoundaryLogic | FINAL MAP:"  );
        moves.showRobotMovesRepresentation();
        stepNum        = 1;
        curState       =  State.start;
        moves.getMovesRepresentationAndClean();
        moves.showRobotMovesRepresentation();
    }
*/

    protected void fsm(String move, String endmove) {
        System.out.println(myname + " | fsm state=" + curState + " stepNum=" + stepNum + " move=" + move + " endmove=" + endmove);
        firstSonar=true;
        switch (curState) {
            case start: {
                moves.showRobotMovesRepresentation();
                generaMossa();
                curState = State.walking;
                break;
            }
            case walking: {
                if (endmove.equals("false")) {
                    System.out.println("trovato ostacolo");
                    curState = State.obstacle;
                    ritornoTana(moves.getMovesRepresentation());
                } else {
                    generaMossa();
                }
                break;
            }
            case obstacle: {
                if (endmove.equals("false")) {
                    System.out.println("TANA");
                    curState = State.end;
                }
                break;
            }
            case end: {
                System.out.println("BOUNDARY WALK END");
                moves.showRobotMovesRepresentation();
                System.exit(0);
                break;
            }
            default:
                break;
        }
    }
            /*
            case start: {
                moves.showRobotMovesRepresentation();
                doStep();
                curState = State.walking;
                break;
            }
            case stop:{
                System.out.println("PREMUTO STOP");
                break;
            }
            case walking: {
                if (move.equals("moveForward") && endmove.equals("true")) {
                    //curState = State.walk;
                    moves.updateMovesRep("w");
                    doStep();
                 } else if (move.equals("moveForward") && endmove.equals("false")) {
                    curState = State.obstacle;
                    turnLeft();
                } else {System.out.println("IGNORE answer of turnLeft");
                }
                break;
            }//walk

            case obstacle :
                if( move.equals("turnLeft") && endmove.equals("true")) {
                    if( stepNum < 4) {
                        stepNum++;
                        moves.updateMovesRep("l");
                        moves.showRobotMovesRepresentation();
                        curState = State.walking;
                        doStep();
                    }else{  //at home again
                        curState = State.end;
                        turnLeft(); //to force state transition
                    }
                } break;

            case end : {
                if( move.equals("turnLeft") ) {
                    System.out.println("BOUNDARY WALK END");
                    moves.showRobotMovesRepresentation();
                    turnRight();    //to compensate last turnLeft
                }else{
                    //reset();
                    stepNum        = 1;
                    curState       =  State.start;
                    moves.getMovesRepresentationAndClean();
                }
                break;
            }
            default: {
                System.out.println("error - curState = " + curState);
            }
        }
    }*/

    private void ritornoTana(String mossa){
        String move= "";
        if(!ostacolo){
            ostacolo=true;
            move=mossa;
        }
        for(int i=move.length()-1; i>=0; i--){
            switch(move.charAt(i)){
                case 'w':{
                    goBack();
                    break;
                }
                case 'l':
                    turnRight();
                    break;
                case 'r':
                    turnLeft();
                    break;
                case 's':
                    doStep();
                    break;
                default:
                    break;
            }
        }
    }
private void generaMossa(){
        Random random = new Random();
        int num = random.nextInt(4);
        switch(num){
            case 0: {
                moves.updateMovesRep("w");
                doStep();
                break;
            }
            case 1: {
                moves.updateMovesRep("l");
                turnLeft();
                break;
            }
            case 2: {
                moves.updateMovesRep("s");
                goBack();
                break;
            }
            case 3: {
                moves.updateMovesRep("r");
                turnRight();
                break;
            }
            default:
                break;
        }
 }

    @Override
    protected void handleInput(String msg ) {     //called when a msg is in the queue
        //System.out.println( name + " | input=" + msgJsonStr);
        if( msg.equals("startApp"))  fsm("","");
        else msgDriven( new JSONObject(msg) );
    }

    protected void msgDriven( JSONObject infoJson){
        if( infoJson.has("endmove") )        fsm(infoJson.getString("move"), infoJson.getString("endmove"));
        else if( infoJson.has("sonarName") ) handleSonar(infoJson);
        else if( infoJson.has("collision") ) handleCollision(infoJson);
        else if( infoJson.has("robotcmd") )  handleRobotCmd(infoJson);
    }

    protected void handleSonar( JSONObject sonarinfo ){
        if(firstSonar) {
            firstSonar=false;
            delay(2000);
        }
        String sonarname = (String)  sonarinfo.get("sonarName");
        int distance     = (Integer) sonarinfo.get("distance");
        //System.out.println("RobotApplication | handleSonar:" + sonarname + " distance=" + distance);
    }
    protected void handleCollision( JSONObject collisioninfo ){
        //we should handle a collision  when there are moving obstacles
        //in this case we could have a collision even if the robot does not move
        //String move   = (String) collisioninfo.get("move");
        //System.out.println("RobotApplication | handleCollision move=" + move  );
    }
  
    protected void handleRobotCmd( JSONObject robotCmd ){
        String cmd = (String)  robotCmd.get("robotcmd");
        if(cmd.equals("STOP")){
            curState =State.stop;
        }else if(cmd.equals("RESUME")) {
            curState=State.walking;
            doStep();
        }
        System.out.println("===================================================="    );
        System.out.println("RobotApplication | handleRobotCmd cmd=" + cmd  );
        System.out.println("===================================================="    );
    }

    //------------------------------------------------
    protected void doStep(){
        support.forward( forwardMsg);
        delay(1000); //to avoid too-rapid movement
    }
    protected void goBack(){
        support.forward( backwardMsg);
        delay(1000); //to avoid too-rapid movement
    }
    protected void turnLeft(){
        support.forward( turnLeftMsg );
        delay(500); //to avoid too-rapid movement
    }
    protected void turnRight(){
        support.forward( turnRightMsg );
        delay(500); //to avoid too-rapid movement
    }

}
