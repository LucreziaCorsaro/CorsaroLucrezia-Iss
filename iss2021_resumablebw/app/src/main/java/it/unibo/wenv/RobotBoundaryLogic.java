/*
===============================================================
RobotBoundaryLogic.java
implements the business logic  

===============================================================
*/
package it.unibo.wenv;
import it.unibo.interaction.MsgRobotUtil;
import it.unibo.supports.IssCommSupport;
import mapRoomKotlin.mapUtil;

public class RobotBoundaryLogic {
    private IssCommSupport rs ;

private int stepNum              = 1;
private boolean boundaryWalkDone = false ;
private boolean usearil          = false;
private int moveInterval         = 1000;
private RobotMovesInfo robotInfo;
private boolean stop=false;
private boolean escludi = false;
private String mappa="";
    //public enum robotLang {cril, aril}    //todo

    public RobotBoundaryLogic(IssCommSupport support, boolean usearil, boolean doMap){
        rs           = support;
        this.usearil = usearil;
        robotInfo    = new RobotMovesInfo(doMap);
        robotInfo.showRobotMovesRepresentation();
    }

    public void doBoundaryGoon(){
        rs.request( usearil ? MsgRobotUtil.wMsg : MsgRobotUtil.forwardMsg  );
        delay(moveInterval ); //to reduce the robot move rate
    }

    public void setEscludi(boolean escludi){
	this.escludi=escludi;
    }

    public synchronized String doBoundaryInit(){
        System.out.println("RobotBoundaryLogic | doBoundary rs=" + rs + " usearil=" + usearil);
        rs.request( usearil ? MsgRobotUtil.wMsg : MsgRobotUtil.forwardMsg  );
        //The reply to the request is sent by WEnv after the wtime defined in issRobotConfig.txt  
        //delay(moveInterval ); //to reduce the robot move rate
        System.out.println( mapUtil.getMapRep() );
        mappa+="w";
	if(!escludi){
         while( ! boundaryWalkDone ) {
            try {
                System.out.println("prima wait");
                wait();
                System.out.println("dopo wait");
                //System.out.println("RobotBoundaryLogic | RESUMES " );
                rs.close();
             } catch (InterruptedException e) {
                e.printStackTrace();
            }
          }
	}
        return robotInfo.getMovesRepresentationAndClean();
    }
	public String getmappa(){
        	return mappa;
	}

    public void updateMovesRep (String move ){
        mappa+=move;
        robotInfo.updateRobotMovesRepresentation(move);
    }

    public void setStop(boolean b){
        stop=b;
    }
 //Business logic in RobotBoundaryLogic
    protected synchronized void boundaryStep( String move, boolean obstacle ){
        System.out.println("boundaryStep:"+ getmappa());
        if(!stop){
         if (stepNum <= 4) {
            if( move.equals("turnLeft") ){
                updateMovesRep("l");
                //showRobotMovesRepresentation();
                if (stepNum == 4) {
                    boundaryWalkDone=true;
                    notify(); //to resume the main
                    return;
                }
                stepNum++;
                doBoundaryGoon();
                return;
            }
            //the move is moveForward
            if( obstacle ){
                rs.request( usearil ? MsgRobotUtil.lMsg : MsgRobotUtil.turnLeftMsg   );
                delay(moveInterval ); //to reduce the robot move rate
            }
            if( ! obstacle ){
                updateMovesRep("w");
                doBoundaryGoon();
            }
            robotInfo.showRobotMovesRepresentation();
        }else{ //stepNum > 4
            System.out.println("RobotBoundaryLogic | boundary ENDS"  );
        }}
    }

    protected void delay( int dt ){
        try { Thread.sleep(dt); } catch (InterruptedException e) { e.printStackTrace(); }
    }

}
